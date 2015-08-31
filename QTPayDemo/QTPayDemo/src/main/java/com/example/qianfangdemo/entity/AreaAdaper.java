package com.example.qianfangdemo.entity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import qfpay.wxshop.R;


public class AreaAdaper extends BaseAdapter {

	private List<Area> areas;
	private Context context;
	private LayoutInflater inflater;

	public AreaAdaper(List<Area> areas, Context context) {
		this.areas = areas;
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return areas.size();
	}

	@Override
	public Object getItem(int position) {
		return areas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Area area = areas.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_act_list, null);
			holder.textView = (TextView) convertView.findViewById(R.id.act_list_item);
			convertView.setTag(holder);
		}

		holder = (ViewHolder) convertView.getTag();
		holder.textView.setText(area.getArea_name());

		return convertView;
	}

	class ViewHolder {
		TextView textView;
	}
}
