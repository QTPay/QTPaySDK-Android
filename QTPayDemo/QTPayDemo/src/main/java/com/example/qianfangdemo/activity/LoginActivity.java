package com.example.qianfangdemo.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.qianfangdemo.view.ClearEditText;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.qfpay.sdk.common.QTEnviroment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import qfpay.wxshop.R;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.username)
    private ClearEditText phoneNumber;
    @ViewInject(R.id.login)
    private Button button;

    @ViewInject(R.id.setting)
    private TextView setting;

    private boolean isLoading;

    @ViewInject(R.id.version)
    private TextView version;

    @ViewInject(R.id.qt_title)
    private  View title;

    private Dialog dialog;

    long currentTime = 0;
    long touchTime = 0;
    long waitTime =  800;
    int pressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        checkMerchantBtnCanPress();
        dialog = new Dialog(LoginActivity.this, R.style.DialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.my_dialog);
        dialog.setCancelable(false);

        TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void afterTextChanged(Editable arg0) {
                // 检查商户的登录按钮是否可以点击
                checkMerchantBtnCanPress();
            }
        };

        if(App.sharedPreferencesAccess.getBoolean("Debug" , false)){
            setting.setVisibility(View.VISIBLE);
        }else{
            setting.setVisibility(View.INVISIBLE);
        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = System.currentTimeMillis();
                if ((currentTime - touchTime) >= waitTime) {
                    touchTime = currentTime;
                    pressCount =0;
                } else if (pressCount < 3) {
                    touchTime = currentTime;
                    pressCount++;
                } else {
                    pressCount = 0;

                    if(setting.getVisibility() == View.INVISIBLE) {
                        Toast.makeText(LoginActivity.this, "调试模式已开启", Toast.LENGTH_SHORT).show();
                        App.sharedPreferencesAccess.putBoolean("Debug" , true);
                        setting.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(LoginActivity.this, "调试模式已关闭", Toast.LENGTH_SHORT).show();
                        App.sharedPreferencesAccess.putBoolean("Debug" , false);
                        setting.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        phoneNumber.addTextChangedListener(watcher);
        PackageManager manager =getPackageManager();
        String versionNo = "";
        try {
            versionNo = manager.getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version.setText("V " + versionNo);
        T.i(App.getCurrentUserConfig().toString());
    }

    @OnClick(R.id.login)
    public void onLoginClick(View view) {
        login();
    }

    @OnClick(R.id.setting)
    public void onSetClick(View view) {
        startActivity(new Intent(this, SetConfigActivity.class));
    }


    // 登录
    private void login() {

        if (!Utils.isCanConnectionNetWork(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "网络连接异常！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!button.isSelected() || isLoading) {
            return;
        }

        if (dialog != null) {
            dialog.show();
            isLoading = true;
        }

        final int evn = App.getIndex()[0];
        StringRequest req = new StringRequest(Method.POST, App.getDomain() + "/createtoken", new Listener<String>() {
            @Override
            public void onResponse(String arg0) {
                try {
                    T.d(arg0.toString());

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        isLoading = false;
                    }

                    try {
                        JSONObject tokenJson = new JSONObject(arg0);
                        ConstValue.userToken = tokenJson.getString("token");
                        ConstValue.mobile = phoneNumber.getText().toString();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "登陆失败, 请检查配置", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //配置前台SDK
                    mqt.setUserToken(ConstValue.userToken);
                    switch (evn) {
                        case 0:
                            mqt.setEnviroment(QTEnviroment.WORK);
                            mqt.setAppInfo(App.getAppCod(), ConstValue.apiKey, ConstValue.wxAppId);
                            break;
                        case 1:
                            mqt.setEnviroment(QTEnviroment.SANDBOX);
                            mqt.setAppInfo(App.getAppCod(), ConstValue.apiKey, ConstValue.wxAppId);
                            break;
                        case 2:
                            mqt.setEnviroment(QTEnviroment.QA);
                            mqt.setAppInfo(App.getAppCod(), ConstValue.apiKey, ConstValue.wxAppId);
                            break;
                        case 3:
                            mqt.setEnviroment(QTEnviroment.DEV);
                            mqt.setAppInfo(App.getAppCod(), ConstValue.apiKey_dev, ConstValue.wxAppId);
                            break;
                        default:
                            break;
                    }
                    startActivity(new Intent(LoginActivity.this, SelectActivity.class));
                    finish();

                } catch (Exception e) {
                    T.d(e.toString());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    isLoading = false;
                }
                Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
                T.d(arg0.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", phoneNumber.getText().toString().trim());
                params.put("app_code", App.getAppCod());
                params.put("out_mchnt", App.getOutMerchant());
                return params;
            }

        };
        mQueue.add(req);

    }


    private void checkMerchantBtnCanPress() {
        String usernameString = phoneNumber.getText().toString().trim();
        if (usernameString != null && !usernameString.equals("")) {
            button.setSelected(true);
            return;
        } else {
            button.setSelected(false);
        }
    }

}
