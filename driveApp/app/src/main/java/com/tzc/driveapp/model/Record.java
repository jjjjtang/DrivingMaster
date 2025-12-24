package com.tzc.driveapp.model;

public class Record {

    private Long recordId;
    private Long userId;

    /**
     * 注意：这是一个 JSON 字符串
     * 例如：{"wrongIds":[1,2,3]}
     */
    private String detail;

    /**
     * 例如："0.00%"
     */
    private String correctRate;

    /**
     * 例如："2025-12-24T05:55:23"
     */
    private String startTime;

    private String endTime;

    /**
     * 例如："00-00-05"
     */
    private String time;

    private String subject;
    private String method;

    private Integer totalNum;
    private Integer correctNum;

    // ===== Getter / Setter =====

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCorrectRate() {
        return correctRate;
    }

    public void setCorrectRate(String correctRate) {
        this.correctRate = correctRate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getCorrectNum() {
        return correctNum;
    }

    public void setCorrectNum(Integer correctNum) {
        this.correctNum = correctNum;
    }
}