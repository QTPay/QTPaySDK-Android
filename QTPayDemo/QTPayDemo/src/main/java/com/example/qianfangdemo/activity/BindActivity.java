package com.example.qianfangdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.qianfangdemo.Utils.T;
import com.example.qianfangdemo.Utils.Utils;
import com.example.qianfangdemo.base.App;
import com.example.qianfangdemo.base.ConstValue;
import com.example.qianfangdemo.entity.CardInfo;
import com.gc.materialdesign.views.ProgressBarIndeterminate;

import org.json.JSONException;
import org.json.JSONObject;

import qfpay.wxshop.R;


public class BindActivity extends AppCompatActivity {

    private EditText mUserNameView;
    private EditText mCardNumView;
    private TextView mErrorView;
    CardInfo card ;
    private ProgressBarIndeterminate progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mUserNameView = (EditText) findViewById(R.id.et_username);
        mCardNumView = (EditText) findViewById(R.id.et_cardnum);

        fillBankNumSpeace(mCardNumView);
        mErrorView = (TextView) findViewById(R.id.errormsg);
        progressBar= (ProgressBarIndeterminate) findViewById(R.id.progressBarIndeterminate);

        Button nextBt = (Button) findViewById(R.id.bt_next);
        nextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(progressBar.getVisibility() == View.VISIBLE){
                    return;
                }
                getCardBin();
            }
        });

        mCardNumView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == R.id.next || actionId == EditorInfo.IME_NULL) {
                    getCardBin();
                    return true;
                }
                return false;
            }
        });
    }


    private void showProgress(boolean show){
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void getCardBin(){

        //reset error msg
        mErrorView.setText("");

        final String userName  =  mUserNameView.getText().toString();
        if(TextUtils.isEmpty(userName) || !isInvildName(userName)){
            mErrorView.setText("* 用户名格式不正确");
            shake(this,mUserNameView);
            return;
        }

        final String cardNum = mCardNumView.getText().toString().replaceAll(" ", "");
        if(TextUtils.isEmpty(cardNum) || !isInvildCard(cardNum)){
            mErrorView.setText("* 银行卡号格式不正确");
            shake(this, mCardNumView);
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        String url  = App.getUtilsDomain() + ConstValue.CARD_INFO + "?token="+ ConstValue.userToken+"&caller=server&q="+cardNum.substring(0,6);
        T.i("post url: " + url);
        StringRequest stringRequest =  new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                T.i("Cardinfo: " + response);
                showProgress(false);
                try {
                    JSONObject json = new JSONObject(response);
                    if(json.getString("respcd").equals("0000")){
                        JSONObject cardInfo = (JSONObject) json.getJSONObject("data").getJSONArray("records").get(0);
                        card = new CardInfo();
                        card.setCardtype(cardInfo.getString("cardtype"));
                        card.setCsphone(cardInfo.getString("csphone"));
                        card.setHeadbankname(cardInfo.getString("headbankname"));
                        card.setHeadbankid(cardInfo.getString("headbankid"));
                        card.setIscommon(cardInfo.getString("iscommon"));

                    }else{
                        Toast.makeText(BindActivity.this, json.getString("resperr"), Toast.LENGTH_SHORT).show();

                    }

                    jumtoDetail(userName, cardNum);

                } catch (JSONException e) {
                    e.printStackTrace();
                    jumtoDetail(userName, cardNum);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(BindActivity.this , error.toString() , Toast.LENGTH_SHORT).show();
                T.w(error.toString());
                jumtoDetail(userName, cardNum);
            }
        });
        showProgress(true);
        mQueue.add(stringRequest);

    }

    private void jumtoDetail(String userName, String cardNum){
        Intent intent = new Intent(BindActivity.this , CardDetailActivity.class);
        if(card!=null) {
            intent.putExtra("CARDINFO", card);
        }
        intent.putExtra("USERNAME", userName);
        intent.putExtra("CARDNUM" , cardNum);
        startActivityForResult(intent, 0x00);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0x00){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }

    }

    private boolean isInvildCard(String num) {
        return num.length()>15 && num.length()<24;
    }

    private boolean isInvildName(String name) {
        return name.length()>1 && Utils.isAllHanzi(name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bind, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void shake(Context context, View view) {
        // TODO Auto-generated method stub
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        shake.reset();
        shake.setFillAfter(true);
        view.startAnimation(shake);
    }


    public  static  void  fillBankNumSpeace(final  EditText mAddCardNumEdt){//银行卡补齐 空格
        mAddCardNumEdt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (count == 1) {
                    if (s.length() == 4) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(5);
                    }
                    if (s.length() == 9) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(10);
                    }
                    if (s.length() == 14) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(15);
                    }
                    if (s.length() == 19) {
                        mAddCardNumEdt.setText(s + " ");
                        mAddCardNumEdt.setSelection(20);
                    }
                } else if (count == 0) {
                    if (s.length() == 4) {
                        mAddCardNumEdt.setText(s.subSequence(0,
                                s.length() - 1));
                        mAddCardNumEdt.setSelection(3);
                    }
                    if (s.length() == 9) {
                        mAddCardNumEdt.setText(s.subSequence(0,
                                s.length() - 1));
                        mAddCardNumEdt.setSelection(8);
                    }
                    if (s.length() == 14) {
                        mAddCardNumEdt.setText(s.subSequence(0,
                                s.length() - 1));
                        mAddCardNumEdt.setSelection(13);
                    }
                    if (s.length() == 19) {
                        mAddCardNumEdt.setText(s.subSequence(0,
                                s.length() - 1));
                        mAddCardNumEdt.setSelection(18);
                    }
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
