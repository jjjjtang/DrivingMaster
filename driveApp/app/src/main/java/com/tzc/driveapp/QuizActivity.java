package com.tzc.driveapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.card.MaterialCardView;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";

    private ProgressBar progressBar;
    private TextView tvQuestionType;
    private TextView tvQuestionContent;
    private LinearLayout layoutOptions;
    private CardView cardAnalysis;
    private TextView tvAnalysisContent;

    // 底部工具栏按钮
    private Button btnSubmit;
    private Button btnNext;
    private TextView tvQuestionCount;

    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private String mode;
    private int subject;
    private String title;
    private String questionType;

    // 当前是否显示解析
    private boolean isShowingAnalysis = false;
    // 当前页码
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreQuestions = true;

    // 多选题相关
    private boolean isMultipleChoice = false;
    private Set<Integer> selectedOptions = new HashSet<>(); // 存储选中的选项索引
    private List<View> optionViews = new ArrayList<>(); // 存储选项视图

    // === 考试模式相关 ===
    private CountDownTimer examTimer;
    private int examTimeSeconds;
    private TextView tvExamTimer;
    private boolean isExamMode = false;

    // 统计
    private int correctCount = 0;
    private int wrongCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // ✅ 先初始化所有 View
        initView();
        setupListeners();

        // 再获取参数
        mode = getIntent().getStringExtra("mode");
        subject = getIntent().getIntExtra("subject", 1);
        title = getIntent().getStringExtra("title");
        questionType = getIntent().getStringExtra("questionType");

        Log.d(TAG, "mode: " + mode + ", subject: " + subject + ", type: " + questionType);

        // ✅ 再根据模式启动考试
        if ("exam".equals(mode)) {
            isExamMode = true;
            examTimeSeconds = getIntent().getIntExtra("examTime", 3600);
            startExamTimer();      // ✅ 此时 tvExamTimer 已初始化
            loadExamQuestions();
            return;               // ⚠️ 考试模式直接 return
        }

        // 非考试模式
        if ("type".equals(mode) && questionType != null) {
            loadTypeQuestions(questionType, currentPage);
        } else {
            loadSequenceQuestions();
        }
    }

    private void initView() {
        progressBar = findViewById(R.id.progress_bar);
        tvQuestionType = findViewById(R.id.tv_question_type);
        tvQuestionContent = findViewById(R.id.tv_question_content);
        layoutOptions = findViewById(R.id.layout_options);
        cardAnalysis = findViewById(R.id.card_analysis);
        tvAnalysisContent = findViewById(R.id.tv_analysis_content);
        tvQuestionCount = findViewById(R.id.tv_question_count);
        tvExamTimer = findViewById(R.id.tv_exam_timer);

        // 底部工具栏
        btnSubmit = findViewById(R.id.btn_submit);
        btnNext = findViewById(R.id.btn_next);

        // 设置标题
        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

        // 初始化时隐藏提交按钮
        btnSubmit.setVisibility(View.GONE);
    }

    private void startExamTimer() {
        tvExamTimer.setVisibility(View.VISIBLE);

        examTimer = new CountDownTimer(examTimeSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long min = millisUntilFinished / 1000 / 60;
                long sec = (millisUntilFinished / 1000) % 60;
                tvExamTimer.setText(String.format("%02d:%02d", min, sec));
            }

            @Override
            public void onFinish() {
                tvExamTimer.setText("00:00");
                submitExam();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (isExamMode) {
            Toast.makeText(this, "考试中不能退出", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    private void loadExamQuestions() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllQuestions().enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Question> all = response.body();
                    List<Question> filtered = new ArrayList<>();

                    // 按科目过滤
                    String subjectStr = subject == 1 ? "C1C2科目一" : "C1C2科目四";
                    for (Question q : all) {
                        if (subjectStr.equals(q.getSubject())) {
                            filtered.add(q);
                        }
                    }

                    if (filtered.size() < 100) {
                        Toast.makeText(QuizActivity.this, "题库不足 100 题", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    Collections.shuffle(filtered);
                    questionList = filtered.subList(0, 100);

                    currentQuestionIndex = 0;
                    showQuestion(0);
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(QuizActivity.this, "加载考试题目失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupListeners() {
        // 提交按钮（多选题用）
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> submitMultipleChoice());
        }

        // 下一题按钮
        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                if (currentQuestionIndex < questionList.size() - 1) {
                    // 还有下一题，直接显示
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                } else if (hasMoreQuestions) {
                    // 需要加载更多题目
                    loadMoreQuestions();
                } else {
                    // 所有题目完成
                    Toast.makeText(this, "恭喜！您已完成所有题目", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // 返回按钮
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> onBackPressed());
        }
    }

    private void loadTypeQuestions(String type, int page) {
        showLoading(true);
        isLoading = true;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Log.d(TAG, "正在请求接口: api/question/type/" + type + "/page/" + page);

        Call<List<Question>> call = apiService.getQuestionsByType(type, page);

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                isLoading = false;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Question> newQuestions = response.body();

                    if (!newQuestions.isEmpty()) {
                        // 如果是第一页，替换整个列表
                        if (page == 1) {
                            questionList.clear();
                            questionList.addAll(newQuestions);
                            currentQuestionIndex = 0;
                        } else {
                            // 如果是加载更多，添加到列表末尾
                            questionList.addAll(newQuestions);
                        }

                        showQuestion(currentQuestionIndex);

                        // 判断是否还有更多题目
                        if (newQuestions.size() < 20) { // 假设每页20题，如果少于20题说明可能是最后一页
                            hasMoreQuestions = false;
                        } else {
                            hasMoreQuestions = true;
                            currentPage = page; // 更新当前页码
                        }

                        Log.d(TAG, "成功加载题目: " + newQuestions.size() + "题");
                    } else {
                        if (page == 1) {
                            // 第一页就没数据
                            showEmptyState();
                        } else {
                            // 加载更多时没数据了
                            hasMoreQuestions = false;
                            Toast.makeText(QuizActivity.this, "没有更多题目了", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    showErrorState("加载题目失败: " + response.code());
                    Log.e(TAG, "请求失败: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                showErrorState("网络错误: " + t.getMessage());
                Log.e(TAG, "网络错误: " + t.getMessage());
            }
        });
    }

    private void submitExam() {
        if (examTimer != null) {
            examTimer.cancel();
        }

        int score = correctCount; // 一题一分

        Toast.makeText(
                this,
                "考试结束\n得分：" + score + "\n正确：" + correctCount + "\n错误：" + wrongCount,
                Toast.LENGTH_LONG
        ).show();

        finish();
    }

    private void loadSequenceQuestions() {
        showLoading(true);
        isLoading = true;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // 根据科目筛选
        String subjectFilter;
        if (subject == 1) {
            subjectFilter = "C1C2科目一";
        } else if (subject == 4) {
            subjectFilter = "C1C2科目四";
        } else {
            subjectFilter = "C1C2科目一";
        }

        Call<List<Question>> call = apiService.getQuestionList(
                subjectFilter,
                1,    // 第一页
                100   // 每次加载100题
        );

        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(Call<List<Question>> call, Response<List<Question>> response) {
                isLoading = false;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    if (!questionList.isEmpty()) {
                        currentQuestionIndex = 0;
                        showQuestion(currentQuestionIndex);
                    } else {
                        Toast.makeText(QuizActivity.this, "没有找到题目", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Log.e(TAG, "加载题目失败: " + response.code() + " - " + response.message());
                    Toast.makeText(QuizActivity.this, "加载题目失败，请检查网络", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<List<Question>> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                Log.e(TAG, "网络错误: " + t.getMessage());
                Toast.makeText(QuizActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadMoreQuestions() {
        if (isLoading || !hasMoreQuestions) {
            return;
        }

        int nextPage = currentPage + 1;
        loadTypeQuestions(questionType, nextPage);
    }

    private void showQuestion(int index) {
        if (index >= questionList.size()) {
            // 已经完成所有题目
            Toast.makeText(this, "恭喜！您已完成所有题目", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Question question = questionList.get(index);

        // 判断是否为多选题
        isMultipleChoice = "多选题".equals(question.getType());

        // 清空之前的选中状态
        selectedOptions.clear();
        optionViews.clear();

        // 更新UI
        if (tvQuestionType != null) {
            String type = question.getType();
            if (type == null || type.isEmpty()) {
                type = questionType != null ? questionType : "未知题型";
            }
            tvQuestionType.setText(type);

            // 更新题型标签背景色
            if ("多选题".equals(type)) {
                tvQuestionType.setBackgroundResource(R.drawable.bg_tag_multiple);
            } else if ("单选题".equals(type)) {
                tvQuestionType.setBackgroundResource(R.drawable.bg_tag_single);
            } else if ("判断题".equals(type)) {
                tvQuestionType.setBackgroundResource(R.drawable.bg_tag_truefalse);
            }
        }

        if (tvQuestionContent != null) {
            String content = question.getContent();
            if (content == null || content.isEmpty()) {
                content = "题目内容为空";
            }
            tvQuestionContent.setText(content);
        }

        // 更新题目计数
        if (tvQuestionCount != null) {
            tvQuestionCount.setText((index + 1) + "/" + questionList.size());
        }

        // 清空并添加选项
        if (layoutOptions != null) {
            layoutOptions.removeAllViews();

            Question.Options options = question.getOptionsObj();
            if (options != null) {
                String[] optionList = options.getOptionList();
                for (int i = 0; i < optionList.length; i++) {
                    char optionChar = (char) ('A' + i);
                    String optionText = optionList[i];
                    if (optionText != null) {
                        View optionView = createOptionView(i, optionChar, optionText, options, question);
                        layoutOptions.addView(optionView);
                        optionViews.add(optionView);
                    }
                }
            }
        }

        // 更新解析
        if (tvAnalysisContent != null) {
            String explain = question.getExplain();
            if (explain == null || explain.isEmpty()) {
                explain = "暂无解析";
            }
            tvAnalysisContent.setText(explain);
        }

        // 隐藏解析
        if (cardAnalysis != null) {
            cardAnalysis.setVisibility(View.GONE);
            isShowingAnalysis = false;
        }

        // 更新底部按钮
        updateBottomButtons();

        // 更新进度
        if (progressBar != null) {
            int progress = (int) ((index / (float) questionList.size()) * 100);
            progressBar.setProgress(progress);
        }
    }

    private View createOptionView(int position, char optionChar, String optionText,
                                  Question.Options options, Question question) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View optionView = inflater.inflate(R.layout.item_option, layoutOptions, false);

        TextView tvOptionCircle = optionView.findViewById(R.id.tv_option_circle);
        TextView tvOptionText = optionView.findViewById(R.id.tv_option_text);
        MaterialCardView cardOption = (MaterialCardView) optionView.findViewById(R.id.card_option);

        // 添加多选框（多选题用）
        View checkBox = optionView.findViewById(R.id.checkbox);
        if (checkBox != null) {
            checkBox.setVisibility(isMultipleChoice ? View.VISIBLE : View.GONE);
        }

        tvOptionCircle.setText(String.valueOf(optionChar));
        tvOptionText.setText(optionText);

        // 重置卡片状态
        cardOption.setStrokeWidth(1);
        cardOption.setStrokeColor(getResources().getColor(R.color.border_gray));
        cardOption.setCardBackgroundColor(getResources().getColor(R.color.white));
        tvOptionCircle.setBackgroundResource(R.drawable.bg_option_circle);
        tvOptionCircle.setTextColor(getResources().getColor(R.color.text_secondary));

        // 添加点击事件
        cardOption.setOnClickListener(v -> {
            if (isShowingAnalysis) {
                return; // 如果已经显示解析，不再处理新的点击
            }

            if (isMultipleChoice) {
                // 多选题处理
                onMultipleOptionSelected(position, cardOption, tvOptionCircle, checkBox);
            } else {
                // 单选题/判断题处理
                onSingleOptionSelected(optionChar, options, cardOption, tvOptionCircle);
            }
        });

        // 启用点击
        cardOption.setClickable(true);
        cardOption.setEnabled(true);

        return optionView;
    }

    private void onMultipleOptionSelected(int position, MaterialCardView card,
                                          TextView circleText, View checkBox) {

        if (selectedOptions.contains(position)) {
            // 取消选中
            selectedOptions.remove(position);
            card.setStrokeColor(getResources().getColor(R.color.border_gray));
            card.setCardBackgroundColor(getResources().getColor(R.color.white));
            circleText.setBackgroundResource(R.drawable.bg_option_circle);
            circleText.setTextColor(getResources().getColor(R.color.text_secondary));

            if (checkBox != null) {
                checkBox.setBackgroundResource(R.drawable.ic_checkbox_unchecked);
            }
        } else {
            // 选中
            selectedOptions.add(position);
            card.setStrokeColor(getResources().getColor(R.color.primary_color));
            card.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            circleText.setBackgroundResource(R.drawable.bg_option_circle_selected);
            circleText.setTextColor(getResources().getColor(R.color.white));

            if (checkBox != null) {
                checkBox.setBackgroundResource(R.drawable.ic_checkbox_checked);
            }
        }

        // 更新提交按钮状态
        updateSubmitButton();
    }

    private void onSingleOptionSelected(char selectedOption, Question.Options options,
                                        MaterialCardView selectedCard, TextView selectedCircle) {

        // ========= 考试模式 =========
        if (isExamMode) {

            // 1️⃣ 重置所有选项为普通状态
            for (View optionView : optionViews) {
                MaterialCardView card = optionView.findViewById(R.id.card_option);
                TextView circle = optionView.findViewById(R.id.tv_option_circle);

                card.setStrokeColor(getResources().getColor(R.color.border_gray));
                card.setCardBackgroundColor(getResources().getColor(R.color.white));
                circle.setBackgroundResource(R.drawable.bg_option_circle);
                circle.setTextColor(getResources().getColor(R.color.text_secondary));
            }

            // 2️⃣ 设置“被选中”样式（不代表对错）
            selectedCard.setStrokeColor(getResources().getColor(R.color.primary_color));
            selectedCard.setBackgroundResource(R.drawable.bg_option_exam_selected);
            selectedCircle.setBackgroundResource(R.drawable.bg_option_circle_exam_selected);
            selectedCircle.setTextColor(getResources().getColor(R.color.white));

            // 3️⃣ 统计答案（不展示）
            boolean isCorrect = options.isCorrectAnswer(selectedOption);
            if (isCorrect) {
                correctCount++;
            } else {
                wrongCount++;
            }

            // 4️⃣ 不显示解析、不禁用选项
            btnNext.setVisibility(View.VISIBLE);
            return;
        }

        // ========= 非考试模式（你原来的逻辑） =========
        isShowingAnalysis = true;

        if (cardAnalysis != null) {
            cardAnalysis.setVisibility(View.VISIBLE);
        }

        boolean isCorrect = options.isCorrectAnswer(selectedOption);

        if (isCorrect) {
            selectedCard.setStrokeColor(getResources().getColor(R.color.green));
            selectedCard.setCardBackgroundColor(getResources().getColor(R.color.green_light));
            selectedCircle.setBackgroundResource(R.drawable.bg_option_circle_correct);
            selectedCircle.setTextColor(getResources().getColor(R.color.white));
        } else {
            selectedCard.setStrokeColor(getResources().getColor(R.color.red));
            selectedCard.setCardBackgroundColor(getResources().getColor(R.color.red_light));
            selectedCircle.setBackgroundResource(R.drawable.bg_option_circle_wrong);
            selectedCircle.setTextColor(getResources().getColor(R.color.white));

            String correctAnswer = options.getAnswer();
            if (correctAnswer != null && correctAnswer.length() > 0) {
                showCorrectAnswer(correctAnswer.charAt(0), options);
            }
        }

        disableAllOptions();
        btnSubmit.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void submitMultipleChoice() {
        if (selectedOptions.isEmpty()) {
            Toast.makeText(this, "请至少选择一个选项", Toast.LENGTH_SHORT).show();
            return;
        }

        // 标记为显示解析
        isShowingAnalysis = true;

        // 显示解析
        if (cardAnalysis != null) {
            cardAnalysis.setVisibility(View.VISIBLE);
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        Question.Options options = currentQuestion.getOptionsObj();

        if (options == null) {
            Toast.makeText(this, "题目数据异常", Toast.LENGTH_SHORT).show();
            return;
        }

        // 将选中的选项转换为字母字符串
        StringBuilder selectedAnswer = new StringBuilder();
        for (int index : selectedOptions) {
            selectedAnswer.append((char) ('A' + index));
        }

        // 获取正确答案
        String correctAnswer = options.getAnswer();
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            Toast.makeText(this, "无法获取正确答案", Toast.LENGTH_SHORT).show();
            return;
        }

        // 判断是否正确（需要将答案排序后比较，因为多选题顺序不影响）
        String sortedSelected = sortString(selectedAnswer.toString());
        String sortedCorrect = sortString(correctAnswer);

        boolean isCorrect = sortedSelected.equals(sortedCorrect);

        // 更新所有选中选项的状态
        for (int index : selectedOptions) {
            if (index < optionViews.size()) {
                View optionView = optionViews.get(index);
                MaterialCardView card = optionView.findViewById(R.id.card_option);
                TextView circleText = optionView.findViewById(R.id.tv_option_circle);

                if (isCorrect) {
                    // 选对了
                    card.setStrokeColor(getResources().getColor(R.color.green));
                    card.setCardBackgroundColor(getResources().getColor(R.color.green_light));
                    circleText.setBackgroundResource(R.drawable.bg_option_circle_correct);
                    circleText.setTextColor(getResources().getColor(R.color.white));
                } else {
                    // 选错了
                    card.setStrokeColor(getResources().getColor(R.color.red));
                    card.setCardBackgroundColor(getResources().getColor(R.color.red_light));
                    circleText.setBackgroundResource(R.drawable.bg_option_circle_wrong);
                    circleText.setTextColor(getResources().getColor(R.color.white));
                }
            }
        }

        // 显示所有正确答案（即使答对了也要显示）
        showAllCorrectAnswers(correctAnswer);

        // 禁用所有选项点击
        disableAllOptions();

        // 多选题提交后显示下一题按钮
        updateButtonsAfterSubmit();
    }

    private String sortString(String str) {
        char[] chars = str.toCharArray();
        java.util.Arrays.sort(chars);
        return new String(chars);
    }

    private void showAllCorrectAnswers(String correctAnswer) {
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return;
        }

        for (int i = 0; i < optionViews.size(); i++) {
            View optionView = optionViews.get(i);
            MaterialCardView card = optionView.findViewById(R.id.card_option);
            TextView circleText = optionView.findViewById(R.id.tv_option_circle);
            TextView optionText = optionView.findViewById(R.id.tv_option_text);

            char currentOptionChar = (char) ('A' + i);

            if (correctAnswer.indexOf(currentOptionChar) >= 0) {
                // 这是正确答案
                card.setStrokeColor(getResources().getColor(R.color.green));
                card.setCardBackgroundColor(getResources().getColor(R.color.green_light));
                circleText.setBackgroundResource(R.drawable.bg_option_circle_correct);
                circleText.setTextColor(getResources().getColor(R.color.white));

                // 在选项文本后面添加"(正确答案)"
                String originalText = optionText.getText().toString();
                if (!originalText.contains("(正确答案)")) {
                    optionText.setText(originalText + " (正确答案)");
                }
            }
        }
    }

    private void showCorrectAnswer(char correctOption, Question.Options options) {
        if (layoutOptions != null) {
            for (int i = 0; i < optionViews.size(); i++) {
                View optionView = optionViews.get(i);
                MaterialCardView cardOption = optionView.findViewById(R.id.card_option);
                TextView tvOptionCircle = optionView.findViewById(R.id.tv_option_circle);
                TextView tvOptionText = optionView.findViewById(R.id.tv_option_text);

                char currentOptionChar = (char) ('A' + i);

                if (currentOptionChar == correctOption) {
                    // 这是正确答案
                    cardOption.setStrokeColor(getResources().getColor(R.color.green));
                    cardOption.setCardBackgroundColor(getResources().getColor(R.color.green_light));
                    tvOptionCircle.setBackgroundResource(R.drawable.bg_option_circle_correct);
                    tvOptionCircle.setTextColor(getResources().getColor(R.color.white));

                    // 在选项文本后面添加"(正确答案)"
                    String originalText = tvOptionText.getText().toString();
                    if (!originalText.contains("(正确答案)")) {
                        tvOptionText.setText(originalText + " (正确答案)");
                    }
                }
            }
        }
    }

    // 在QuizActivity.java中添加保存错题的方法
    private void saveIncorrectQuestion(Question question) {
        if (question.getQuestionId() == null) return;

        // 这里需要实现保存错题的接口
        // 可以在用户答错时调用此方法
    }

    // 在答错时调用
// 在showCorrectAnswer方法中，当用户答错时：
    private void onAnswerWrong(Question question) {
        // 保存错题到服务器
        saveIncorrectQuestion(question);
    }

    private void disableAllOptions() {
        for (View optionView : optionViews) {
            MaterialCardView cardOption = optionView.findViewById(R.id.card_option);
            cardOption.setClickable(false);
            cardOption.setEnabled(false);
        }
    }

    private void updateBottomButtons() {
        if (isMultipleChoice) {
            // 多选题：显示提交按钮，隐藏下一题按钮
            btnSubmit.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);

            // 初始时禁用提交按钮
            btnSubmit.setEnabled(false);
            btnSubmit.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
        } else {
            // 单选题/判断题：显示下一题按钮，隐藏提交按钮
            btnSubmit.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void updateSubmitButton() {
        if (isMultipleChoice) {
            btnSubmit.setEnabled(!selectedOptions.isEmpty());
            if (selectedOptions.isEmpty()) {
                btnSubmit.setBackgroundColor(getResources().getColor(R.color.btn_disabled));
            } else {
                btnSubmit.setBackgroundColor(getResources().getColor(R.color.primary_color));
            }
        }
    }

    private void updateButtonsAfterSubmit() {
        // 多选题提交后，隐藏提交按钮，显示下一题按钮
        btnSubmit.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        } else {
            // 所有题目完成
            Toast.makeText(this, "恭喜！您已完成所有题目", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showLoading(boolean show) {
        ProgressBar loadingProgress = findViewById(R.id.loading_progress);
        View contentLayout = findViewById(R.id.content_layout);

        if (loadingProgress != null) {
            loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState() {
        LinearLayout emptyLayout = findViewById(R.id.layout_empty);
        View contentLayout = findViewById(R.id.content_layout);

        if (emptyLayout != null) {
            emptyLayout.setVisibility(View.VISIBLE);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(View.GONE);
        }
    }

    private void showErrorState(String errorMsg) {
        LinearLayout errorLayout = findViewById(R.id.layout_error);
        TextView tvError = findViewById(R.id.tv_error);
        View contentLayout = findViewById(R.id.content_layout);

        if (errorLayout != null) {
            errorLayout.setVisibility(View.VISIBLE);
        }

        if (tvError != null) {
            tvError.setText(errorMsg);
        }

        if (contentLayout != null) {
            contentLayout.setVisibility(View.GONE);
        }

        // 重试按钮
        View btnRetry = findViewById(R.id.btn_retry);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                if ("type".equals(mode) && questionType != null) {
                    loadTypeQuestions(questionType, 1);
                } else {
                    loadSequenceQuestions();
                }
            });
        }
    }
}