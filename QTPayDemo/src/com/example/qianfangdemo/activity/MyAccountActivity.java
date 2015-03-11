package com.example.qianfangdemo.activity;

import java.util.Map;

import com.example.qianfangdemo.Utils.CacheData;
import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.Utils.Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.QTConst;
import com.qfpay.sdk.entity.CustomerInfo;
import com.qfpay.sdk.utils.T;

import qfpay.wxshop.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyAccountActivity extends BaseActivity {

	@ViewInject(R.id.account_coupons)
	private TextView mCouponNums;
	@ViewInject(R.id.account_balances)
	private TextView mBalanceAmt;

	private CustomerInfo mCustomerInfo;

	private String couponKey = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_account);

		ViewUtils.inject(this);
		getMyAcountInfo();
	}

	@OnClick(R.id.back)
	private void onBackClick(View view) {
		finish();
	}

	@OnClick(R.id.rl_account_coupons)
	private void onCouponsLayoutClick(View view) {
		Intent intent = new Intent(MyAccountActivity.this, CouponsActivity.class);
		intent.putExtra("couponTag", couponKey);
		startActivity(intent);
	}

	@OnClick(R.id.rl_account_recharge_balances)
	private void onBalanceRechargeClick(View view) {
		startActivity(new Intent(MyAccountActivity.this, InputRechargeAmtActivity.class));
	}

	private void getMyAcountInfo() {
		onDialogShow();
		mqt.getCustomerInfo("", new int[] { QTConst.CustomerInfo_Balance, QTConst.CustomerInfo_Coupon }, new QTCallBack() {

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
}
