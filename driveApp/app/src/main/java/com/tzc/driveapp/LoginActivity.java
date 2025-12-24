package com.tzc.driveapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.dao.user.UserLoginRequest;
import com.tzc.driveapp.model.dao.user.UserLoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // 检查是否已登录
        String token = sharedPreferences.getString("token", null);
        if (token != null && !token.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        initView();
        setupListeners();
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> register());
        tvForgotPassword.setOnClickListener(v -> forgotPassword());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserLoginResponse> call = apiService.login(request);

        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserLoginResponse loginResponse = response.body();

                    // 保存token和用户信息
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", loginResponse.getToken());
                    editor.putLong("userId", loginResponse.getUserId());
                    editor.putString("username", loginResponse.getUsername());
                    editor.putBoolean("isAdmin", loginResponse.isRole());
                    editor.apply();

                    // 跳转到主页面
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();

                    Toast.makeText(LoginActivity.this, loginResponse.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register() {
        // TODO: 跳转到注册页面
        Toast.makeText(this, "注册功能开发中", Toast.LENGTH_SHORT).show();
    }

    private void forgotPassword() {
        // TODO: 跳转到忘记密码页面
        Toast.makeText(this, "忘记密码功能开发中", Toast.LENGTH_SHORT).show();
    }
}