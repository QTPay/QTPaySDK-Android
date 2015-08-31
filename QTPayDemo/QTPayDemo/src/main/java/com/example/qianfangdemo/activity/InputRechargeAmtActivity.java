package com.example.qianfangdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.qianfangdemo.view.ClearEditText;
import com.qfpay.sdk.activity.CashierActivity;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.QTConst;
import com.qfpay.sdk.entity.ExtraInfo;
import com.qfpay.sdk.entity.Good;
import com.qfpay.sdk.entity.QTHolder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.R;

public class InputRechargeAmtActivity extends BaseActivity implements View.OnClickListener{

	private ClearEditText editRecharge;

	private Button btnRecharge;

	private String mAmt;
	private Dialog dialog;

	List<Good> goods;
	List<ExtraInfo> extraInfo;

	int rechargeAmt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_recharge_amt);

		editRecharge = (ClearEditText) findViewById(R.id.input_recharge);
		btnRecharge = (Button) findViewById(R.id.recharge);
		dialog = new Dialog(InputRechargeAmtActivity.this, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog.setCancelable(false);

		TextWatcher watcher = new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
				// 检查商户的登录按钮是否可以点击
				checkRechargeAmt();
			}
		};
		editRecharge.addTextChangedListener(watcher);

		findViewById(R.id.back).setOnClickListener(this);
		btnRecharge.setOnClickListener(this);
	}

	/**
	 * 请求配置参数
	 */
	private void getSettingConfig() {

		if (!Utils.isCanConnectionNetWork(InputRechargeAmtActivity.this)) {
			Toast.makeText(InputRechargeAmtActivity.this, "网络连接异常！", Toast.LENGTH_SHORT);
			return;
		}

		if (dialog != null) {
			dialog.show();
		}

		mqt.getSettingConfiguration(ConstValue.orderToken, new QTCallBack() {
			@Override
			public void onSuccess(Map<String, Object> dataMap) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				ConstValue.paymentType = dataMap;
				doJump();
			}

			@Override
			public void onError(Map<String, String> errorInfo) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}

				AlertDialog dialog = new AlertDialog.Builder(InputRechargeAmtActivity.this).setMessage("获取请求参数失败")
						.setPositiveButton("退出", new OnClickListener() {
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

				Toast.makeText(InputRechargeAmtActivity.this, "请求配置参数失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 点击余额充值按钮
	public void onRechargeBtnClick() {
		if (!btnRecharge.isSelected()) {
			return;
		}

		rechargeAmt = Integer.valueOf(editRecharge.getText().toString());

		preCreateOrder();

	}

	/**
	 * 预下单
	 */
	private void preCreateOrder() {

		if (!Utils.isCanConnectionNetWork(InputRechargeAmtActivity.this)) {
			Toast.makeText(InputRechargeAmtActivity.this, "网络连接异常！", Toast.LENGTH_SHORT);
			return;
		}

		if (dialog != null) {
			dialog.show();
		}

		StringRequest req = new StringRequest(Method.POST, App.getDomain() + "/ordertoken", new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {

					T.d("return => " + arg0);

					JSONObject tokenJson = new JSONObject(arg0);
					ConstValue.orderToken = tokenJson.getString("token");

					mqt.setOutOrderToken(ConstValue.orderToken);

					// 获取充值返现信息
					getRechargeAward();

				} catch (Exception e) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toaster.show(InputRechargeAmtActivity.this, "请求失败！");
				T.d(arg0.toString());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {

				Map<String, String> params = new HashMap<String, String>();
				params.put("total_amt", mAmt);
				return params;
			}

		};
		mQueue.add(req);
	}

	/**
	 * 获取充值返现金额
	 */
	private void getRechargeAward() {

		mqt.matchRule(rechargeAmt + "", new QTCallBack() {

			@Override
			public void onSuccess(Map<String, Object> dataInfo) {
				if (dataInfo.containsKey("award")) {
					if (dialog.isShowing()) {
						dialog.dismiss();
					}

					extraInfo = new ArrayList<ExtraInfo>();
					ExtraInfo extra = new ExtraInfo("充值奖励", "奖励金额："
							+ Utils.num2String(Integer.valueOf((String) dataInfo.get("award"))) + "元");
					extraInfo.add(extra);

					ExtraInfo recharge = new ExtraInfo("余额充值", "充值金额：" + Utils.num2String(rechargeAmt) + "元");
					extraInfo.add(recharge);

					getSettingConfig();
				}
			}

			@Override
			public void onError(Map<String, String> errorInfo) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
	}

	// 跳转到钱台收银页面
	private void doJump() {

		Intent intent = new Intent(InputRechargeAmtActivity.this, CashierActivity.class);
		intent.putExtra(QTConst.EXTRO, new QTHolder(QTConst.ServerType_RECHARGE, rechargeAmt, goods, extraInfo , ConstValue.mobile));
		startActivityForResult(intent, ConstValue.REQUEST_FOR_CASHIER);
		overridePendingTransition(R.anim.qt_slide_in_from_bottom, R.anim.qt_slide_out_to_top);
	}

	private void checkRechargeAmt() {
		mAmt = editRecharge.getText().toString().trim();
		if (mAmt != null && !mAmt.equals("")) {
			btnRecharge.setSelected(true);
			return;
		} else {
			btnRecharge.setSelected(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.back:
				finish();
				break;
			case R.id.recharge:
				onRechargeBtnClick();
				break;
		}
	}
}
