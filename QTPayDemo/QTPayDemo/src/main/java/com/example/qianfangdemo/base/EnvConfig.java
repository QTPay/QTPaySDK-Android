package com.example.qianfangdemo.base;

import java.io.Serializable;
import java.util.List;

public class EnvConfig implements Serializable {

	public static final String sandbox = "https://qtsandbox.qfpay.com";
	public static final String api = "https://qtapi.qfpay.com";
	public static final String QA = "https://qtapi.qa.qfpay.net";
	public static final String DEV = "https://qttest.qfpay.com";
	/**
	 * 
	 */
	private static final long serialVersionUID = 6028411603487623688L;
	private String envName;
	private String envUrl;
	private List<UserConfig> userConfigs;

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

	public List<UserConfig> getUserConfigs() {
		return userConfigs;
	}

	public void setUserConfigs(List<UserConfig> userConfigs) {
		this.userConfigs = userConfigs;
	}

	@Override
	public String toString() {
		return "EnvConfig [envName=" + envName + ", envUrl=" + envUrl + "]";
	}

}
