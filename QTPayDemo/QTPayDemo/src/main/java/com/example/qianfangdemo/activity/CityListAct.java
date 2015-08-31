package com.example.qianfangdemo.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.qianfangdemo.Utils.DBHelper;
import com.example.qianfangdemo.entity.City;
import com.example.qianfangdemo.entity.CityAdaper;

import java.util.List;

import qfpay.wxshop.R;


public class CityListAct extends AppCompatActivity {

	private ListView listView;
	private List<City> cities;
	private String prov_code;
	private String prov_name = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		if(getSupportActionBar()!=null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		initViews();
	}

	protected void initViews() {

		if (getIntent().getStringExtra("PROV_CODE") != null) {
			prov_code = getIntent().getStringExtra("PROV_CODE");
		}
		if (getIntent().getStringExtra("PROV_NAME") != null) {
			prov_name = getIntent().getStringExtra("PROV_NAME");
		}

		SQLiteDatabase database = DBHelper.getDatabase();
		cities = DBHelper.getCities(database, prov_code);

		listView = (ListView) findViewById(R.id.act_list);
		listView.setAdapter(new CityAdaper(cities, this));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("CITY_CODE", cities.get(position).getCity_code());
				intent.putExtra("CITY_NAME", cities.get(position).getCity_name());
				intent.putExtras(getIntent());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement

		if(id == android.R.id.home){
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
