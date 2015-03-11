package com.example.qianfangdemo.activity;

import qfpay.wxshop.R;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.qfpay.sdk.common.QTPayCommon;

public class BaseActivity extends Activity {

	protected Dialog dialog;

	public static final int RECHARGE = 1;
	public static final int PAY = 2;

	protected RequestQueue mQueue;
	protected QTPayCommon mqt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dialog = new Dialog(BaseActivity.this, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog.setCancelable(false);

		mQueue = Volley.newRequestQueue(getApplicationContext());
		mqt = QTPayCommon.getInstance(getApplicationContext());

	}

	protected void onDialogShow() {
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	protected void onDialogDismiss() {
		if (dialog.isShowing() && null != dialog) {
			dialog.dismiss();
		}
	}
}
