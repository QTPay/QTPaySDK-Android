package com.example.qianfangdemo.entity;

import java.io.Serializable;


public class Province implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1196744110947943739L;
	private int prov_id;
	private String prov_code;
	private String prov_name;
	private String country_code;
	private String create_date;

	public int getProv_id() {
		return prov_id;
	}

	public void setProv_id(int prov_id) {
		this.prov_id = prov_id;
	}

	public String getProv_code() {
		return prov_code;
	}

	public void setProv_code(String prov_code) {
		this.prov_code = prov_code;
	}

	public String getProv_name() {
		return prov_name;
	}

	public void setProv_name(String prov_name) {
		this.prov_name = prov_name;
	}

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public String getCreate_date() {
		return create_date;
	}

	public void setCreate_date(String create_date) {
		this.create_date = create_date;
	}

}
