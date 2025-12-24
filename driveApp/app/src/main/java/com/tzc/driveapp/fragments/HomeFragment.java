package com.tzc.driveapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.tzc.driveapp.MainActivity;
import com.tzc.driveapp.QuizActivity;
import com.tzc.driveapp.R;
import com.tzc.driveapp.SearchResultsActivity;

public class HomeFragment extends MainActivity.BaseFragment {

    // 搜索相关
    private EditText etSearch;
    private ImageView ivSearchIcon;
    private CardView cardSearch;

    // 主功能卡片
    private CardView cardSequence;
    private CardView cardExam;
    private CardView cardChapter;
    private CardView cardType;
    private CardView cardWrong;
    private CardView cardHistory;
    private CardView cardCollection;
    private CardView cardRanking;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setupSearchListener();
        setupListeners();
        return view;
    }

    private void initView(View view) {
        // 搜索相关视图
        etSearch = view.findViewById(R.id.et_search);
        ivSearchIcon = view.findViewById(R.id.iv_search_icon);
        cardSearch = view.findViewById(R.id.card_search);

        // 主功能卡片
        cardSequence = view.findViewById(R.id.card_sequence);
        cardExam = view.findViewById(R.id.card_exam);
        cardChapter = view.findViewById(R.id.card_chapter);
        cardType = view.findViewById(R.id.card_type);
        cardWrong = view.findViewById(R.id.card_wrong);
        cardHistory = view.findViewById(R.id.card_history);

    }

    private void setupSearchListener() {
        if (etSearch != null) {
            // 搜索框回车键监听
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch();
                    return true;
                }
                return false;
            });
        }

        // 搜索图标点击监听
        if (ivSearchIcon != null) {
            ivSearchIcon.setOnClickListener(v -> performSearch());
        }

        // 搜索卡片点击监听（整个卡片都可点击）
        if (cardSearch != null) {
            cardSearch.setOnClickListener(v -> {
                etSearch.requestFocus();
                // 显示软键盘
                showKeyboard();
            });
        }
    }

    private void showKeyboard() {
        if (getContext() != null && etSearch != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearch, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void hideKeyboard() {
        if (getContext() != null && etSearch != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
            }
        }
    }

    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();

        if (keyword.isEmpty()) {
            Toast.makeText(getContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // 隐藏软键盘
        hideKeyboard();

        // 跳转到搜索结果页面
        Intent intent = new Intent(getActivity(), SearchResultsActivity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    private void setupListeners() {
        // 顺序刷题
        if (cardSequence != null) {
            cardSequence.setOnClickListener(v -> startSequenceQuiz());
        }

        // 模拟考试
        if (cardExam != null) {
            cardExam.setOnClickListener(v -> navigateToExam());
        }

        // 章节刷题
        if (cardChapter != null) {
            cardChapter.setOnClickListener(v -> navigateToChapterSelect());
        }

        // 题型刷题
        if (cardType != null) {
            cardType.setOnClickListener(v -> navigateToTypeSelect());
        }

        // 错题记录
        if (cardWrong != null) {
            cardWrong.setOnClickListener(v -> navigateToWrongRecord());
        }

        // 历史记录
        if (cardHistory != null) {
            cardHistory.setOnClickListener(v -> navigateToHistoryRecord());
        }

        // 我的收藏
        if (cardCollection != null) {
            cardCollection.setOnClickListener(v -> navigateToCollection());
        }

        // 排行榜
        if (cardRanking != null) {
            cardRanking.setOnClickListener(v -> navigateToRanking());
        }
    }

    private void startSequenceQuiz() {
        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            int subject = activity.getCurrentSubject();

            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("mode", "sequence");
            intent.putExtra("subject", subject);
            intent.putExtra("title", "顺序刷题");
            startActivity(intent);
        }
    }

    private void navigateToExam() {
        if (getActivity() != null) {
            // 跳转到考试设置页面
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ExamFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // 在 HomeFragment.java 中修改 navigateToChapterSelect 方法
    private void navigateToChapterSelect() {
        if (getActivity() != null) {
            // 跳转到章节选择页面
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChapterSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToTypeSelect() {
        if (getActivity() != null) {
            // 跳转到题型选择页面
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TypeSelectFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    // 修改HomeFragment.java中的navigateToWrongRecord方法
    private void navigateToWrongRecord() {
        if (getActivity() != null) {
            // 跳转到错题记录页面
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new IncorrectQuestionsFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }
    private void navigateToHistoryRecord() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HistoryRecordFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToCollection() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CollectionFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToRanking() {
        // TODO: 跳转到排行榜页面
        Toast.makeText(getContext(), "排行榜功能开发中", Toast.LENGTH_SHORT).show();
    }

    private void showChapterSelectionDialog() {
        // TODO: 实现章节选择对话框
        Toast.makeText(getContext(), "章节选择功能开发中", Toast.LENGTH_SHORT).show();

        // 临时示例：显示简单的章节列表
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择章节")
               .setItems(new String[]{"章节1", "章节2", "章节3"}, (dialog, which) -> {
                   // 用户选择了章节
                   String selectedChapter = "章节" + (which + 1);
                   startChapterQuiz(selectedChapter);
               })
               .setNegativeButton("取消", null)
               .show();
        */
    }

    private void startChapterQuiz(String chapterName) {
        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            int subject = activity.getCurrentSubject();

            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("mode", "chapter");
            intent.putExtra("subject", subject);
            intent.putExtra("title", chapterName);
            intent.putExtra("chapter", chapterName);
            startActivity(intent);
        }
    }

    @Override
    public void onSubjectChanged(int newSubject) {
        // 更新科目相关数据
        // 可以在这里更新界面上的科目提示信息
        if (getContext() != null) {
            Toast.makeText(getContext(), "科目已切换为: " + newSubject, Toast.LENGTH_SHORT).show();
        }
    }
}