package com.example.qianfangdemo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.ConstValue;
import com.example.qianfangdemo.entity.BankInfo;
import com.example.qianfangdemo.entity.SubBank;
import com.gc.materialdesign.views.ProgressBarIndeterminate;
import com.gc.materialdesign.widgets.Dialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qfpay.wxshop.R;

public class CardDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private BankInfo cardInfo;
    private EditText mUserNameView;
    private EditText mCardNumView;
    private TextView mBankNameView , mAreaView , mSubNameView;
    private String bank_code;
    private String bank_name;
    private String city_code;
    private String city_name;
    private String prov_code;
    private String prov_name;
    private String subBankName;
    private String userName;
    private String cardNum;
    ProgressBarIndeterminate progressBar;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        handler = new Handler(Looper.getMainLooper());
        mUserNameView = (EditText) findViewById(R.id.et_username);
        mCardNumView = (EditText) findViewById(R.id.et_cardnum);
        mBankNameView = (TextView) findViewById(R.id.bankname);
        mAreaView = (TextView) findViewById(R.id.area);
        mSubNameView = (TextView) findViewById(R.id.subbank_name);
        progressBar= (ProgressBarIndeterminate) findViewById(R.id.progressBarIndeterminate);
        progressBar.setMax(50);
        progressBar.setMin(25);
        mSubNameView.setSelected(true);
        fillData();
    }

    private void fillData(){
        cardInfo = (BankInfo) getIntent().getSerializableExtra("CARDINFO");
        userName = getIntent().getStringExtra("USERNAME");
        cardNum = getIntent().getStringExtra("CARDNUM");
        mUserNameView.setText(userName);
        mCardNumView.setText(getFormatCardNum(cardNum));

        if(cardInfo!=null && !TextUtils.isEmpty(cardInfo.getHeadbankname())){
            T.d(cardInfo.toString());
            mBankNameView.setText(cardInfo.getHeadbankname());
        }

        findViewById(R.id.ll_bankname).setOnClickListener(this);
        findViewById(R.id.ll_city).setOnClickListener(this);
        findViewById(R.id.ll_subbank).setOnClickListener(this);
    }


    public static String getFormatCardNum(String src){

        StringBuilder sb1 =  new StringBuilder(src);
        int j = 1;
        for (int i = 1 ; i< sb1.length() ; i++){
            if(i%4 == 0){
                if(j*4 + j -1 <= sb1.length()) {
                    sb1.insert(j * 4 + (j - 1), " ");
                    j++;
                }
            }
        }
        return sb1.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            bindCard();
            return true;
        }
        if(id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(progressBar.getVisibility()== View.VISIBLE){
            return;
        }
        switch (v.getId()){
            case R.id.ll_bankname:
                getBankName();
                break;
            case R.id.ll_city:
                Intent city = new Intent(CardDetailActivity.this, ProvinceListAct.class);
                startActivityForResult(city, 0x01);
                break;
            case R.id.ll_subbank:
                getSubBankInfo();
                break;
        }
    }

    private void getBankName() {

        if(App.bankInfos!=null){
            startActivityForResult(new Intent(this , BankListAct.class) , 0x00);
        }else {
            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
            String url = App.getUtilsDomain() + ConstValue.BANK_LIST + "?token=" + ConstValue.userToken + "&caller=server";
            T.i("post url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    T.i("Cardinfo: " + response);
                    showProgress(false);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("respcd").equals("0000")) {
                            JSONArray banks = json.getJSONObject("data").getJSONArray("records");
                            App.bankInfos = new Gson().fromJson(banks.toString(), new TypeToken<List<BankInfo>>() {
                            }.getType());
                            startActivityForResult(new Intent(CardDetailActivity.this, BankListAct.class), 0x00);
                        } else {
                            Toast.makeText(CardDetailActivity.this, json.getString("resperr"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Toast.makeText(CardDetailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            showProgress(true);
            mQueue.add(stringRequest);
        }
    }

    private void getSubBankInfo(){

        if(cardInfo==null){
            Toast.makeText(this , "请先选择开户行" , Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(prov_code) || TextUtils.isEmpty(city_code)){
            Toast.makeText(this , "请先选择开户行地区" , Toast.LENGTH_SHORT).show();
            return;
        }
            RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
            String url = App.getUtilsDomain() + ConstValue.SUB_BANK_LIST + "?token=" + ConstValue.userToken + "&caller=server&cityid=" + city_code + "&headbankid=" + cardInfo.getHeadbankid();
            T.i("post url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    T.i("Cardinfo: " + response);
                    showProgress(false);
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getString("respcd").equals("0000")) {
                            JSONArray banks = json.getJSONObject("data").getJSONArray("records");
                            App.subBanks= new Gson().fromJson(banks.toString(), new TypeToken<List<SubBank>>() {
                            }.getType());
                            startActivityForResult(new Intent(CardDetailActivity.this, SubBankListAct.class), 0x02);
                        } else {
                            Toast.makeText(CardDetailActivity.this, json.getString("resperr"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    Toast.makeText(CardDetailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            showProgress(true);
            mQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x00:
                    T.w("onActivityResult");
                    cardInfo = (BankInfo) data.getSerializableExtra("BANKINFO");
                    bank_name = cardInfo.getHeadbankname();
                    bank_code = cardInfo.getHeadbankid();
                    mBankNameView.setText(bank_name);
                    break;
                case 0x01:
                    prov_code = data.getStringExtra("PROV_CODE").substring(0,2);
                    prov_name = data.getStringExtra("PROV_NAME");
                    city_code = data.getStringExtra("CITY_CODE").substring(0,4);
                    city_name = data.getStringExtra("CITY_NAME");
                    mAreaView.setText(prov_name + "-" + city_name);
                    break;
                case 0x02:
                    subBankName = data.getStringExtra("SUBBANKINFO");
                    mSubNameView.setText(subBankName);
                    break;
            }
        }
    }

    private void showProgress(boolean show){
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void bindCard(){
        if(cardInfo==null){
            Toast.makeText(this , "请先选择开户行" , Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(prov_code) || TextUtils.isEmpty(city_code)){
            Toast.makeText(this , "请先选择开户行地区" , Toast.LENGTH_SHORT).show();
            return;
        }


        if(TextUtils.isEmpty(subBankName)){
            Toast.makeText(this , "请完善银行卡信息后再提交" , Toast.LENGTH_SHORT).show();
            return;
        }



        AlertDialog.Builder builder = new AlertDialog.Builder(CardDetailActivity.this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setMessage("绑定成功");
        dialog.show();


        String url = "" ;
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                T.d("返回：" + response);
                JSONObject object;
                try {
                    object = new JSONObject(response);
                    if(object.getString("respcd").equals("0000")){
                        JSONObject data = (JSONObject) object.get("data");
                        //绑定成功
                        final Dialog dialog = new Dialog(CardDetailActivity.this,"提示", "银行卡绑定成功!");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                setResult(RESULT_OK);
                                finish();
                            }
                        });

                        showProgress(false);
                        dialog.show();

                    }else{
                        Toast.makeText(CardDetailActivity.this , object.has("respcd")?
                                        object.getString("resperr"):
                                        "",
                                Toast.LENGTH_SHORT ).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CardDetailActivity.this ,
                            "登录失败",
                            Toast.LENGTH_SHORT ).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(CardDetailActivity.this ,
                        "登录失败",
                        Toast.LENGTH_SHORT ).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("userid", BaseApplication.userInfo.getUser_id());
//                params.put("app_id", BaseApplication.userInfo.getAppid());
//                params.put("mchnt_id",BaseApplication.userInfo.getMchnt_id());
//                params.put("head_bank_name", cardInfo.getHeadbankname());
//                params.put("bank_user",userName );
//                params.put("bank_account",cardNum);
//                params.put("branch_bank_name" , subBankName);
                return params;
            }
        };

//        BaseApplication.queue.add(request);
    }

}
