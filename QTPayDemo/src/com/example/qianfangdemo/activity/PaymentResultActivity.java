package com.example.qianfangdemo.activity;

import java.util.Map;

import qfpay.wxshop.R;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.base.ConstValue;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.common.QTCallBack;
import com.qfpay.sdk.common.WeChatQT;

public class PaymentResultActivity extends BaseActivity {

	private boolean canShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment_result);

		ViewUtils.inject(this);

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
				canShare = true;
			}

			@Override
			public void onError(Map<String, String> errorInfo) {

				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				canShare = false;
				if (!errorInfo.containsKey("errmsg")) {
					Toaster.show(PaymentResultActivity.this, "获取分享信息失败！");
				} else {
					Toaster.show(PaymentResultActivity.this, errorInfo.get("errmsg"));
				}
			}
		});

	}

	@OnClick(R.id.btn_share_pengyouquan)
	public void onShareBtnClick(View view) {

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

	@OnClick(R.id.btn_share_weixin)
	public void onShareWeiXinBtnClick(View view) {
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

}
