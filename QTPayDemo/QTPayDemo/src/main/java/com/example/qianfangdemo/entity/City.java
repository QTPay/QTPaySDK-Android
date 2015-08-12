package com.example.qianfangdemo.entity;

import java.io.Serializable;


public class City implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -628333124851699514L;

	private int city_id;
	private String city_code;
	private String city_name;
	private String prov_code;
	private String create_date;

	public int getCity_id() {
		return city_id;
	}

	public void setCity_id(int city_id) {
		this.city_id = city_id;
	}

	public String getCity_code() {
		return city_code;
	}

	public void setCity_code(String city_code) {
		this.city_code = city_code;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String getProv_code() {
		return prov_code;
	}

	public void setProv_code(String prov_code) {
		this.prov_code = prov_code;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

}
