package com.example.qianfangdemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.Utils.Utils;
import com.example.qianfangdemo.base.ConstValue;
import com.example.qianfangdemo.fragment.HomeFragment;
import com.example.qianfangdemo.fragment.MyInfoFragment;
import com.example.qianfangdemo.view.DirectionalViewPager;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qfpay.sdk.activity.CashierActivity;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.QTConst;
import com.qfpay.sdk.common.QTPayCommon;
import com.qfpay.sdk.entity.ExtraInfo;
import com.qfpay.sdk.entity.Good;
import com.qfpay.sdk.entity.QTHolder;

public class HomeActivity extends FragmentActivity {

	@ViewInject(R.id.pager)
	private DirectionalViewPager viewPager;

	private boolean isLoading;
	private Dialog dialog;

	private int specialAmt;

	private String mTotalAmt = "1";

	private Fragment homeFragment, InfoFragment;

	List<Good> goods;
	List<ExtraInfo> extraInfo;

	protected RequestQueue mQueue;
	protected QTPayCommon mqt;

	/**
	 * 外部订单号
	 */
	public String mOutSn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_page);
		ViewUtils.inject(this);

		mQueue = Volley.newRequestQueue(getApplicationContext());
		mqt = QTPayCommon.getInstance(getApplicationContext());

		specialAmt = getIntent().getIntExtra("totalAmt", -1);
		if (specialAmt < 0) {
			finish();
			return;
		}

		dialog = new Dialog(HomeActivity.this, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog.setCancelable(false);

		initView();

		homeFragment = new HomeFragment();
		InfoFragment = new MyInfoFragment();

		getSettingConfig();

	}

	private void initView() {
		viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
		viewPager.setOrientation(DirectionalViewPager.VERTICAL);
	}

	private class MyFragmentAdapter extends FragmentPagerAdapter {

		public MyFragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return homeFragment;
			case 1:
				return InfoFragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

	}

	/**
	 * 预下单
	 */
	public void preCreateOrder() {

		goods = new ArrayList<Good>();
		Good good1 = new Good("大象", 1, specialAmt);
		Good good2 = new Good("辛太急", 2, 0);
		goods.add(good1);
		goods.add(good2);

		extraInfo = new ArrayList<ExtraInfo>();
		ExtraInfo mobile = new ExtraInfo("电话", "186********");
		ExtraInfo address = new ExtraInfo("地址", "望京SOHO 塔3 A17层");
		extraInfo.add(address);
		extraInfo.add(mobile);

		int totalAmount = 0;
		for (Good good : goods) {
			totalAmount += good.good_amount * good.good_count;
		}
		mTotalAmt = totalAmount + "";

		if (!Utils.isCanConnectionNetWork(HomeActivity.this)) {
			Toast.makeText(HomeActivity.this, "网络连接异常！", Toast.LENGTH_SHORT);
			return;
		}

		if (isLoading) {
			return;
		}

		if (null != dialog) {
			dialog.show();
			isLoading = true;
		}

		StringRequest req = new StringRequest(Method.POST, ConstValue.domain + "/ordertoken", new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					if (dialog.isShowing()) {
						dialog.dismiss();
						isLoading = false;
					}

					T.d("return => " + arg0);

					JSONObject tokenJson = new JSONObject(arg0);
					ConstValue.orderToken = tokenJson.getString("token");

					mOutSn = ConstValue.orderToken;
					mqt.setOutOrderToken(ConstValue.orderToken);

					// 跳转到下单页面
					// doJump();
					JumpTo();

				} catch (Exception e) {
					if (dialog.isShowing()) {
						dialog.dismiss();
						isLoading = false;
					}
					T.d(e.toString());
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialog.isShowing()) {
					dialog.dismiss();
					isLoading = false;
				}
				Toaster.show(HomeActivity.this, "请求失败！");
				T.d(arg0.toString());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("total_amt", mTotalAmt);

				return params;
			}

		};
		mQueue.add(req);
	}

	/**
	 * 请求配置参数
	 */
	private void getSettingConfig() {

		if (!Utils.isCanConnectionNetWork(HomeActivity.this)) {
			Toast.makeText(HomeActivity.this, "网络连接异常！", Toast.LENGTH_SHORT);
			return;
		}

		if (dialog != null) {
			dialog.show();
		}

		mqt.getSettingConfiguration(new QTCallBack() {
			@Override
			public void onSuccess(Map<String, Object> dataMap) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				ConstValue.paymentType = dataMap;
			}

			@Override
			public void onError(Map<String, String> errorInfo) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this).setMessage("获取请求参数失败").setPositiveButton("退出", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				}).setNegativeButton("重试", new OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						getSettingConfig();
					}
				}).show();
				dialog.setCancelable(false);

				Toast.makeText(HomeActivity.this, "请求配置参数失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void JumpTo() {

		mqt.queryRule("", new QTCallBack() {

			@Override
			public void onSuccess(Map<String, Object> dataInfo) {

			}

			@Override
			public void onError(Map<String, String> errorInfo) {

			}
		});

		Intent intent = new Intent(HomeActivity.this, CashierActivity.class);
		intent.putExtra(QTConst.EXTRO, new Gson().toJson(new QTHolder(QTConst.ServerType_PAY, Integer.valueOf(mTotalAmt), goods, extraInfo)));
		startActivityForResult(intent, ConstValue.REQUEST_FOR_CASHIER);
		overridePendingTransition(R.anim.qt_slide_in_from_bottom, R.anim.qt_slide_out_to_top);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		T.d("set成功！");

		if (requestCode == ConstValue.REQUEST_FOR_CASHIER) {

			if (resultCode == Activity.RESULT_OK) {
				int result = data.getExtras().getInt("pay_result");

				T.d("result = " + result);
				switch (result) {
				case QTConst.PAYMENT_RETURN_SUCCESS:
					startActivity(new Intent(HomeActivity.this, PaymentResultActivity.class));
					break;

				case QTConst.PAYMENT_RETURN_FAIL:
					break;
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				T.d("取消");

			} else if (resultCode == QTConst.ACTIVITY_RETURN_ERROR) {
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

}
