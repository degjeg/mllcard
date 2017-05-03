package attendance.mll.com.check;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import attendance.mll.com.check.http.Net;
import attendance.mll.com.check.utils.T;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    EditText etAccount, etPassword;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        T.init(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        etAccount = (EditText) findViewById(R.id.et_acc);
        etPassword = (EditText) findViewById(R.id.et_pw);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnLongClickListener(this);

        SharedPreferences p = getSharedPreferences(Constants.PREF_, MODE_PRIVATE);
        String account = p.getString(Constants.PREF_ACCOUNT, "");
        String password = p.getString(Constants.PREF_PASSWORD, "");

        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            etAccount.setText(account);
            etPassword.setText(password);

            doLogin(account, password);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                onClickLogin();
        }
    }

    private void onClickLogin() {
        final String account = etAccount.getText().toString();
        final String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(password)) {
            T.show("账号或者密码不正确");
            return;
        }

        SharedPreferences p = getSharedPreferences(Constants.PREF_, MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putString(Constants.PREF_ACCOUNT, account);
        editor.putString(Constants.PREF_PASSWORD, password);
        editor.commit();
        doLogin(account, password);
    }

    private void dissmissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private void doLogin(final String account, final String password) {
        dialog = ProgressDialog.show(this, "正在登录", "请稍等...");

        Net.login(account, password, new Net.LoginHandler() {
            @Override
            public void onLoginSuccess() {
                T.show("登录成功");
                // getCardTime();
                startActivity(new Intent(MainActivity.this, CardListActivity.class));
                dissmissDialog();
            }

            @Override
            public void onLoginFail(String description) {
                T.show(description);
                dissmissDialog();
            }
        });
    }


    @Override
    public boolean onLongClick(View v) {
        startActivity(new Intent(this, SoundListActivity.class));
        return true;
    }
}
