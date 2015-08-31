package com.example.qianfangdemo.activity;

import java.util.Map;

import com.example.qianfangdemo.Utils.CacheData;
import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.Utils.Utils;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.QTConst;
import com.qfpay.sdk.entity.CustomerInfo;

import qfpay.wxshop.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyAccountActivity extends BaseActivity implements View.OnClickListener{

	private TextView mCouponNums;
	private TextView mBalanceAmt;

	private CustomerInfo mCustomerInfo;

	private String couponKey = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_account);
		mCouponNums = (TextView) findViewById(R.id.account_coupons);
		mBalanceAmt = (TextView) findViewById(R.id.account_balances);

		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.rl_account_coupons).setOnClickListener(this);
		findViewById(R.id.rl_account_recharge_balances).setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		getMyAcountInfo();
		super.onResume();
	}

	private void onCouponsLayoutClick() {
		Intent intent = new Intent(MyAccountActivity.this, CouponsActivity.class);
		intent.putExtra("couponTag", couponKey);
		startActivity(intent);
	}

	private void onBalanceRechargeClick() {
		startActivity(new Intent(MyAccountActivity.this, InputRechargeAmtActivity.class));
	}

	private void getMyAcountInfo() {
		onDialogShow();
		mqt.getCustomerInfo("", new int[] { QTConst.CustomerInfo_Balance, QTConst.CustomerInfo_Coupon },
				new QTCallBack() {

					@Override
					public void onSuccess(Map<String, Object> dataInfo) {
						if (dataInfo.containsKey("customer_info")) {
							mCustomerInfo = (CustomerInfo) dataInfo.get("customer_info");
							couponKey = System.currentTimeMillis() + "";
							CacheData.getInstance().setData(couponKey, mCustomerInfo.getCoupons());
							displayCustomerInfo();
						}
						onDialogDismiss();
					}

					@Override
					public void onError(Map<String, String> errorInfo) {
						onDialogDismiss();
						Toaster.show(MyAccountActivity.this, "获取用户信息失败!");
					}
				});
	}

	// 显示获取到
	private void displayCustomerInfo() {
		mBalanceAmt.setText("￥" + Utils.num2String(mCustomerInfo.getBalance()) + "元");
		mCouponNums.setText(mCustomerInfo.getCoupons().size() + "张");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.back:
				finish();
				break;
			case R.id.rl_account_coupons:
				onCouponsLayoutClick();
				break;
			case R.id.rl_account_recharge_balances:
				onBalanceRechargeClick();
				break;
		}
	}
}
