package com.example.qianfangdemo.entity;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import qfpay.wxshop.R;


public class CityAdaper extends BaseAdapter {

	private List<City> cities;
	private Context context;
	private LayoutInflater inflater;

	public CityAdaper(List<City> cities, Context context) {
		this.cities = cities;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return cities.size();
	}

	@Override
	public Object getItem(int position) {
		return cities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		City city = cities.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_act_list, null);
			holder.textView = (TextView) convertView.findViewById(R.id.act_list_item);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.textView.setText(city.getCity_name());
		return convertView;
	}

	class ViewHolder {
		TextView textView;
	}
}
