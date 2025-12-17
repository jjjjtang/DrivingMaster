package com.tzc.drivemasterserver.service;

import com.tzc.drivemasterserver.mapper.LogMapper;
import com.tzc.drivemasterserver.mapper.QuestionMapper;
import com.tzc.drivemasterserver.model.Question;
import com.tzc.drivemasterserver.model.dao.QuestionRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private LogMapper logMapper;

    /**
     * 新增题目
     */
    public boolean addQuestion(Question question) {
        return questionMapper.insert(question) > 0;
    }

    /**
     * 修改题目
     */
    public boolean updateQuestion(QuestionRequest questionRequest) {
        return questionMapper.update(questionRequest) > 0;
    }

    /**
     * 删除题目
     */
    public boolean deleteQuestion(Long questionId) {
        return questionMapper.deleteById(questionId) > 0;
    }

    /**
     * 根据 ID 查询
     */
    public Question getQuestionById(Long questionId) {
        return questionMapper.selectById(questionId);

    }

    /**
     * 查询全部题目
     */
    public List<Question> getAllQuestions() {
        return questionMapper.selectAll();
    }

    /**
     * 按科目查询（科目一 / 科目四）
     */
    public List<Question> getQuestionsBySubject(String subject) {
        return questionMapper.selectBySubject(subject);
    }

    /***
     * 分页查询
     */
    public List<Question> getQuestionsByPage(int pageNumber) {
        int pageSize = 50;
        int offset = (pageNumber - 1) * pageSize;
        return questionMapper.selectByPage(offset, pageSize);
    }

    /***
     * 按题型分页查询
     */
    public List<Question> getQuestionsByTypeAndPage(String type, int pageNumber) {
        int pageSize = 50;
        int offset = (pageNumber - 1) * pageSize;
        return questionMapper.selectByTypeAndPage(type, offset, pageSize);
    }

    /***
     * 按章节分页查询
     */
    public List<Question> getQuestionsByChapterAndPage(String chapter, int pageNumber) {
        int pageSize = 50;
        int offset = (pageNumber - 1) * pageSize;
        return questionMapper.selectByChapterAndPage(chapter, offset, pageSize);
    }

    /***
     * 对题干content模糊查询
     */
    public List<Question> getQuestionsByContentKeyword(String keyword) {
        return questionMapper.selectByContentKeyword(keyword);
    }
}