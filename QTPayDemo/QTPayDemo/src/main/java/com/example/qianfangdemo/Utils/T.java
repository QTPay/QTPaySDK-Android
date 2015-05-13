package com.example.qianfangdemo.Utils;

import android.util.Log;

public class T {
	static String AppName = "QF";

	static Boolean isTesting = true;

	public static void i(String string) {
		if (isTesting) {
			Log.i(AppName, string);
		}
	}

	public static void w(String string) {
		if (isTesting) {
			Log.e(AppName, string);
		}
	}

	public static void e(Exception string) {
		if (isTesting) {
			Log.e(AppName, string.toString());
		}
	}

	public static void d(String string) {
		if (isTesting) {
			Log.d(AppName, string);
		}
	}

	public static void a(int num) {
		if (isTesting) {
			Log.d(AppName, Integer.toString(num));
		}
	}
}
