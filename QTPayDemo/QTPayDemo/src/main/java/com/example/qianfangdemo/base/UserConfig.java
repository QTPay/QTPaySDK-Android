package com.example.qianfangdemo.base;

import java.io.Serializable;

public class UserConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8823893840011067755L;
	private String appCode;
	private String appKey;
	private String outMerchant;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getOutMerchant() {
		return outMerchant;
	}

	public void setOutMerchant(String outMerchant) {
		this.outMerchant = outMerchant;
	}

	@Override
	public String toString() {
		return "UserConfig [appCode=" + appCode + ", appKey=" + appKey + ", outMerchant=" + outMerchant + "]";
	}
	
	

}
