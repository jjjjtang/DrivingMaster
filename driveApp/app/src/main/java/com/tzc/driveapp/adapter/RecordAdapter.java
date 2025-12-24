package com.tzc.driveapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.R;
import com.tzc.driveapp.model.Record;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private final List<Record> recordList;

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = recordList.get(position);

        holder.tvMethod.setText(record.getMethod());
        holder.tvSubject.setText(record.getSubject());

        holder.tvResult.setText(
                "正确率 " + record.getCorrectRate()
                        + "（" + record.getCorrectNum()
                        + " / " + record.getTotalNum() + "）"
        );

        holder.tvTime.setText("用时 " + record.getTime());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMethod, tvSubject, tvResult, tvTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMethod = itemView.findViewById(R.id.tv_method);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvResult = itemView.findViewById(R.id.tv_result);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}