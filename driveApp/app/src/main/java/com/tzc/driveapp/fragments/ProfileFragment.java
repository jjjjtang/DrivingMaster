package com.tzc.driveapp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tzc.driveapp.LoginActivity;
import com.tzc.driveapp.MainActivity;
import com.tzc.driveapp.R;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends MainActivity.BaseFragment {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvUserStatus;
    private TextView tvHistoryCount;
    private TextView tvAvgScore;
    private Button btnLogout;

    private View btnCollection;
    private View btnCustomerService;
    private View btnAbout;

    // 添加卡片视图变量
    private CardView cardHistoryCount;
    private CardView cardAvgScore;

    private SharedPreferences sharedPreferences;
    private Long userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (getActivity() instanceof MainActivity) {
            sharedPreferences = ((MainActivity) getActivity()).getSharedPreferences();
            userId = sharedPreferences.getLong("userId", 0);
        }

        initView(view);
        setupListeners();
        loadUserData();
        return view;
    }

    private void initView(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvUserStatus = view.findViewById(R.id.tv_user_status);

        btnLogout = view.findViewById(R.id.btn_logout);

        btnCollection = view.findViewById(R.id.btn_collection);
        btnCustomerService = view.findViewById(R.id.btn_customer_service);
        btnAbout = view.findViewById(R.id.btn_about);


    }

    private void setupListeners() {

        // 历史答题数量点击
        if (cardHistoryCount != null) {
            cardHistoryCount.setOnClickListener(v -> navigateToHistoryRecord());
        }

        // 考试平均分点击
        if (cardAvgScore != null) {
            cardAvgScore.setOnClickListener(v -> navigateToScoreHistory());
        }

// 我的错题
        if (btnCollection != null) {
            btnCollection.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new IncorrectQuestionsFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

// 做题记录
        if (btnCustomerService != null) {
            btnCustomerService.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, new HistoryRecordFragment())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
        // 关于我们
        if (btnAbout != null) {
            btnAbout.setOnClickListener(v -> navigateToAbout());
        }

        // 退出登录
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    private void loadUserData() {
        // 从SharedPreferences加载基本信息
        String username = sharedPreferences.getString("username", "用户");
        if (tvUsername != null) {
            tvUsername.setText(username);
        }

        if (getActivity() instanceof MainActivity) {
            int subject = ((MainActivity) getActivity()).getCurrentSubject();
            if (tvUserStatus != null) {
                tvUserStatus.setText("科目" + subject + "备考中");
            }
        }

        // 从API加载详细数据
        loadUserInfoFromApi();
    }

    private void loadUserInfoFromApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserInfo(userId);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (tvHistoryCount != null) {
                        tvHistoryCount.setText(String.valueOf(user.getTotal_num()));
                    }
                    if (tvAvgScore != null) {
                        tvAvgScore.setText(String.format("%.1f", user.getAverage_score()));
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "加载用户信息失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToHistoryRecord() {
        // TODO: 跳转到历史记录页面
        Toast.makeText(getContext(), "历史记录功能开发中", Toast.LENGTH_SHORT).show();
    }

    private void navigateToScoreHistory() {
        // TODO: 跳转到成绩记录页面
        Toast.makeText(getContext(), "成绩记录功能开发中", Toast.LENGTH_SHORT).show();
    }


    private void navigateToAbout() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), com.tzc.driveapp.AboutActivity.class);
            startActivity(intent);
        }
    }

    private void logout() {
        // 1. 清除登录信息（JWT）
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        // 2. 重置 ApiClient（非常关键）
        ApiClient.reset();

        // 3. 跳转到登录页
        startActivity(new Intent(getActivity(), LoginActivity.class));
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onSubjectChanged(int newSubject) {
        // 更新状态文本
        if (tvUserStatus != null) {
            tvUserStatus.setText("科目" + newSubject + "备考中");
        }
    }
}