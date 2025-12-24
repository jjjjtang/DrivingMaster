package com.tzc.driveapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tzc.driveapp.R;
import com.tzc.driveapp.adapter.IncorrectAdapter;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.IncorrectQuestion;
import com.tzc.driveapp.model.Question;
import com.tzc.driveapp.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncorrectQuestionsFragment extends Fragment {

    private static final String TAG = "IncorrectFragment";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private TextView tvError;
    private TextView tvTitleCount;
    private View errorLayout;

    private final List<IncorrectQuestion> incorrectQuestions = new ArrayList<>();
    private final List<Question> questionList = new ArrayList<>();
    private IncorrectAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_incorrect_simple, container, false);
        initView(view);
        setupRecyclerView();
        loadIncorrectQuestions();
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        tvError = view.findViewById(R.id.tv_error);
        tvTitleCount = view.findViewById(R.id.tv_title_count);
        errorLayout = view.findViewById(R.id.error_layout);

        swipeRefreshLayout.setOnRefreshListener(this::loadIncorrectQuestions);

        view.findViewById(R.id.btn_retry)
                .setOnClickListener(v -> loadIncorrectQuestions());
    }

    private void setupRecyclerView() {
        adapter = new IncorrectAdapter(questionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new IncorrectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Question question) {
                startQuizActivity(question);
            }

            @Override
            public void onDeleteClick(Question question) {
                for (IncorrectQuestion iq : incorrectQuestions) {
                    if (iq.getQuestion() != null
                            && iq.getQuestion().getQuestionId() != null
                            && iq.getQuestion().getQuestionId().equals(question.getQuestionId())) {
                        deleteIncorrectQuestion(iq);
                        break;
                    }
                }
            }
        });
    }

    private void loadIncorrectQuestions() {
        showLoading(true);
        showEmpty(false);
        showError(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<IncorrectQuestion>> call = apiService.getMyIncorrectQuestions();

        call.enqueue(new Callback<List<IncorrectQuestion>>() {
            @Override
            public void onResponse(
                    Call<List<IncorrectQuestion>> call,
                    Response<List<IncorrectQuestion>> response
            ) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);

                Log.d(TAG, "响应码: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    tryFallbackApi();
                }
            }

            @Override
            public void onFailure(Call<List<IncorrectQuestion>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showLoading(false);
                showError(true);
                tvError.setText("网络错误：" + t.getMessage());
                Log.e(TAG, "load failed", t);
            }
        });
    }

    private void tryFallbackApi() {
        Long userId = SharedPreferencesUtils.getUserId(getContext());
        if (userId == null || userId == 0) {
            showError(true);
            tvError.setText("请先登录");
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getIncorrectListByUserId(userId)
                .enqueue(new Callback<List<IncorrectQuestion>>() {
                    @Override
                    public void onResponse(
                            Call<List<IncorrectQuestion>> call,
                            Response<List<IncorrectQuestion>> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            bindData(response.body());
                        } else {
                            showError(true);
                            tvError.setText("加载失败：" + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IncorrectQuestion>> call, Throwable t) {
                        showError(true);
                        tvError.setText("网络错误：" + t.getMessage());
                    }
                });
    }

    private void bindData(List<IncorrectQuestion> data) {
        incorrectQuestions.clear();
        incorrectQuestions.addAll(data);

        questionList.clear();
        for (IncorrectQuestion iq : incorrectQuestions) {
            if (iq.getQuestion() != null) {
                questionList.add(iq.getQuestion());
            }
        }

        adapter.notifyDataSetChanged();
        updateTitleCount();

        if (questionList.isEmpty()) {
            showEmpty(true);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void deleteIncorrectQuestion(IncorrectQuestion incorrectQuestion) {
        if (incorrectQuestion.getId() == null) {
            Toast.makeText(getContext(), "错题ID为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getClient()
                .create(ApiService.class)
                .deleteIncorrectQuestion(incorrectQuestion.getId())
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            incorrectQuestions.remove(incorrectQuestion);
                            bindData(incorrectQuestions);
                            Toast.makeText(getContext(), "已移除错题", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startQuizActivity(Question question) {
        Intent intent = new Intent(getActivity(), com.tzc.driveapp.QuizActivity.class);
        intent.putExtra("mode", "incorrect");
        intent.putExtra("title", "错题练习");
        intent.putExtra("questionId", question.getQuestionId());
        startActivity(intent);
    }

    private void updateTitleCount() {
        tvTitleCount.setText("错题(" + questionList.size() + ")");
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) recyclerView.setVisibility(View.GONE);
    }

    private void showEmpty(boolean show) {
        tvEmpty.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) recyclerView.setVisibility(View.GONE);
    }

    private void showError(boolean show) {
        errorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) recyclerView.setVisibility(View.GONE);
    }
}