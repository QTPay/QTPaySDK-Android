package com.example.qianfangdemo.activity;

import java.util.Hashtable;
import java.util.Map;

import qfpay.wxshop.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

import com.example.qianfangdemo.Utils.LocalData;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.Utils.Toaster;
import com.example.qianfangdemo.base.ConstValue;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qfpay.sdk.common.QTCallBack;

public class PrePayActivity extends BaseActivity {

	private int QR_WIDTH = 500;
	private int QR_HEIGHT = 500;

	@ViewInject(R.id.iv_qrcode)
	private ImageView mImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pre_pay);
		ViewUtils.inject(this);

		mqt.getShareIcon1("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=gQHz7zoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL21IVzZqLWJsekR5MVE2dGJMVnQ1AAIENXLZVAMECAcAAA==", new QTCallBack() {

			@Override
			public void onSuccess(Map<String, Object> dataInfo) {
				Bitmap map = (Bitmap) dataInfo.get("bitmap");
				mImage.setImageBitmap(map);
			}

			@Override
			public void onError(Map<String, String> errorInfo) {

			}
		});

		// String str =
		// LocalData.getInstance(PrePayActivity.this).getDate(ConstValue.mobile);
		// T.d("order_id = " + str);
		// if (TextUtils.isEmpty(str)) {
		// Toaster.show(PrePayActivity.this, "无需要支付的订单！");
		// } else {
		// createQRImage("order_id=" + str);
		// }
	}

	private void createQRImage(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
			mImage.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
