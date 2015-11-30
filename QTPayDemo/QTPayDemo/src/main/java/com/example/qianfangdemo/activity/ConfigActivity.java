package com.example.qianfangdemo.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.EnvConfig;
import com.example.qianfangdemo.base.UserConfig;

import java.util.List;

import qfpay.wxshop.R;

public class ConfigActivity extends BaseActivity implements OnItemClickListener, OnClickListener,
		OnItemLongClickListener {

	private int index;
	private int childIndex = 0;
	private List<UserConfig> userConfigs;
	boolean isDel = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_config);

		TextView right = (TextView) findViewById(R.id.setting);
		right.setText("添加新配置");

		right.setOnClickListener(this);

		index = getIntent().getIntExtra("index", 0);
		EnvConfig envconfig = App.getEnvConfig().get(index);
		userConfigs = envconfig.getUserConfigs();

		adapter = new SimpleAdapter(this, userConfigs);

		if (App.getIndex()[0] == index) {
			childIndex = App.getIndex()[1];
			adapter.setSelected(childIndex);
		} else {
			adapter.setSelected(0);
			App.updateSelection(index + "," + 0);
		}

		listview = (ListView) findViewById(R.id.list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);

	}

	public class SimpleAdapter extends BaseAdapter {
		Context context;
		List<UserConfig> list;
		private LayoutInflater inflater;
		int index = -1;

		public SimpleAdapter(Context context, List<UserConfig> userConfigs) {
			this.context = context;
			list = userConfigs;
			inflater = LayoutInflater.from(context);
		}

		public void setSelected(int index) {
			this.index = index;
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

				((TextView) convertView.findViewById(R.id.tagname)).setText("配置 ＃" + position);
				((TextView) convertView.findViewById(R.id.appCode)).setText(list.get(position).getAppCode());
				((TextView) convertView.findViewById(R.id.appKey)).setText(list.get(position).getAppKey());
				((TextView) convertView.findViewById(R.id.merid)).setText(list.get(position).getOutMerchant());

			} else {
				convertView = inflater.inflate(R.layout.item_config_detail, null);
				((TextView) convertView.findViewById(R.id.tagname)).setText("配置 ＃" + position);
				((TextView) convertView.findViewById(R.id.appCode)).setText(list.get(position).getAppCode());
				((TextView) convertView.findViewById(R.id.appKey)).setText(list.get(position).getAppKey());
				((TextView) convertView.findViewById(R.id.merid)).setText(list.get(position).getOutMerchant());
			}

			if (index >= 0 && position == index) {
				convertView.findViewById(R.id.img).setVisibility(View.VISIBLE);
			} else {
				convertView.findViewById(R.id.img).setVisibility(View.INVISIBLE);

			}

			return convertView;
		}

	}

	private SimpleAdapter adapter;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		App.updateSelection(index + "," + position);

		if (!isDel) {
			listview.getChildAt(childIndex).findViewById(R.id.img).setVisibility(View.INVISIBLE);
		}
		isDel = false;
		view.findViewById(R.id.img).setVisibility(View.VISIBLE);

		T.i("click----------------- update index: " + index + "," + position);

		Intent intent = new Intent(ConfigActivity.this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.setting:
			Builder builder = new Builder(this, AlertDialog.THEME_HOLO_LIGHT);
			LayoutInflater inflater = LayoutInflater.from(this);
//			builder.setCancelable(false);
			View view = inflater.inflate(R.layout.item_config_detail, null);
			view.setClickable(false);
			final EditText appCode = (EditText) view.findViewById(R.id.appCode);
			appCode.setEnabled(true);
			appCode.setFocusable(true);
			appCode.setFocusableInTouchMode(true);
			appCode.requestFocus();
			final EditText appKey = (EditText) view.findViewById(R.id.appKey);
			appKey.setEnabled(true);
			appKey.setFocusable(true);
			appKey.setFocusableInTouchMode(true);
			final EditText merid = (EditText) view.findViewById(R.id.merid);
			merid.setEnabled(true);
			merid.setFocusable(true);
			merid.setFocusableInTouchMode(true);

			view.findViewById(R.id.img).setVisibility(View.VISIBLE);
			view.findViewById(R.id.img).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String ac = appCode.getText().toString();
					String ak = appKey.getText().toString();
					String mi = merid.getText().toString();

					if (TextUtils.isEmpty(ac) || TextUtils.isEmpty(ak)) {
						Toast.makeText(getApplicationContext(), "appCode 与 appKey 不能为空", 0).show();
						return;
					}

					UserConfig config = new UserConfig();
					config.setAppCode(ac);
					config.setAppKey(ak);
					config.setOutMerchant(mi);
					App.addUserConfig(index, config);
					userConfigs.add(config);
					adapter.notifyDataSetChanged();
					handler.sendEmptyMessage(0x00);
				}
			});
			
			builder.setView(view);
			dia = builder.create();
			dia.setCanceledOnTouchOutside(false);
			dia.show();
			break;

		default:
			break;
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x00:
				dia.dismiss();
				break;

			default:
				break;
			}
		}
	};

	private AlertDialog dia;
	private ListView listview;

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

		Builder builder = new Builder(this);

		builder.setMessage("请选择您要的操作");
		final AlertDialog longDia = builder.create();
		longDia.setButton(DialogInterface.BUTTON_NEGATIVE, "修改", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				longDia.cancel();

				Builder builder = new Builder(ConfigActivity.this, AlertDialog.THEME_HOLO_LIGHT);
				LayoutInflater inflater = LayoutInflater.from(ConfigActivity.this);
//				builder.setCancelable(false);
				View view = inflater.inflate(R.layout.item_config_detail, null);
				view.setClickable(false);
				final EditText appCode = (EditText) view.findViewById(R.id.appCode);
				appCode.setText(userConfigs.get(position).getAppCode());
				appCode.setEnabled(true);
				appCode.setFocusable(true);
				appCode.setFocusableInTouchMode(true);
				appCode.requestFocus();
				final EditText appKey = (EditText) view.findViewById(R.id.appKey);
				appKey.setText(userConfigs.get(position).getAppKey());
				appKey.setEnabled(true);
				appKey.setFocusable(true);
				appKey.setFocusableInTouchMode(true);
				final EditText merid = (EditText) view.findViewById(R.id.merid);
				merid.setText(userConfigs.get(position).getOutMerchant());
				merid.setEnabled(true);
				merid.setFocusable(true);
				merid.setFocusableInTouchMode(true);

				view.findViewById(R.id.img).setVisibility(View.VISIBLE);
				view.findViewById(R.id.img).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String ac = appCode.getText().toString();
						String ak = appKey.getText().toString();
						String mi = merid.getText().toString();

						if (TextUtils.isEmpty(ac) || TextUtils.isEmpty(ak)) {
							Toast.makeText(getApplicationContext(), "appCode 与 appKey 不能为空", 0).show();
							return;
						}

						UserConfig config = new UserConfig();
						config.setAppCode(ac);
						config.setAppKey(ak);
						config.setOutMerchant(mi);
						App.replaceUserConfig(index, position, config);
						userConfigs.set(position, config);
						adapter.notifyDataSetChanged();
						handler.sendEmptyMessage(0x00);
					}
				});

				builder.setView(view);
				dia = builder.create();
				dia.setCanceledOnTouchOutside(false);
				dia.show();

			}
		});
		longDia.setButton(DialogInterface.BUTTON_POSITIVE, "删除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				longDia.dismiss();
				isDel = true;
				App.removeUserConfig(index, position);
				adapter.notifyDataSetChanged();
				userConfigs.remove(position);

				App.updateSelection(index + "," + 0);

				listview.getChildAt(0).findViewById(R.id.img).setVisibility(View.INVISIBLE);
			}
		});
		longDia.show();
		return true;
	}
	
}
