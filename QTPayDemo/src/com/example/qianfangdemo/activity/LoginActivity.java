package com.example.qianfangdemo.activity;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.R;

import org.json.JSONObject;

import android.app.Dialog;
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
import com.example.qianfangdemo.Utils.Utils;
import com.example.qianfangdemo.base.ConstValue;
import com.example.qianfangdemo.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class LoginActivity extends BaseActivity {

	@ViewInject(R.id.username)
	private ClearEditText phoneNumber;
	@ViewInject(R.id.login)
	private Button button;

	private boolean isLoading;

	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);

		dialog = new Dialog(LoginActivity.this, R.style.DialogStyle);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		dialog.setContentView(R.layout.my_dialog);
		dialog.setCancelable(false);

		TextWatcher watcher = new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
				// 检查商户的登录按钮是否可以点击
				checkMerchantBtnCanPress();
			}
		};

		phoneNumber.addTextChangedListener(watcher);
	}

	@OnClick(R.id.login)
	public void onLoginClick(View view) {
		login();
	}

	// 登录
	private void login() {

		if (!Utils.isCanConnectionNetWork(LoginActivity.this)) {
			Toast.makeText(LoginActivity.this, "网络连接异常！", Toast.LENGTH_SHORT);
			return;
		}

		if (!button.isSelected() || isLoading) {
			return;
		}

		if (dialog != null) {
			dialog.show();
			isLoading = true;
		}

		StringRequest req = new StringRequest(Method.POST, ConstValue.domain + "/createtoken", new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					T.d(arg0.toString());

					if (dialog.isShowing()) {
						dialog.dismiss();
						isLoading = false;
					}

					JSONObject tokenJson = new JSONObject(arg0);
					ConstValue.userToken = tokenJson.getString("token");
					ConstValue.mobile = phoneNumber.getText().toString();

					mqt.setUserToken(ConstValue.userToken);
					mqt.setAppInfo(ConstValue.appId, ConstValue.apiKey, ConstValue.wxAppId);

					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();

				} catch (Exception e) {
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
				Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
				T.d(arg0.toString());
			}
		}) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("mobile", phoneNumber.getText().toString().trim());
				params.put("app_code", ConstValue.appId);
				return params;
			}

		};
		mQueue.add(req);

	}

	private void checkMerchantBtnCanPress() {
		String usernameString = phoneNumber.getText().toString().trim();
		if (usernameString != null && !usernameString.equals("")) {
			button.setSelected(true);
			return;
		} else {
			button.setSelected(false);
		}
	}

}
