package com.example.qianfangdemo.base;

import com.qfpay.sdk.base.ConstValue;


public enum Env {
	
	WORK("WORK", ConstValue.api), SANDBOX("SANDBOX", ConstValue.sandbox);

	private String envName;
	private String envUrl;

	private Env(String envName, String envUrl) {
		this.envName = envName;
		this.envUrl = envUrl;
	}

	public static String getUrl(String envName) {
		for (Env env : Env.values()) {
			if (envName.equals(env.getEnvName())) {
				return env.getEnvUrl();
			}
		}
		return null;
	}

	public String getEnvName() {
		return envName;
	}

	public void setEnvName(String envName) {
		this.envName = envName;
	}

	public String getEnvUrl() {
		return envUrl;
	}

	public void setEnvUrl(String envUrl) {
		this.envUrl = envUrl;
	}


}
