package com.example.qianfangdemo.fragment;

import qfpay.wxshop.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qianfangdemo.activity.HomeActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class HomeFragment extends Fragment {

	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fagment_home, container, false);

		ViewUtils.inject(this, view);

		mActivity = HomeFragment.this.getActivity();

		return view;
	}

	@OnClick(R.id.qt_create_order_btns)
	private void onCreateOrderClicked(View view) {
		((HomeActivity) mActivity).getSettingConfig();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
