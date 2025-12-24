package com.tzc.driveapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tzc.driveapp.fragments.HomeFragment;
import com.tzc.driveapp.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private TextView tvTitle;
    private MaterialButtonToggleGroup toggleSubject;
    private MaterialButton btnSubject1;
    private MaterialButton btnSubject4;
    private BottomNavigationView bottomNavigation;
    private SharedPreferences sharedPreferences;

    // 当前科目（1或4）
    private int currentSubject = 1;
    // 当前Fragment
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查登录状态
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null || token.isEmpty()) {
            // 未登录，跳转到登录页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        initView();
        setupStatusBar();
        setupSubjectToggle();
        setupBottomNavigation();

        // 默认加载首页Fragment
        loadFragment(new HomeFragment());
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        toggleSubject = findViewById(R.id.toggle_subject);
        btnSubject1 = findViewById(R.id.btn_subject_1);
        btnSubject4 = findViewById(R.id.btn_subject_4);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupStatusBar() {
        View statusBar = findViewById(R.id.status_bar);
        ViewGroup.LayoutParams params = statusBar.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBar.setLayoutParams(params);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setupSubjectToggle() {
        toggleSubject.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_subject_1) {
                    currentSubject = 1;
                    updateSubjectButtonStyle(btnSubject1, btnSubject4);
                } else if (checkedId == R.id.btn_subject_4) {
                    currentSubject = 4;
                    updateSubjectButtonStyle(btnSubject4, btnSubject1);
                }
                // 通知当前Fragment科目已更改
                if (currentFragment instanceof BaseFragment) {
                    ((BaseFragment) currentFragment).onSubjectChanged(currentSubject);
                }
            }
        });
    }

    private void updateSubjectButtonStyle(MaterialButton checkedBtn, MaterialButton uncheckedBtn) {

        checkedBtn.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.primary_color));
        checkedBtn.setTextColor(Color.WHITE);

        uncheckedBtn.setBackgroundTintList(
                ContextCompat.getColorStateList(this, R.color.white));
        uncheckedBtn.setTextColor(
                ContextCompat.getColor(this, R.color.text_secondary));
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                tvTitle.setText("PDD互砍");
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                tvTitle.setText("个人中心");
                loadFragment(new ProfileFragment());
                return true;
            }
            return false;
        });

        // 重新点击当前项时刷新
        bottomNavigation.setOnNavigationItemReselectedListener(item -> {
            if (currentFragment instanceof BaseFragment) {
                ((BaseFragment) currentFragment).onSubjectChanged(currentSubject);
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        currentFragment = fragment;
    }

    // 提供给Fragment使用的公共方法
    public int getCurrentSubject() {
        return currentSubject;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    // Fragment基类
    public static abstract class BaseFragment extends Fragment {
        public void onSubjectChanged(int newSubject) {
            // 子类可重写此方法
        }
    }
}