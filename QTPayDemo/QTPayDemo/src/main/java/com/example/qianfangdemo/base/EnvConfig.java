package com.example.qianfangdemo.base;

import java.io.Serializable;
import java.util.List;

public class EnvConfig implements Serializable {

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
