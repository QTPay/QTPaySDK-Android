package com.example.qianfangdemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import qfpay.wxshop.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.Utils.Utils;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.ConstValue;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.common.QTConst;
import com.qfpay.sdk.entity.ExtraInfo;
import com.qfpay.sdk.entity.Good;
import com.qfpay.sdk.entity.QTHolder;

public class MainActivity extends BaseActivity {

	private ArrayList<Good> goods;
	private ArrayList<ExtraInfo> extraInfo;
	private String mTotalAmt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

	}

	@OnClick(R.id.order_one)
	private void onCreateOrderClick(View view) {
		itemClick(1);
	}

	@OnClick(R.id.order_two)
	private void onRechargeClick(View view) {
		itemClick(2);
	}

	@OnClick(R.id.order_three)
	private void onPrePayClick(View view) {
		itemClick(3);
	}

	private void itemClick(int amt) {
		// 预下单
		preCreateOrder(amt);
	}

	/**
	 * 预下单
	 */
	private void preCreateOrder(final int amt) {

		goods = new ArrayList<Good>();
		Good good1 = new Good("大象", 1, amt, "专业的套套");
		Good good2 = new Good("辛太急", 2, 0, "");
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

		if (!Utils.isCanConnectionNetWork(MainActivity.this)) {
			Toast.makeText(MainActivity.this, "网络连接异常！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (null != dialog) {
			dialog.show();
		}

		StringRequest req = new StringRequest(Method.POST, App.getDomain() + "/ordertoken", new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}

					T.d("return => " + arg0);

					JSONObject tokenJson = new JSONObject(arg0);
					ConstValue.orderToken = tokenJson.getString("token");

					mqt.setOutOrderToken(ConstValue.orderToken);
					// 跳转到下单页面
					jumpToCashier(mTotalAmt);

				} catch (Exception e) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					T.d(e.toString());
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toaster.show(MainActivity.this, "请求失败！");
				T.d(arg0.toString());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("total_amt", mTotalAmt);
				params.put("out_mchnt", App.getOutMerchant());
				return params;
			}

		};
		mQueue.add(req);
	}

	private void jumpToCashier(String amt) {
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		intent.putExtra(QTConst.EXTRO, new QTHolder(QTConst.ServerType_PAY, Integer.valueOf(amt), goods, extraInfo,
				ConstValue.mobile));
		startActivity(intent);
	}

	@OnClick(R.id.my_account)
	private void onAccountClick(View view) {
		startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
	}

}
