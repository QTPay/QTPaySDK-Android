package com.example.qianfangdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.qianfangdemo.Utils.Toaster;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.WeChatQT;

import java.util.Map;

import qfpay.wxshop.R;

public class PaymentResultActivity extends BaseActivity {

	private boolean canShare;

    private View shareLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_result);


		findViewById(R.id.btn_share_pengyouquan).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShareBtnClick();
			}
		});

		findViewById(R.id.btn_share_weixin).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShareWeiXinBtnClick();
			}
		});


        shareLayout = findViewById(R.id.sharelayout);
		getShareInfo();
	}

	private void getShareInfo() {

		if (!dialog.isShowing()) {
			dialog.show();
		}

		mqt.getShareInfo(new QTCallBack() {

			@Override
			public void onSuccess(Map<String, Object> dataMap) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
                shareLayout.setVisibility(View.VISIBLE);
				canShare = true;
			}

			@Override
			public void onError(Map<String, String> errorInfo) {

				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				canShare = false;
                shareLayout.setVisibility(View.INVISIBLE);

				if (errorInfo!=null && !errorInfo.containsKey("errmsg")) {
					Toaster.show(PaymentResultActivity.this, errorInfo.get("errmsg"));
				} else {
					Toaster.show(PaymentResultActivity.this, "获取分享信息失败！");
				}
			}
		});

	}


	public void onShareBtnClick() {

		if (!canShare) {
			return;
		}

		WeChatQT weChatQt = WeChatQT.init(PaymentResultActivity.this);

		boolean shareTemp = weChatQt.share(WeChatQT.WEIXIN_PENGYOUQUAN);
		if (!shareTemp) {
			Toaster.show(PaymentResultActivity.this, "分享失败！");
		} else {
			Toaster.show(PaymentResultActivity.this, "正在唤起微信，请稍等！");
		}
	}

	public void onShareWeiXinBtnClick() {
		if (!canShare) {
			return;
		}

		WeChatQT weChatQt = WeChatQT.init(PaymentResultActivity.this);

		boolean shareTemp = weChatQt.share(WeChatQT.WEIXIN);
		if (!shareTemp) {
			Toaster.show(PaymentResultActivity.this, "分享失败！");
		} else {
			Toaster.show(PaymentResultActivity.this, "正在唤起微信，请稍等！");
		}
	}
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

}
