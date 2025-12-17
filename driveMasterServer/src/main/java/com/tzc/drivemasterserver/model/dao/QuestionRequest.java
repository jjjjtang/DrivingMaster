package com.tzc.drivemasterserver.model.dao;

public class QuestionRequest {
    private Long questionId;
    private String content;
    private String chapter;
    private String option;
    private String difficuity;
    private String subject;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getDifficuity() {
        return difficuity;
    }

    public void setDifficuity(String difficuity) {
        this.difficuity = difficuity;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
