package com.tzc.driveapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tzc.driveapp.MainActivity;
import com.tzc.driveapp.QuizActivity;
import com.tzc.driveapp.R;

public class ExamFragment extends MainActivity.BaseFragment {

    private EditText etQuestionCount;
    private EditText etExamTime;
    private Button btnStartExam;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        initView(view);
        setupListeners();
        return view;
    }

    private void initView(View view) {
        etQuestionCount = view.findViewById(R.id.et_question_count);
        etExamTime = view.findViewById(R.id.et_exam_time);
        btnStartExam = view.findViewById(R.id.btn_start_exam);
    }

    private void setupListeners() {
        btnStartExam.setOnClickListener(v -> startExam());
    }

    private void startExam() {
        String examTimeStr = etExamTime.getText().toString().trim();

        if (examTimeStr.isEmpty()) {
            Toast.makeText(getContext(), "请设置考试时间", Toast.LENGTH_SHORT).show();
            return;
        }

        int examTime = Integer.parseInt(examTimeStr);
        if (examTime <= 0) {
            Toast.makeText(getContext(), "请输入有效时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getActivity() != null) {
            MainActivity activity = (MainActivity) getActivity();
            int subject = activity.getCurrentSubject();

            Intent intent = new Intent(getActivity(), QuizActivity.class);
            intent.putExtra("mode", "exam");              // 考试模式
            intent.putExtra("subject", subject);
            intent.putExtra("title", "模拟考试");
            intent.putExtra("examTime", examTime * 60);   // 秒
            startActivity(intent);
        }
    }

    @Override
    public void onSubjectChanged(int newSubject) {
        // 更新科目相关设置
        Toast.makeText(getContext(), "考试科目已切换: " + newSubject, Toast.LENGTH_SHORT).show();
    }
}