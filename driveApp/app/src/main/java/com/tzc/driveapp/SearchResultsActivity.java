package com.tzc.driveapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.adapter.SearchResultAdapter;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.Question;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity {

    private static final String TAG = "SearchResultsActivity";

    private EditText etSearch;
    private TextView tvResultCount;
    private RecyclerView rvSearchResults;
    private LinearLayout loadingLayout;
    private LinearLayout emptyLayout;
    private MaterialCardView cardResultStats;
    private ProgressBar progressBar;

    private SearchResultAdapter adapter;
    private List<Question> searchResults = new ArrayList<>();
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        initView();
        setupListeners();

        // 获取传递的关键词
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("keyword")) {
            currentKeyword = intent.getStringExtra("keyword");
            if (currentKeyword != null && !currentKeyword.isEmpty()) {
                etSearch.setText(currentKeyword);
                performSearch(currentKeyword);
            }
        }
    }

    private void initView() {
        etSearch = findViewById(R.id.et_search);
        tvResultCount = findViewById(R.id.tv_result_count);
        rvSearchResults = findViewById(R.id.rv_search_results);
        loadingLayout = findViewById(R.id.loading_layout);
        emptyLayout = findViewById(R.id.empty_layout);
        cardResultStats = findViewById(R.id.card_result_stats);
        progressBar = findViewById(R.id.progress_bar);

        // 设置返回按钮
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // 初始化RecyclerView
        adapter = new SearchResultAdapter(searchResults, currentKeyword);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(adapter);

        // 设置点击监听
        adapter.setOnItemClickListener(position -> {
            if (position >= 0 && position < searchResults.size()) {
                Question question = searchResults.get(position);
                startQuizActivity("search", question);
            }
        });
    }

    private void setupListeners() {
        // 搜索按钮点击
        TextView btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                performSearch(keyword);
            } else {
                Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            }
        });

        // 搜索框回车键搜索
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    performSearch(keyword);
                }
                return true;
            }
            return false;
        });

        // 搜索框文本变化监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 可以在这里添加实时搜索功能
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 如果搜索框为空，清空搜索结果
                if (s.toString().trim().isEmpty()) {
                    clearSearchResults();
                }
            }
        });

        // 清空搜索按钮
        TextView btnClearSearch = findViewById(R.id.btn_clear_search);
        btnClearSearch.setOnClickListener(v -> {
            etSearch.setText("");
            clearSearchResults();
        });
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            return;
        }

        currentKeyword = keyword;

        // 显示加载状态
        showLoading(true);
        hideEmptyState();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Question>> call = apiService.searchQuestions(keyword);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    searchResults = response.body();
                    updateSearchResults();
                } else {
                    Log.e(TAG, "搜索失败: " + response.code() + " - " + response.message());
                    Toast.makeText(SearchResultsActivity.this,
                            "搜索失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "网络错误: " + t.getMessage());
                Toast.makeText(SearchResultsActivity.this,
                        "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            }
        });
    }

    private void updateSearchResults() {
        if (searchResults.isEmpty()) {
            showEmptyState();
            return;
        }

        // 更新适配器数据
        adapter.updateData(searchResults, currentKeyword);
        adapter.notifyDataSetChanged();

        // 显示结果统计
        cardResultStats.setVisibility(View.VISIBLE);
        tvResultCount.setText(String.format("找到 %d 个结果", searchResults.size()));

        // 显示结果列表
        rvSearchResults.setVisibility(View.VISIBLE);
        emptyLayout.setVisibility(View.GONE);
    }

    private void clearSearchResults() {
        searchResults.clear();
        adapter.updateData(searchResults, "");
        adapter.notifyDataSetChanged();

        // 隐藏结果统计
        cardResultStats.setVisibility(View.GONE);
        rvSearchResults.setVisibility(View.GONE);
        showEmptyState();
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.GONE);
            cardResultStats.setVisibility(View.GONE);
        } else {
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        emptyLayout.setVisibility(View.VISIBLE);
        rvSearchResults.setVisibility(View.GONE);
        cardResultStats.setVisibility(View.GONE);
    }

    private void hideEmptyState() {
        emptyLayout.setVisibility(View.GONE);
    }

    private void startQuizActivity(String mode, Question question) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("mode", mode);
        intent.putExtra("title", "搜索结果");
        intent.putExtra("questionId", question.getQuestionId());
        intent.putExtra("questionNo", question.getQuestionNo());
        intent.putExtra("subject", getSubjectNumber(question.getSubject()));
        startActivity(intent);
    }

    private int getSubjectNumber(String subject) {
        if (subject != null) {
            if (subject.contains("科目一")) {
                return 1;
            } else if (subject.contains("科目四")) {
                return 4;
            }
        }
        return 1; // 默认科目一
    }
}