package com.tzc.driveapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.R;

import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {

    private List<String> options;
    private OnOptionClickListener listener;
    private boolean enabled = true;

    public OptionAdapter(List<String> options) {
        this.options = options;
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        this.listener = listener;
    }

    public void updateOptions(List<String> newOptions) {
        this.options = newOptions;
        notifyDataSetChanged();
    }

    public void setOptionsEnabled(boolean enabled) {
        this.enabled = enabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String option = options.get(position);
        char optionChar = (char) ('A' + position);

        holder.tvOptionCircle.setText(String.valueOf(optionChar));
        holder.tvOptionText.setText(option);

        // 设置点击状态
        holder.itemView.setEnabled(enabled);
        holder.itemView.setClickable(enabled);

        // 点击事件
        if (enabled) {
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onOptionClick(position);
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOptionCircle;
        TextView tvOptionText;

        ViewHolder(View itemView) {
            super(itemView);
            tvOptionCircle = itemView.findViewById(R.id.tv_option_circle);
            tvOptionText = itemView.findViewById(R.id.tv_option_text);
        }
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }
}