package com.tzc.driveapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.tzc.driveapp.MainActivity;
import com.tzc.driveapp.QuizActivity;
import com.tzc.driveapp.R;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeSelectFragment extends MainActivity.BaseFragment {

    private MaterialButtonToggleGroup toggleSubject;
    private MaterialButton btnSubject1;
    private MaterialButton btnSubject4;

    private LinearLayout layoutSubject1;
    private LinearLayout layoutSubject4;
    private LinearLayout loadingLayout;

    // 科目一题型卡片
    private CardView cardSingleChoice1;
    private CardView cardMultipleChoice1;
    private CardView cardTrueFalse1;

    // 科目四题型卡片
    private CardView cardSingleChoice4;
    private CardView cardMultipleChoice4;
    private CardView cardTrueFalse4;

    // 当前科目
    private int currentSubject = 1;

    // 题型数据
    private final String[] questionTypes = {"单选题", "多选题", "判断题"};
    private final int[] subject1QuestionCounts = {1200, 500, 400};
    private final int[] subject4QuestionCounts = {800, 300, 200};
    private final int[] typeIcons = {
            R.drawable.ic_single_choice,
            R.drawable.ic_multiple_choice,
            R.drawable.ic_true_false
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_select, container, false);
        initView(view);
        setupSubjectToggle();
        setupTypeCards();
        return view;
    }

    private void initView(View view) {
        toggleSubject = view.findViewById(R.id.toggle_subject);
        btnSubject1 = view.findViewById(R.id.btn_subject_1);
        btnSubject4 = view.findViewById(R.id.btn_subject_4);

        layoutSubject1 = view.findViewById(R.id.layout_subject_1);
        layoutSubject4 = view.findViewById(R.id.layout_subject_4);
        loadingLayout = view.findViewById(R.id.loading_layout);

        // 科目一题型卡片
        cardSingleChoice1 = view.findViewById(R.id.card_single_choice_1);
        cardMultipleChoice1 = view.findViewById(R.id.card_multiple_choice_1);
        cardTrueFalse1 = view.findViewById(R.id.card_true_false_1);

        // 科目四题型卡片
        cardSingleChoice4 = view.findViewById(R.id.card_single_choice_4);
        cardMultipleChoice4 = view.findViewById(R.id.card_multiple_choice_4);
        cardTrueFalse4 = view.findViewById(R.id.card_true_false_4);
    }

    private void setupSubjectToggle() {
        toggleSubject.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_subject_1) {
                    currentSubject = 1;
                    switchToSubject1();
                } else if (checkedId == R.id.btn_subject_4) {
                    currentSubject = 4;
                    switchToSubject4();
                }
            }
        });
    }

    private void switchToSubject1() {
        layoutSubject1.setVisibility(View.VISIBLE);
        layoutSubject4.setVisibility(View.GONE);
        updateQuestionCounts(subject1QuestionCounts, true);
    }

    private void switchToSubject4() {
        layoutSubject1.setVisibility(View.GONE);
        layoutSubject4.setVisibility(View.VISIBLE);
        updateQuestionCounts(subject4QuestionCounts, false);
    }

    private void setupTypeCards() {
        // 设置科目一卡片监听器
        cardSingleChoice1.setOnClickListener(v -> startTypeQuiz("单选题"));
        cardMultipleChoice1.setOnClickListener(v -> startTypeQuiz("多选题"));
        cardTrueFalse1.setOnClickListener(v -> startTypeQuiz("判断题"));

        // 设置科目四卡片监听器
        cardSingleChoice4.setOnClickListener(v -> startTypeQuiz("单选题"));
        cardMultipleChoice4.setOnClickListener(v -> startTypeQuiz("多选题"));
        cardTrueFalse4.setOnClickListener(v -> startTypeQuiz("判断题"));

        // 初始化题目数量显示
        updateQuestionCounts(subject1QuestionCounts, true);
    }

    private void updateQuestionCounts(int[] counts, boolean isSubject1) {
        if (isSubject1) {
            updateCardCount(cardSingleChoice1, counts[0]);
            updateCardCount(cardMultipleChoice1, counts[1]);
            updateCardCount(cardTrueFalse1, counts[2]);
        } else {
            updateCardCount(cardSingleChoice4, counts[0]);
            updateCardCount(cardMultipleChoice4, counts[1]);
            updateCardCount(cardTrueFalse4, counts[2]);
        }
    }

    private void updateCardCount(CardView card, int count) {
        if (card != null) {
            View cardView = card.getChildAt(0);
            if (cardView instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) cardView;
                TextView countView = layout.findViewById(R.id.tv_question_count);
                if (countView != null) {
                    countView.setText(count + "题");
                }
            }
        }
    }

    private void startTypeQuiz(String type) {
        // 显示加载状态
        showLoading(true);

        // 测试题型接口
        testTypeApi(type);
    }
    private void testTypeApi(String type) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 直接使用题型刷题接口
        Call<List<Question>> call = apiService.getQuestionsByType(type, 1);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questions = response.body();
                    if (!questions.isEmpty()) {
                        // 成功获取到题目，跳转到刷题页面
                        navigateToQuizActivity(type);
                    } else {
                        // 如果返回空数组，可能是分页问题
                        showNoQuestionsDialog(type);
                    }
                } else {
                    Toast.makeText(getContext(), "加载题目失败: " + response.code(), Toast.LENGTH_SHORT).show();

                    // 如果是404，可能是接口格式问题，尝试其他格式
                    if (response.code() == 404) {
                        tryAlternativeTypeFormat(type);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 尝试其他可能的题型格式
    private void tryAlternativeTypeFormat(String type) {
        String alternativeType = type;

        // 尝试不同的题型名称格式
        switch (type) {
            case "单选题":
                alternativeType = "选择题";
                break;
            case "多选题":
                alternativeType = "多选";
                break;
            case "判断题":
                alternativeType = "判断";
                break;
        }

        // 重新尝试请求
        testTypeApiWithType(alternativeType);
    }

    private void testTypeApiWithType(String type) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Question>> call = apiService.getQuestionsByType(type, 1);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questions = response.body();
                    if (!questions.isEmpty()) {
                        navigateToQuizActivity(type);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                // 如果还是失败，允许用户进入空模式
                Toast.makeText(getContext(), "使用本地题目进行练习", Toast.LENGTH_SHORT).show();
                navigateToQuizActivity(type);
            }
        });
    }

    private void showNoQuestionsDialog(String type) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("提示")
                .setMessage("当前" + type + "暂无题目数据，是否仍要进入练习模式？")
                .setPositiveButton("进入", (dialog, which) -> navigateToQuizActivity(type))
                .setNegativeButton("取消", null)
                .show();
    }

    private void navigateToQuizActivity(String type) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("mode", "type");
            intent.putExtra("subject", currentSubject);
            intent.putExtra("title", type + "练习");
            intent.putExtra("questionType", type); // 传递题型参数
            startActivity(intent);
        }
    }

    private void showLoading(boolean show) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onSubjectChanged(int newSubject) {
        if (currentSubject != newSubject) {
            currentSubject = newSubject;

            if (toggleSubject != null) {
                if (newSubject == 1 && btnSubject1 != null) {
                    toggleSubject.check(R.id.btn_subject_1);
                    switchToSubject1();
                } else if (newSubject == 4 && btnSubject4 != null) {
                    toggleSubject.check(R.id.btn_subject_4);
                    switchToSubject4();
                }
            }
        }
    }
}