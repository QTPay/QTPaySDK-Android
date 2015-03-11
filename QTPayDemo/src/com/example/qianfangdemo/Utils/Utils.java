package com.example.qianfangdemo.Utils;

import java.text.DecimalFormat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	public static boolean isCanConnectionNetWork(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null || networkInfo.isConnectedOrConnecting() == false) {
			return false;
		}
		return true;
	}

	public static String num2String(int amount) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(amount / 100.0);
	}
}
