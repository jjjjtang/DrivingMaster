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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private List<Question> dataList;
    private OnItemClickListener listener;

    public QuestionAdapter(List<Question> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question question = dataList.get(position);

        holder.tvQuestionType.setText(question.getType());
        holder.tvQuestionContent.setText(question.getContent());
        holder.tvTime.setText("最近练习");

        // 如果是收藏状态，显示收藏图标
        holder.ivStatus.setVisibility(View.VISIBLE);
        holder.ivStatus.setImageResource(R.drawable.ic_star_filled);

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
        TextView tvTime;
        ImageView ivStatus;

        ViewHolder(View itemView) {
            super(itemView);
            tvQuestionType = itemView.findViewById(R.id.tv_question_type);
            tvQuestionContent = itemView.findViewById(R.id.tv_question_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivStatus = itemView.findViewById(R.id.iv_status);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}