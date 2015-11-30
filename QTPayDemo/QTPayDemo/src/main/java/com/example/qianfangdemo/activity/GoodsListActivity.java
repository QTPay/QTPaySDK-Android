package com.example.qianfangdemo.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.Utils.Utils;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.ConstValue;
import com.qfpay.qpossdk.entity.PayOrderInfo;
import com.qfpay.qpossdk.entity.RepealOrderInfo;
import com.qfpay.qpossdk.pay.QfPayCallBack;
import com.qfpay.qpossdk.pay.QfPaySdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.R;

public class GoodsListActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "GoodsListActivity";


    private boolean isCallQpos = false;

    private String timestamp = ""; //时间戳
    private String sign = ""; //MD5加密签名
    //	private String settle_userid = "";  //订单清算的用户id
    private String create_userid = ""; //订单创建的用户id
    private String qf_token = ""; //

    private String goodsname = "";
    private String goodsamt = "";

    private static final int DIALOG_SUCCESS = 0;
    private static final int DIALOG_FAIL = 1;
    private static final int DIALOG_REFUND = DIALOG_FAIL + 1;
    private static final int DIALOG_REFUND_FAIL = DIALOG_REFUND + 1;

    private static final int CALL_POS_SDK = 4;
    private static final int CALL_POS_REFUND = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goodslist);

        findViewById(R.id.order_one).setOnClickListener(this);
        findViewById(R.id.order_two).setOnClickListener(this);
        findViewById(R.id.order_three).setOnClickListener(this);
        findViewById(R.id.order_qpossdk).setOnClickListener(this);
        findViewById(R.id.order_refund).setOnClickListener(this);

    }

    @Override
    protected void onResume() {

        if (isCallQpos) {
            //如果已经唤起了QPOS，则在onResume中对订单进行查询，看看是否已经支付成功
//			queryOrder();
            isCallQpos = false;
        }
        qf_token = "";
        super.onResume();
    }


    private void onCreateOrderClick() {
        goodsname = "奥迪A6";
        goodsamt = "30";
        showAmtDialog(1);
    }

    private void onRechargeClick() {
        goodsname = "特斯拉";
        goodsamt = "50";
        showAmtDialog(2);
    }

    private void onPrePayClick() {
        goodsname = "老年代步车";
        goodsamt = "10";
        showAmtDialog(3);
    }

    private void onSDKPayClick() {
        goodsname = "老年代步车SDK";
        goodsamt = "1";
        showAmtDialog(CALL_POS_SDK);
    }


    /**
     * 撤销上笔交易.
     * 创建撤销订单
     */
    private void onRefundClick() {
        if (dialog != null) {
            dialog.show();
        }
        T.i("url---------->" + App.getDomain() + ConstValue.URL_REFUND);
        StringRequest req = new StringRequest(Method.POST, App.getDomain() + ConstValue.URL_REFUND, new Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                T.i("back------->" + s);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.has("respcd")) {
                        String respcd = jsonObject.getString("respcd");
                        String resperr = jsonObject.getString("resperr");

                        if (!"0000".equals(respcd)) {
                            Toast.makeText(getApplicationContext(), resperr, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (jsonObject.has("data")) {
                            String data = jsonObject.getString("data");
                            T.d("data : " + data);
                            if (TextUtils.isEmpty(data)) {
                                // 无订单信息数据
                                return;
                            }

                            JSONObject dataJson = new JSONObject(data);
                            RepealOrderInfo repealOrderInfo = new RepealOrderInfo();
                            repealOrderInfo.createUserId = dataJson.getString("create_userid");
                            repealOrderInfo.orderId = dataJson.getString("order_id");
                            repealOrderInfo.orderNo = dataJson.getString("orderno");
                            repealOrderInfo.origOrderNo = dataJson.getString("orig_orderno");
                            repealOrderInfo.qfToken = dataJson.getString("qf_token");
                            repealOrderInfo.token = ConstValue.userToken;
                            QfPaySdk.getInstance().repealOrder(GoodsListActivity.this, repealOrderInfo, repealCallBack);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("caller", "APP");
                params.put("app_code", App.getAppCod());

                // 撤销订单号
                params.put("order_id", App.getLastOrderNo());
                // 对应金额，用作检查核对
                params.put("amt", App.getLastOrderAmt());
                params.put("token", ConstValue.userToken);
                return params;
            }
        };
        mQueue.add(req);
    }

    private Dialog amtDialog;

    /**
     * 显示输入金额对话框
     */
    private void showAmtDialog(final int select) {

        View view = getLayoutInflater().inflate(R.layout.amount_dialog, null);

        final EditText amountEditText = (EditText) view.findViewById(R.id.amountEditText);
        final EditText goodsName = (EditText) view.findViewById(R.id.goodsName);
        amountEditText.setText(goodsamt);
        amountEditText.setSelection(goodsamt.length());
        goodsName.setText(goodsname);
        goodsName.setSelection(goodsname.length());
        if (amtDialog == null) {
            amtDialog = new Dialog(GoodsListActivity.this);
            amtDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        amtDialog.show();
        amtDialog.setContentView(view);
        view.findViewById(R.id.setButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                amtDialog.dismiss();

                goodsamt = amountEditText.getText().toString();
                goodsname = goodsName.getText().toString();
                itemClick(select);
            }
        });
    }

    private void itemClick(final int amt) {
        //带着token和商品信息去获取支付订单号

        if (!Utils.isCanConnectionNetWork(GoodsListActivity.this)) {
            Toast.makeText(GoodsListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
                    T.d(arg0);

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    JSONObject tokenJson = new JSONObject(arg0);

                    if (tokenJson.has("respcd")) {
                        String respcd = tokenJson.getString("respcd");
                        String resperr = tokenJson.getString("resperr");
                        if ("0000".equals(respcd)) {

                            if (tokenJson.has("data")) {
                                String data = tokenJson.getString("data");
                                T.d("data : " + data);
                                if (data != null && !data.equals("")) {
                                    JSONObject dataJson = new JSONObject(data);

                                    if (dataJson.has("order_id")) {
                                        ConstValue.payOrderId = dataJson.getString("order_id");
                                        App.putLastOrderNo(ConstValue.payOrderId);
                                        App.putLastOrderAmt(goodsamt);
                                        T.d("order_id : " + ConstValue.payOrderId);
                                    }

                                    if (dataJson.has("create_userid")) {
                                        create_userid = dataJson.getString("create_userid");
                                        T.d("create_userid : " + create_userid);
                                    }

                                    if (dataJson.has("qf_token")) {
                                        qf_token = dataJson.getString("qf_token");
                                    }

                                }
                                //唤起钱方商户的安全支付插件
                                callQposSafe(amt);
                            }
                        } else {
                            T.d("resperr = " + resperr);
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
        }) {
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
                params.put("out_sn", System.currentTimeMillis() + "");
                params.put("goods_name", goodsname);
                params.put("total_amt", goodsamt);
                params.put("goods_info", "这个人很懒，啥么子都木有留下");

                Log.d("", params.toString() + "");
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
     * 查询订单信息
     * 第二次进入该页面且已经唤起过QPOS，则查询订单情况
     */
    private void queryOrder() {

        if (!Utils.isCanConnectionNetWork(GoodsListActivity.this)) {
            Toast.makeText(GoodsListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dialog != null) {
            dialog.show();
        }

        //到server查询该笔订单的交易情况
        StringRequest req = new StringRequest(Method.GET, App.getDomain() + ConstValue.URL_QUERY + "?order_id=" + ConstValue.payOrderId, new Listener<String>() {

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

                        if (!"0000".equals(respcd)) {
                            Toast.makeText(getApplicationContext(), resperr, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (tokenJson.has("data")) {
                            String data = tokenJson.getString("data");
                            T.d("data : " + data);
                            if (TextUtils.isEmpty(data)) {
                                // 无订单信息数据
                                return;
                            }

                            JSONObject dataJson = new JSONObject(data);
                            if (dataJson.has("order_info")) {
                                JSONArray orderList = dataJson.getJSONArray("order_info");
                                if (orderList.getJSONObject(0) != null) {
                                    JSONObject datamap = orderList.getJSONObject(0);

                                    int refund = 0;
                                    if(datamap.has("refund")){
                                        refund = datamap.getInt("refund");
                                    }

                                    if (datamap.has("status")) {
                                        int status = datamap.getInt("status");

                                        T.i("status : " + status);
                                        //1：未支付  2：支付成功
                                        if (status == 2) {
                                            showDialog(DIALOG_SUCCESS);
                                        } else {
                                            //失败
                                            showDialog(DIALOG_FAIL);
                                        }
                                    }
                                }
                            }
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
    protected void callQposSafe(int select) {

        /**
         * 唤起Qpos SDK支付
         */
        if (select == CALL_POS_SDK) {
            callPosSdk();
            return;
        }

        /**
         * 唤起钱方商户支付
         */
        timestamp = System.currentTimeMillis() + "";


        //=======不再校验MD5
//        String oriStr = "";
//        if(TextUtils.isEmpty(qf_token)) {
//             oriStr = "pay_order_create=" + create_userid +
//                    "pay_order_id=" + ConstValue.payOrderId +
//                    "platform=2" +
//                    "timestamp=" + timestamp +
//                    "qpos";
//        }else{
//            oriStr = "pay_order_create=" + create_userid +
//                    "pay_order_id=" + ConstValue.payOrderId +
//                    "platform=2" +
//                    "qf_token=" + qf_token +
//                    "timestamp=" + timestamp +
//                    "qpos";
//        }
//		//MD5加密
//		sign = Utils.toMd5(oriStr);
//		T.w("sign加密前原数据 ： " + oriStr);

        Intent intent = new Intent();

        intent.setClassName("net.qfpay.king.android", "net.qfpay.king.android.function.tradecloud.activity.QfTradeCloudActivity");


        Bundle bundle = new Bundle();
        bundle.putString("pay_order_create", create_userid);
        bundle.putString("pay_order_id", ConstValue.payOrderId);
        bundle.putString("platform", "2");
        bundle.putString("timestamp", timestamp);
        bundle.putString("mobile", ConstValue.mobile);
        if (!TextUtils.isEmpty(qf_token)) {
            bundle.putString("qf_token", qf_token);
        }

        bundle.putString("token", ConstValue.userToken);

//		bundle.putString("sign", sign);

        T.i("qf_token :" + qf_token);
        T.i("pay_order_creater : " + create_userid);
        T.i("pay_order_id : " + ConstValue.payOrderId);
        T.i("timestamp : " + timestamp);
//		T.i("sign : " + sign);

        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
        isCallQpos = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("TAG", "code : " + resultCode);

        if (data != null) {
            String status_code = data.getStringExtra("status_code");
            String mtimestamp = data.getStringExtra("timestamp");
            Toast.makeText(getApplicationContext(), "resultCode" + resultCode + ",\nstatus_code=" + status_code + ",\ntimestamp=" + mtimestamp, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "resultCode" + resultCode, Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
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

            case DIALOG_REFUND:

                dialog = new AlertDialog.Builder(this).setMessage("查询完毕，订单已撤销！").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                    }
                }).setCancelable(false).create();
                break;

            case DIALOG_REFUND_FAIL:
                dialog = new AlertDialog.Builder(this).setMessage("订单撤销失败！").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                    }
                }).setCancelable(false).create();
                break;
        }
        return dialog;
    }


    // 支付结果获取和处理
    // 实际交易结果以服务端返回结果为准
    QfPayCallBack payCallBack = new QfPayCallBack() {
        @Override
        public void onPayResultFail() {
            Toast.makeText(GoodsListActivity.this, "交易失败", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============交易失败============");
            queryOrder();
        }

        @Override
        public void onPayResultSuccess() {
            Toast.makeText(GoodsListActivity.this, "交易成功", Toast.LENGTH_LONG).show();
            Log.w(TAG, "=============交易成功===========");
            queryOrder();
        }

        @Override
        public void onPayResultCancel() {
            Toast.makeText(GoodsListActivity.this, "交易取消", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============交易取消============");
            queryOrder();
        }

        @Override
        public void onPayResultUnknow() {
            Toast.makeText(GoodsListActivity.this, "交易结果未知", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============交易结果未知============");
            queryOrder();
        }
    };


    private void callPosSdk() {
        /**
         * 99963a5c18394e89ba74910625b088a1
         * qf_token :2d74b410-3433-4465-aec5-3ff75b57997e
         08-12 14:21:14.979  16286-16286/qfpay.wxshop I/qposSDK﹕ pay_order_creater : 924656
         08-12 14:21:14.979  16286-16286/qfpay.wxshop I/qposSDK﹕ pay_order_id : 6037114600195093168
         08-12 14:21:14.979  16286-16286/qfpay.wxshop I/qposSDK﹕ timestamp : 1439360474965
         */
        // 组装PayOrderInfo对象
        PayOrderInfo orderInfo = new PayOrderInfo();
        // 支付订单创建者id
        orderInfo.createUserId = create_userid;
        // 支付订单号
        orderInfo.payOrderId = ConstValue.payOrderId;


        // 用户订单token
        orderInfo.qfToken = qf_token;
        orderInfo.mobile = ConstValue.mobile;
        // 用户登录返回token
        orderInfo.token = ConstValue.userToken;
        orderInfo.platform = "2";

        // 调用sdk进行支付
        QfPaySdk.getInstance().payOrder(GoodsListActivity.this, orderInfo, payCallBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_one:
                onCreateOrderClick();
                break;
            case R.id.order_two:
                onRechargeClick();
                break;
            case R.id.order_three:
                onPrePayClick();
                break;
            case R.id.order_qpossdk:
                onSDKPayClick();
                break;
            case R.id.order_refund:
                onRefundClick();
                break;
        }
    }


    /**
     * 实际交易结果以服务端返回结果为准
     *  撤销成功时，queryOrder返回refund 为2
     */
    QfPayCallBack repealCallBack = new QfPayCallBack() {
        @Override
        public void onPayResultFail() {
            Toast.makeText(GoodsListActivity.this, "撤销失败", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============撤销失败============");
            queryReapealOrder();
        }

        @Override
        public void onPayResultSuccess() {
            Toast.makeText(GoodsListActivity.this, "撤销成功", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============撤销成功============");
            queryReapealOrder();
        }

        @Override
        public void onPayResultCancel() {
            Toast.makeText(GoodsListActivity.this, "取消撤销", Toast.LENGTH_LONG).show();
            Log.w(TAG, "============取消撤销============");
            queryReapealOrder();
        }

        @Override
        public void onPayResultUnknow() {
            Toast.makeText(GoodsListActivity.this, "撤销结果未知", Toast.LENGTH_LONG).show();
            Log.w(TAG, "=============撤销结果未知===========");
            queryReapealOrder();
        }
    };


    /**
     * 查询订单信息
     */
    private void queryReapealOrder() {

        if (!Utils.isCanConnectionNetWork(GoodsListActivity.this)) {
            Toast.makeText(GoodsListActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dialog != null) {
            dialog.show();
        }

        //到server查询该笔订单的交易情况
        StringRequest req = new StringRequest(Method.GET, App.getDomain() + ConstValue.URL_QUERY + "?order_id=" + ConstValue.payOrderId, new Listener<String>() {

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

                        if (!"0000".equals(respcd)) {
                            Toast.makeText(getApplicationContext(), resperr, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (tokenJson.has("data")) {
                            String data = tokenJson.getString("data");
                            T.d("data : " + data);
                            if (TextUtils.isEmpty(data)) {
                                // 无订单信息数据
                                return;
                            }

                            JSONObject dataJson = new JSONObject(data);
                            if (dataJson.has("order_info")) {
                                JSONArray orderList = dataJson.getJSONArray("order_info");
                                if (orderList.getJSONObject(0) != null) {
                                    JSONObject datamap = orderList.getJSONObject(0);

                                    int refund = 0;
                                    if(datamap.has("refund")){
                                        refund = datamap.getInt("refund");
                                    }

                                    if (datamap.has("status")) {
                                        int status = datamap.getInt("status");

                                        T.i("status : " + status);
                                        //1：未支付  2：支付成功
                                        if (status == 2) {
                                            // TODO refund = 2时，表示已撤销
                                            if (refund == 2) {
                                                showDialog(DIALOG_REFUND);
                                            } else {
                                                showDialog(DIALOG_REFUND_FAIL);
                                            }
                                        } else {
                                            //失败
                                            showDialog(DIALOG_FAIL);
                                        }
                                    }
                                }
                            }
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

}
