package com.example.qianfangdemo.Utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qianfangdemo.entity.Area;
import com.example.qianfangdemo.entity.City;
import com.example.qianfangdemo.entity.Province;

import java.util.ArrayList;
import java.util.List;

public class DBHelper {

	private static SQLiteDatabase database;
	/** 数据库名称 */
	private static final String DB_NAME = "province_city_area";

	/** 省名称表名 */
	private static final String PROVINCE_TABLE_NAME = "province";
	private static final String PROV_ID = "prov_id";
	private static final String PROV_CODE = "prov_code";
	private static final String PROV_NAME = "prov_name";
	private static final String COUNTRY_CODE = "country_code";
	private static final String CREATE_DATE = "create_date";

	/** 市名称表名 */
	private static final String CITY_TABLE_NAME = "city";
	private static final String CITY_ID = "city_id";
	private static final String CITY_CODE = "city_code";
	private static final String CITY_NAME = "city_name";

	/** 地区名称表名 */
	private static final String AREA_TABLE_NAME = "area";
	private static final String AREA_ID = "area_id";
	private static final String AREA_CODE = "area_code";
	private static final String AREA_NAME = "area_name";

	public static SQLiteDatabase getDatabase() {
		 if (database == null) {
//		 AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
//		 database = manager.getDatabase(DB_NAME);
		 }
		return database;
	}

	/**
	 * 获取省列表
	 * 
	 * @param db
	 * @return
	 */
	public static List<Province> getProvinces(SQLiteDatabase db) {
		AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
		SQLiteDatabase database = manager.getDatabase(DB_NAME);
		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = database.query(PROVINCE_TABLE_NAME, null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Province province = new Province();
			province.setProv_id(cursor.getInt(cursor.getColumnIndex(PROV_ID)));
			province.setProv_code(cursor.getString(cursor.getColumnIndex(PROV_CODE)));
			province.setProv_name(cursor.getString(cursor.getColumnIndex(PROV_NAME)));
			province.setCountry_code(cursor.getString(cursor.getColumnIndex(COUNTRY_CODE)));
			province.setCreate_date(cursor.getString(cursor.getColumnIndex(CREATE_DATE)));
			provinces.add(province);
		}
		cursor.close();
		return provinces;
	}

	/**
	 * 获取市列表
	 * 
	 * @param db
	 * @return
	 */
	public static List<City> getCities(SQLiteDatabase db, String prov_code) {
		AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
		SQLiteDatabase database = manager.getDatabase(DB_NAME);
		List<City> cities = new ArrayList<City>();
		Cursor cursor = database.query(CITY_TABLE_NAME, null, PROV_CODE + "=?", new String[] { prov_code }, null, null, null);
		while (cursor.moveToNext()) {
			City city = new City();
			city.setCity_id(cursor.getInt(cursor.getColumnIndex(CITY_ID)));
			city.setCity_code(cursor.getString(cursor.getColumnIndex(CITY_CODE)));
			city.setCity_name(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
			city.setProv_code(cursor.getString(cursor.getColumnIndex(PROV_CODE)));
			city.setCreate_date(cursor.getString(cursor.getColumnIndex(CREATE_DATE)));
			cities.add(city);
		}
		cursor.close();
		return cities;
	}

	/**
	 * 获取市列表
	 * 
	 * @param db
	 * @return
	 */
	public static List<Area> getAreas(SQLiteDatabase db, String city_code) {
		AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
		SQLiteDatabase database = manager.getDatabase(DB_NAME);
		List<Area> areas = new ArrayList<Area>();
		Cursor cursor = database.query(AREA_TABLE_NAME, null, CITY_CODE + "=?", new String[] { city_code }, null, null, null);
		while (cursor.moveToNext()) {
			Area area = new Area();
			area.setArea_id(cursor.getInt(cursor.getColumnIndex(AREA_ID)));
			area.setArea_code(cursor.getString(cursor.getColumnIndex(AREA_CODE)));
			area.setArea_name(cursor.getString(cursor.getColumnIndex(AREA_NAME)));
			area.setCity_code(cursor.getString(cursor.getColumnIndex(CITY_CODE)));
			area.setCreate_date(cursor.getString(cursor.getColumnIndex(CREATE_DATE)));
			areas.add(area);
		}
		cursor.close();
		return areas;
	}

	public static void closeDatabase() {
		try {
			AssetsDatabaseManager manager = AssetsDatabaseManager.getManager();
			manager.closeDatabase(DB_NAME);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
