package com.example.qianfangdemo.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static boolean isAllHanzi(String str){
		for (int i = 0; i < str.length(); i++) {
			if(!isChinese(String.valueOf(str.charAt(i)))){
				return false;
			}
		}
		return true;
	}

	public static boolean isChinese(String c) {
		String regEx = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(c + "");
		if (m.find()) return true;
		return false;
	}
}
