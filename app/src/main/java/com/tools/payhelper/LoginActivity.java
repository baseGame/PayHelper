package com.tools.payhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.tools.payhelper.bean.LoginBean;
import com.tools.payhelper.utils.EncrptyUtil;
import com.tools.payhelper.utils.HttpUtil;
import com.tools.payhelper.utils.LoadingDialog;
import com.tools.payhelper.utils.LogUtil;
import com.tools.payhelper.utils.SharedUtil;
import com.tools.payhelper.utils.SystemUtil;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class LoginActivity extends Activity {

    private int REQUEST_READ_PHONE_STATE = 1000;

    private EditText username, pwd;

    private LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        dialog = new LoadingDialog.Builder(this).craeteDialog();

        username = (EditText) findViewById(R.id.username);
        pwd = (EditText) findViewById(R.id.password);
        if (HttpUtil.isDebug) {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.baseurlgroup);
            radioGroup.setVisibility(View.VISIBLE);
            radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
                switch (checkedId) {
                    case R.id.baseurl3:
                        HttpUtil.getInstance().setBaseURL(HttpUtil.SC1);
                        break;
                    case R.id.baseurl1:
                        HttpUtil.getInstance().setBaseURL(HttpUtil.MS);
                        break;
                    case R.id.baseurl2:
                        HttpUtil.getInstance().setBaseURL(HttpUtil.JX);
                        break;
                    case R.id.baseurl5:
                        HttpUtil.getInstance().setBaseURL(HttpUtil.SC1_1);
                        break;
                }
            });
        }
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener((View v) -> {
            //测试时直接跳转，跳过登录操作
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (hasPermission()) {
                    doLogin(username.getText().toString(), pwd.getText().toString());
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                }
            } else {
                doLogin(username.getText().toString(), pwd.getText().toString());
            }
        });

        String usernameText = SharedUtil.getString(LoginActivity.this, SharedUtil.USERNAME);
        String pwdText = SharedUtil.getString(LoginActivity.this, SharedUtil.PASSWORD);

        if (!TextUtils.isEmpty(usernameText) && !TextUtils.isEmpty(pwdText)) {
            username.setText(usernameText);
            pwd.setText(pwdText);
        }
    }

    private boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (permissions.length > 0 && grantResults.length > 0 && Manifest.permission.READ_PHONE_STATE.equals(permissions[0]) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doLogin(username.getText().toString(), pwd.getText().toString());
            } else {
                Toast.makeText(LoginActivity.this, "获取权限失败,无法登录.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void doLogin(final String username, final String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "用户名密码不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        dialog.show();
        try {
            HttpUtils httpUtils = new HttpUtils(HttpUtil.TIME_OUT);
            RequestParams requestParams = new RequestParams();
            requestParams.addBodyParameter("username", username);
            requestParams.addBodyParameter("password", EncrptyUtil.md5(password).substring(0, 16));
            requestParams.addBodyParameter("deviceno", SystemUtil.getDeviceUUID(this));
//            requestParams.setBodyEntity(new StringEntity(jsonObject.toString(), "UTF-8"));
//            requestParams.setContentType("application/json");
            LogUtil.d("login url : " + HttpUtil.getInstance().getLOGIN());
            httpUtils.send(HttpRequest.HttpMethod.POST, HttpUtil.getInstance().getLOGIN(), requestParams, new RequestCallBack<Object>() {
                @Override
                public void onSuccess(ResponseInfo<Object> responseInfo) {
                    dialog.dismiss();
                    String result = (String) responseInfo.result;
                    LogUtil.d("login success : " + result);

                    Gson gson = new Gson();
                    LoginBean loginBean = gson.fromJson(result, LoginBean.class);
                    if (loginBean != null) {
                        if (loginBean.getCode() == 0) {
                            SharedUtil.putString(LoginActivity.this, SharedUtil.USERNAME, username);
                            SharedUtil.putString(LoginActivity.this, SharedUtil.PASSWORD, password);
                            SharedUtil.putString(LoginActivity.this, SharedUtil.MD5_KEY, String.valueOf(loginBean.getData().getMd5key()));
                            SharedUtil.putString(LoginActivity.this, SharedUtil.AES_KEY, String.valueOf(loginBean.getData().getAeskey()));
                            SharedUtil.putString(LoginActivity.this, SharedUtil.ID, String.valueOf(loginBean.getData().getId()));
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败:" + loginBean.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    dialog.dismiss();
                    Toast.makeText(LoginActivity.this, "登录失败:" + s, Toast.LENGTH_LONG).show();
                    LogUtil.d(s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
