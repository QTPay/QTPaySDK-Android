package com.example.qianfangdemo.fragment;

import qfpay.wxshop.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qianfangdemo.activity.HomeActivity;

public class MyInfoFragment extends Fragment {

	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_info, container, false);
		mActivity = MyInfoFragment.this.getActivity();
		view.findViewById(R.id.qt_create_order_btns).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCreateOrderClicked();
			}
		});
		return view;
	}

	private void onCreateOrderClicked() {
		((HomeActivity) mActivity).getSettingConfig();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
