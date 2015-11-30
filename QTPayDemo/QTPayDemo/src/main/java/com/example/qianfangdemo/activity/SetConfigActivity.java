package com.example.qianfangdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.EnvConfig;
import com.qfpay.sdk.common.QTEnviroment;

import java.util.List;

import qfpay.wxshop.R;

public class SetConfigActivity extends BaseActivity implements OnItemClickListener {

	private int index;
	private ListView listview;
	private List<EnvConfig> configs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_config);

		listview = (ListView) findViewById(R.id.list);
		listview.setOnItemClickListener(this);

		index = App.getIndex()[0];

		configs = App.getEnvConfig();

		for (EnvConfig envConfig : configs) {
			T.d(envConfig.getEnvName());
			T.d(envConfig.getEnvUrl());
		}

		ConfigAdapter apdater = new ConfigAdapter(configs, this);
		listview.setAdapter(apdater);
	}

	public class ConfigAdapter extends BaseAdapter {
		List<EnvConfig> list;
		LayoutInflater inflater;

		public ConfigAdapter(List<EnvConfig> envs, Context context) {
			this.list = envs;
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (list != null)
				return list.size();
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (list != null)
				return list.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView != null) {
				Holder holder = (Holder) convertView.getTag();
				holder.name = (TextView) convertView.findViewById(R.id.envName);
				holder.url = (TextView) convertView.findViewById(R.id.envUrl);
				if (index == position) {
					holder.img.setVisibility(View.VISIBLE);
				} else {

					holder.img.setVisibility(View.INVISIBLE);
				}

			} else {
				convertView = inflater.inflate(R.layout.item_config, null);
				Holder holder = new Holder();
				holder.name = (TextView) convertView.findViewById(R.id.envName);
				holder.url = (TextView) convertView.findViewById(R.id.envUrl);
				holder.img = (ImageView) convertView.findViewById(R.id.imgCheck);

				holder.name.setText(list.get(position).getEnvName());
				holder.url.setText(list.get(position).getEnvUrl());
				if (index == position) {
					holder.img.setVisibility(View.VISIBLE);
				} else {

					holder.img.setVisibility(View.INVISIBLE);
				}
				convertView.setTag(holder);
			}

			return convertView;
		}

	}

	public class Holder {
		TextView name;
		TextView url;
		ImageView img;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		listview.getChildAt(index).findViewById(R.id.imgCheck).setVisibility(View.INVISIBLE);
		index = position;
		view.findViewById(R.id.imgCheck).setVisibility(View.VISIBLE);
		if(configs.get(position).getEnvName().equals("生产")){
			mqt.setEnviroment(QTEnviroment.WORK);
		}else if(configs.get(position).getEnvName().equals("沙盒")){
			mqt.setEnviroment(QTEnviroment.SANDBOX);
		}else{
			mqt.setEnviroment(QTEnviroment.QA);
		}
		
		
		Intent intent = new Intent(this, ConfigActivity.class);
		intent.putExtra("index", position);
		startActivity(intent);
	}

}
