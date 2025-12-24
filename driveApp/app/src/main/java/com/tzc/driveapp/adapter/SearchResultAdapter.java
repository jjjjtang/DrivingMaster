package com.tzc.driveapp.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.R;
import com.tzc.driveapp.model.Question;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<Question> dataList;
    private String keyword;
    private OnItemClickListener listener;

    public SearchResultAdapter(List<Question> dataList, String keyword) {
        this.dataList = dataList;
        this.keyword = keyword;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Question> newData, String newKeyword) {
        this.dataList = newData;
        this.keyword = newKeyword;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = dataList.get(position);

        // 设置题型
        if (question.getType() != null) {
            holder.tvQuestionType.setText(question.getType());
        }

        // 设置题目内容（高亮关键词）
        if (question.getContent() != null) {
            String content = question.getContent();
            SpannableString spannableContent = new SpannableString(content);

            // 高亮关键词
            if (keyword != null && !keyword.isEmpty() && content.toLowerCase().contains(keyword.toLowerCase())) {
                int startIndex = content.toLowerCase().indexOf(keyword.toLowerCase());
                int endIndex = startIndex + keyword.length();

                if (startIndex >= 0 && endIndex <= content.length()) {
                    spannableContent.setSpan(
                            new ForegroundColorSpan(Color.parseColor("#FFD700")), // 金黄色高亮
                            startIndex,
                            endIndex,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }

            holder.tvQuestionContent.setText(spannableContent);
        }

        // 设置科目
        if (question.getSubject() != null) {
            holder.tvSubject.setText(question.getSubject());
        }

        // 设置难度
        if (question.getDifficuity() != null) {
            holder.tvDifficulty.setText(question.getDifficuity());
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionType;
        TextView tvQuestionContent;
        TextView tvSubject;
        TextView tvDifficulty;

        ViewHolder(View itemView) {
            super(itemView);
            tvQuestionType = itemView.findViewById(R.id.tv_question_type);
            tvQuestionContent = itemView.findViewById(R.id.tv_question_content);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvDifficulty = itemView.findViewById(R.id.tv_difficulty);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}