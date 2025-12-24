package com.tzc.driveapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tzc.driveapp.MainActivity;
import com.tzc.driveapp.R;
import com.tzc.driveapp.adapter.QuestionAdapter;
import com.tzc.driveapp.api.ApiClient;
import com.tzc.driveapp.api.ApiService;
import com.tzc.driveapp.model.Question;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionFragment extends MainActivity.BaseFragment {

    private RecyclerView rvCollection;
    private TextView tvEmpty;
    private QuestionAdapter adapter;
    private List<Question> collectionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        initView(view);
        setupRecyclerView();
        loadCollectionData();
        return view;
    }

    private void initView(View view) {
        rvCollection = view.findViewById(R.id.rv_collection);
        tvEmpty = view.findViewById(R.id.tv_empty);
    }

    private void setupRecyclerView() {
        adapter = new QuestionAdapter(collectionList);
        rvCollection.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCollection.setAdapter(adapter);

        // 设置点击监听
        adapter.setOnItemClickListener(position -> {
            Question question = collectionList.get(position);
            // TODO: 跳转到题目详情
            Toast.makeText(getContext(), "题目ID: " + question.getQuestionId(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadCollectionData() {
        // TODO: 实现收藏题目API调用
        // 目前显示空状态
        if (collectionList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvCollection.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvCollection.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSubjectChanged(int newSubject) {
        // 重新加载收藏数据
        loadCollectionData();
    }
}