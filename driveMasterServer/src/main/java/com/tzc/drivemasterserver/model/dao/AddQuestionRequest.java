package com.tzc.drivemasterserver.model.dao;

public class AddQuestionRequest {
    //题号根据每个章节的最新一个题目题号递增
    private String type;
    private String content;
    private String chapter;
    private String option;
    private String difficuity;
    private String subject;

    private Long userId;
    private String username;
}
