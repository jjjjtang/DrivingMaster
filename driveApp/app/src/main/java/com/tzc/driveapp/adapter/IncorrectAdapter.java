package com.tzc.driveapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.R;
import com.tzc.driveapp.model.Question;

import java.util.List;
public class IncorrectAdapter extends RecyclerView.Adapter<IncorrectAdapter.ViewHolder> {

    private List<Question> questionList;
    private OnItemClickListener listener;

    public IncorrectAdapter(List<Question> questionList) {
        this.questionList = questionList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_incorrect_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(questionList.get(position));
    }

    @Override
    public int getItemCount() {
        return questionList == null ? 0 : questionList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvQuestionType;
        TextView tvQuestionContent;
        TextView tvSubject;
        TextView tvChapter;
        ImageView ivDelete;

        ViewHolder(View itemView) {
            super(itemView);

            tvQuestionType = itemView.findViewById(R.id.tv_question_type);
            tvQuestionContent = itemView.findViewById(R.id.tv_question_content);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvChapter = itemView.findViewById(R.id.tv_chapter);   // ✅ 必须
        }

        void bind(Question question) {

            // 题型
            tvQuestionType.setText(
                    question.getType() != null ? question.getType() : "未知题型"
            );

            // 题目内容
            if (question.getContent() != null) {
                String content = question.getContent();
                if (content.length() > 50) {
                    content = content.substring(0, 50) + "...";
                }
                tvQuestionContent.setText(content);
            } else {
                tvQuestionContent.setText("");
            }

            // 科目
            tvSubject.setText(
                    question.getSubject() != null ? question.getSubject() : "未知科目"
            );

            // 章节
            tvChapter.setText(
                    question.getChapter() != null ? question.getChapter() : "未知章节"
            );

            // item 点击
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(question);
                }
            });

            // 删除按钮（防止布局里没有）
            if (ivDelete != null) {
                ivDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(question);
                    }
                });
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Question question);
        void onDeleteClick(Question question);
    }
}