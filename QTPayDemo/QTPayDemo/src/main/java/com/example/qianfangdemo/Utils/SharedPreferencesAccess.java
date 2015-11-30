package com.example.qianfangdemo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesAccess {

	public static String PREF_NAME = "qfpay_demo";

	private static SharedPreferencesAccess mInstance;

	private static SharedPreferences preferences;

	private SharedPreferencesAccess(Context context) {
		synchronized (this) {
			preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
	}

	public static SharedPreferencesAccess getInstance(Context context) {
		synchronized (SharedPreferencesAccess.class) {
			if (mInstance == null) {
				mInstance = new SharedPreferencesAccess(context);
			}
		}
		return mInstance;
	}

	public void putBoolean(String key, boolean value) {
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putFloat(String key, float value) {
		Editor editor = preferences.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public void putInt(String key, int value) {
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public void putString(String key, String value) {
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}


	public boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return preferences.getInt(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return preferences.getFloat(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return preferences.getLong(key, defValue);
	}

	public String getString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

}
