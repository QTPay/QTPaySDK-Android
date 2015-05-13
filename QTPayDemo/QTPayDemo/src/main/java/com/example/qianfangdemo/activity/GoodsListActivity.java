package com.example.qianfangdemo.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.ConstValue;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.R;

public class GoodsListActivity extends BaseActivity {

	private boolean isCallQpos =  false;
	
	private String timestamp = ""; //时间戳
	private String sign = ""; //MD5加密签名
//	private String settle_userid = "";  //订单清算的用户id
	private String create_userid = ""; //订单创建的用户id
	private String qf_token = ""; //

	private String goodsname = "";
	private String goodsamt = "";
	
	private static final int DIALOG_SUCCESS = 0;
	private static final int DIALOG_FAIL = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_goodslist);
		ViewUtils.inject(this);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		if(isCallQpos){
			//如果已经唤起了QPOS，则在onResume中对订单进行查询，看看是否已经支付成功
			queryOrder();
			isCallQpos = false;
		}
		super.onResume();
	}
	
	
	
	@OnClick(R.id.order_one)
	private void onCreateOrderClick(View view) {
		goodsname = "奥迪A6";
		goodsamt = "30";
		itemClick(1);
	}

	@OnClick(R.id.order_two)
	private void onRechargeClick(View view) {
		goodsname = "特斯拉";
		goodsamt = "50";
		itemClick(2);
	}

	@OnClick(R.id.order_three)
	private void onPrePayClick(View view) {
		goodsname = "老年代步车";
		goodsamt = "10";
		itemClick(3);
	}

	private void itemClick(int amt) {
		//带着token和商品信息去获取支付订单号

		if (!Utils.isCanConnectionNetWork(GoodsListActivity.this)) {
			Toast.makeText(GoodsListActivity.this, "网络异常", Toast.LENGTH_SHORT);
			return;
		}

		if (dialog != null) {
			dialog.show();
		}

        T.w("url: " + App.getDomain() + ConstValue.URL_SIMPLE_CREATE);
		//去服务器简易下单
		StringRequest req = new StringRequest(Method.POST, App.getDomain() + ConstValue.URL_SIMPLE_CREATE, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					T.d(arg0.toString());

					if (dialog.isShowing()) {
						dialog.dismiss();
					}
					JSONObject tokenJson = new JSONObject(arg0);
					
					if (tokenJson.has("respcd")) {
						String respcd = tokenJson.getString("respcd");                             
						String resperr = tokenJson.getString("resperr");
						if("0000".equals(respcd)){
							
							if (tokenJson.has("data")) {
								String data = tokenJson.getString("data");
								T.d("data : " + data);
								if (data != null && !data.equals("")) {
									JSONObject dataJson = new JSONObject(data);
									
									if (dataJson.has("order_id")) {
										ConstValue.payOrderId = dataJson.getString("order_id");
										T.d("order_id : "+ ConstValue.payOrderId);
									}
									
//									if (dataJson.has("settle_userid")) {
//										settle_userid = dataJson.getString("settle_userid");
//										T.d("settle_userid : "+ settle_userid);
//									}
									if (dataJson.has("create_userid")) {
										create_userid = dataJson.getString("create_userid");
										T.d("create_userid : "+ create_userid);
									}

									//qf_token为免登录设计，若需要免登录，唤起钱方商户时需要携带qf_token，同时签名中也需要增加该参数
//                                    if(dataJson.has("qf_token")){
//                                        qf_token = dataJson.getString("qf_token");
//                                    }

								}
								//唤起钱方商户的安全支付插件
								callQposSafe();
							}
						}else{
							Toast.makeText(getApplicationContext(), resperr, Toast.LENGTH_SHORT).show();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				Toast.makeText(GoodsListActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
				T.d(arg0.toString());
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				/**
				 * 请求参数为:
					token
					goods_name
					total_amt
					goods_info(必选)
				 */
				Map<String, String> params = new HashMap<String, String>();
				params.put("token", ConstValue.userToken);
				params.put("out_sn", System.currentTimeMillis()+"");
				params.put("goods_name", goodsname);
				params.put("total_amt", goodsamt);
				params.put("goods_info", "这个人很懒，啥么子都木有留下");
				
				Log.d("",params.toString()+"");
				return params;
			}


                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = super.getHeaders();
                Map<String, String> dHeader = new HashMap<String, String>();
                dHeader.putAll(header);
                dHeader.put("X-QT-ROUTE", "t");
                return header;
            }

        };
		mQueue.add(req);
		
	}
	
	
	/**
	 * 第二次进入该页面且已经唤起过QPOS，则查询订单情况
	 */
	private void queryOrder(){

		if (!Utils.isCanConnectionNetWork(GoodsListActivity.this)) {
			Toast.makeText(GoodsListActivity.this, "网络异常", Toast.LENGTH_SHORT);
			return;
		}

		if (dialog != null) {
			dialog.show();
		}


		//到server查询该笔订单的交易情况
		StringRequest req = new StringRequest(Method.GET, App.getDomain() + ConstValue.URL_QUERY+"?order_id=" + ConstValue.payOrderId, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				try {
					T.d(arg0.toString());

					if (dialog.isShowing()) {
						dialog.dismiss();
					}

					JSONObject tokenJson = new JSONObject(arg0);
					
					
					if (tokenJson.has("respcd")) {
						String respcd = tokenJson.getString("respcd");                             
						String resperr = tokenJson.getString("resperr");
						
						if("0000".equals(respcd)){
							
							if (tokenJson.has("data")) {
								String data = tokenJson.getString("data");
								T.d("data : "+data);
								if (data != null && !data.equals("")) {
									JSONObject dataJson = new JSONObject(data); 
									if (dataJson.has("order_info")) {
										JSONArray orderList = dataJson.getJSONArray("order_info");
										if(orderList.getJSONObject(0) != null){
											JSONObject datamap = orderList.getJSONObject(0);
											if(datamap.has("status")){
												int status = datamap.getInt("status");
												T.i("status : " + status);
												//1：未支付  2：支付成功
												if(status == 2){
													//支付成功
													showDialog(DIALOG_SUCCESS);
												}else{
													//失败
													showDialog(DIALOG_FAIL);
												}
											}
										}
									}
								}
							}
						}else{
							Toast.makeText(getApplicationContext(), resperr, Toast.LENGTH_SHORT).show();
						}
						
						
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				T.d(arg0.toString());
			}
		});
		mQueue.add(req);
		
		
	}
	

	

	/**
	 * 唤起安全支付插件
	 */
	protected void callQposSafe() {
		
		timestamp = System.currentTimeMillis() + "";
        String oriStr = "";
        if(TextUtils.isEmpty(qf_token)) {
             oriStr = "pay_order_create=" + create_userid +
                    "pay_order_id=" + ConstValue.payOrderId +
                    "platform=2" +
                    "timestamp=" + timestamp +
                    "qpos";
        }else{ //免登录
            oriStr = "pay_order_create=" + create_userid +
                    "pay_order_id=" + ConstValue.payOrderId +
                    "platform=2" +
                    "qf_token=" + qf_token +
                    "timestamp=" + timestamp +
                    "qpos";
        }
		//MD5加密
		sign = Utils.toMd5(oriStr);
		T.w("sign加密前原数据 ： " + oriStr);
		
		Intent intent = new Intent();
		intent.setClassName("net.qfpay.king.android", "net.qfpay.king.android.function.tradecloud.activity.QfTradeCloudActivity");
		Bundle bundle = new Bundle();
		bundle.putString("pay_order_create", create_userid);
		bundle.putString("pay_order_id", ConstValue.payOrderId);
        bundle.putString("platform", "2");
		bundle.putString("timestamp", timestamp);
        if(!TextUtils.isEmpty(qf_token)) {
            bundle.putString("qf_token", qf_token);
        }
		bundle.putString("sign", sign);

        T.i("qf_token :" + qf_token);
		T.i("pay_order_creater : " +  create_userid);
		T.i("pay_order_id : " +  ConstValue.payOrderId);
		T.i("timestamp : " + timestamp);
		T.i("sign : " + sign);
		
		intent.putExtras(bundle);
		startActivityForResult(intent, 1);
		isCallQpos = true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("TAG", "code : " + resultCode);
		                             
		if(data != null){
			String status_code = data.getStringExtra("status_code");
			String mtimestamp = data.getStringExtra("timestamp");
			Toast.makeText(getApplicationContext(), "resultCode"+resultCode+",\nstatus_code=" + status_code +",\ntimestamp=" + mtimestamp, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getApplicationContext(), "resultCode"+resultCode,Toast.LENGTH_SHORT).show();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;  
	    switch(id) {  
	    case DIALOG_SUCCESS:  
	    	dialog = new AlertDialog.Builder(this).setMessage("查询完毕，交易成功！").setPositiveButton("ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					
				}
			}).setCancelable(false).create();
	        break;  
	    case DIALOG_FAIL: 
	    	dialog = new AlertDialog.Builder(this).setMessage("查询完毕，交易未完成！").setPositiveButton("ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialoginterface, int i) {
					
				}
			}).setCancelable(false).create();
	    	break; 
	    }
	    return dialog;
	}
	

}
