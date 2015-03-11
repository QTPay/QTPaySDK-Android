package com.example.qianfangdemo.activity;

import qfpay.wxshop.R;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class MainActivity extends BaseActivity {

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
		Intent intent = new Intent(MainActivity.this, HomeActivity.class);
		intent.putExtra("totalAmt", amt);
		startActivity(intent);
	}

	@OnClick(R.id.my_account)
	private void onAccountClick(View view) {
		startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
	}

}
