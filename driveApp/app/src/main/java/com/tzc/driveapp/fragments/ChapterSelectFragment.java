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

public class ChapterSelectFragment extends MainActivity.BaseFragment {

    private MaterialButtonToggleGroup toggleSubject;
    private MaterialButton btnSubject1;
    private MaterialButton btnSubject4;

    private LinearLayout layoutSubject1;
    private LinearLayout layoutSubject4;
    private LinearLayout loadingLayout;

    // 科目一章节卡片
    private CardView cardChapter1;
    private CardView cardChapter2;
    private CardView cardChapter3;
    private CardView cardChapter4;
    private CardView cardChapter5;
    private CardView cardChapter6;

    // 科目四章节卡片
    private CardView cardSubject4Chapter1;
    private CardView cardSubject4Chapter2;
    private CardView cardSubject4Chapter3;
    private CardView cardSubject4Chapter4;
    private CardView cardSubject4Chapter5;
    private CardView cardSubject4Chapter6;
    private CardView cardSubject4Chapter7;

    // 当前科目
    private int currentSubject = 1;

    // 科目一章节数据
    private final String[] subject1Chapters = {
            "驾驶证和机动车管理规定294题",
            "道路通行条件及通行规定1005题",
            "道路交通安全违法行为及处罚94题",
            "道路交通事故处理相关规定54题",
            "机动车机基础知识151题",
            "交通事故救护常识28题"
    };

    // 科目四章节数据
    private final String[] subject4Chapters = {
            "安全行车常识330题",
            "文明行车常识 (149题)",
            "道路交通信号在交通场景中的综合应用241题",
            "恶劣气象和复杂道路条件下安全驾驶知识428题",
            "紧急情况下避险常识127题",
            "防范次生事故处置与伤员急救知识62题",
            "典型事故案例分析37题"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter_select, container, false);
        initView(view);
        setupSubjectToggle();
        setupChapterListeners();
        return view;
    }

    private void initView(View view) {
        toggleSubject = view.findViewById(R.id.toggle_subject);
        btnSubject1 = view.findViewById(R.id.btn_subject_1);
        btnSubject4 = view.findViewById(R.id.btn_subject_4);

        layoutSubject1 = view.findViewById(R.id.layout_subject_1);
        layoutSubject4 = view.findViewById(R.id.layout_subject_4);
        loadingLayout = view.findViewById(R.id.loading_layout);

        // 科目一章节卡片
        cardChapter1 = view.findViewById(R.id.card_chapter_1);
        cardChapter2 = view.findViewById(R.id.card_chapter_2);
        cardChapter3 = view.findViewById(R.id.card_chapter_3);
        cardChapter4 = view.findViewById(R.id.card_chapter_4);
        cardChapter5 = view.findViewById(R.id.card_chapter_5);
        cardChapter6 = view.findViewById(R.id.card_chapter_6);

        // 科目四章节卡片
        cardSubject4Chapter1 = view.findViewById(R.id.card_subject4_chapter_1);
        cardSubject4Chapter2 = view.findViewById(R.id.card_subject4_chapter_2);
        cardSubject4Chapter3 = view.findViewById(R.id.card_subject4_chapter_3);
        cardSubject4Chapter4 = view.findViewById(R.id.card_subject4_chapter_4);
        cardSubject4Chapter5 = view.findViewById(R.id.card_subject4_chapter_5);
        cardSubject4Chapter6 = view.findViewById(R.id.card_subject4_chapter_6);
        cardSubject4Chapter7 = view.findViewById(R.id.card_subject4_chapter_7);
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
    }

    private void switchToSubject4() {
        layoutSubject1.setVisibility(View.GONE);
        layoutSubject4.setVisibility(View.VISIBLE);
    }

    private void setupChapterListeners() {
        // 科目一章节点击监听
        if (cardChapter1 != null) {
            cardChapter1.setOnClickListener(v -> startChapterQuiz(subject1Chapters[0]));
        }
        if (cardChapter2 != null) {
            cardChapter2.setOnClickListener(v -> startChapterQuiz(subject1Chapters[1]));
        }
        if (cardChapter3 != null) {
            cardChapter3.setOnClickListener(v -> startChapterQuiz(subject1Chapters[2]));
        }
        if (cardChapter4 != null) {
            cardChapter4.setOnClickListener(v -> startChapterQuiz(subject1Chapters[3]));
        }
        if (cardChapter5 != null) {
            cardChapter5.setOnClickListener(v -> startChapterQuiz(subject1Chapters[4]));
        }
        if (cardChapter6 != null) {
            cardChapter6.setOnClickListener(v -> startChapterQuiz(subject1Chapters[5]));
        }

        // 科目四章节点击监听
        if (cardSubject4Chapter1 != null) {
            cardSubject4Chapter1.setOnClickListener(v -> startChapterQuiz(subject4Chapters[0]));
        }
        if (cardSubject4Chapter2 != null) {
            cardSubject4Chapter2.setOnClickListener(v -> startChapterQuiz(subject4Chapters[1]));
        }
        if (cardSubject4Chapter3 != null) {
            cardSubject4Chapter3.setOnClickListener(v -> startChapterQuiz(subject4Chapters[2]));
        }
        if (cardSubject4Chapter4 != null) {
            cardSubject4Chapter4.setOnClickListener(v -> startChapterQuiz(subject4Chapters[3]));
        }
        if (cardSubject4Chapter5 != null) {
            cardSubject4Chapter5.setOnClickListener(v -> startChapterQuiz(subject4Chapters[4]));
        }
        if (cardSubject4Chapter6 != null) {
            cardSubject4Chapter6.setOnClickListener(v -> startChapterQuiz(subject4Chapters[5]));
        }
        if (cardSubject4Chapter7 != null) {
            cardSubject4Chapter7.setOnClickListener(v -> startChapterQuiz(subject4Chapters[6]));
        }
    }

    private void startChapterQuiz(String chapterName) {
        // 显示加载状态
        showLoading(true);

        // 测试接口是否可用
        testChapterApi(chapterName);
    }

    private void testChapterApi(String chapterName) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 注意：需要将章节名中的空格和括号编码处理
        String encodedChapterName = encodeChapterName(chapterName);

        Call<List<Question>> call = apiService.getChapterQuestions(encodedChapterName, 1);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Question> questions = response.body();
                    if (!questions.isEmpty()) {
                        // 跳转到刷题页面
                        navigateToQuizActivity(chapterName);
                    } else {
                        Toast.makeText(getContext(), "该章节暂无题目", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "加载章节题目失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encodeChapterName(String chapterName) {
        // 处理章节名称中的特殊字符
        String encoded = chapterName;
        // 移除括号内容，只保留章节名称
        if (encoded.contains("(")) {
            encoded = encoded.substring(0, encoded.indexOf("(")).trim();
        }
        // 可以添加更多的编码处理
        return encoded;
    }

    private void navigateToQuizActivity(String chapterName) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("mode", "chapter");
            intent.putExtra("subject", currentSubject);
            intent.putExtra("title", "章节练习");
            intent.putExtra("chapter", chapterName);
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
        // 如果是从外部切换科目，更新界面
        if (currentSubject != newSubject) {
            currentSubject = newSubject;

            if (toggleSubject != null) {
                if (newSubject == 1 && btnSubject1 != null) {
                    toggleSubject.check(R.id.btn_subject_1);
                } else if (newSubject == 4 && btnSubject4 != null) {
                    toggleSubject.check(R.id.btn_subject_4);
                }
            }
        }
    }
}