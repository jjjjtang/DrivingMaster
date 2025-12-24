package com.tzc.driveapp.model;

import com.google.gson.annotations.SerializedName;

public class IncorrectQuestion {
    @SerializedName("id")
    private Long id;

    @SerializedName("questionId")
    private Long questionId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("question")
    private Question question;  // 完整的题目对象

    @SerializedName("createTime")
    private String createTime;

    @SerializedName("updateTime")
    private String updateTime;

    // 构造函数
    public IncorrectQuestion() {}

    // Getter和Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}