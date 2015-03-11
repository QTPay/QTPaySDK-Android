package com.example.qianfangdemo.activity;

import java.util.List;

import qfpay.wxshop.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianfangdemo.Utils.CacheData;
import com.example.qianfangdemo.Utils.Utils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.entity.Coupon;

public class CouponsActivity extends BaseActivity {

	@ViewInject(R.id.coupons_list)
	private ListView mCouponList;

	private String couponKey;

	private List<Coupon> mCoupons;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupons);

		ViewUtils.inject(this);

		couponKey = getIntent().getStringExtra("couponTag");
		mCoupons = (List<Coupon>) CacheData.getInstance().getData(couponKey, false);
		mCouponList.setAdapter(new CouponAdapter());
	}

	@OnClick(R.id.back)
	private void onBackButtonClick(View view) {
		finish();
	}

	private class CouponAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCoupons.size();
		}

		@Override
		public Object getItem(int position) {
			return mCoupons.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			Coupon coupon = mCoupons.get(position);
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(CouponsActivity.this).inflate(R.layout.item_coupons, null);
				holder.couponContent = (TextView) convertView.findViewById(R.id.coupon_title);
				holder.couponAmt = (TextView) convertView.findViewById(R.id.coupon_amt);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.couponContent.setText(coupon.getTitle());
			holder.couponAmt.setText(Utils.num2String(coupon.getAmt()) + "元");

			return convertView;
		}

		class ViewHolder {
			TextView couponContent;
			TextView couponAmt;
		}

	}

}
