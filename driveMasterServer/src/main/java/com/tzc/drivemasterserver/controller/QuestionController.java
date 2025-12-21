package com.tzc.drivemasterserver.controller;

import com.tzc.drivemasterserver.model.Question;
import com.tzc.drivemasterserver.model.dao.QuestionRequest;
import com.tzc.drivemasterserver.service.LogService;
import com.tzc.drivemasterserver.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
@CrossOrigin
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private LogService logService;

    /***
     * 添加题目
     * @param question
     * @return
     */
    @PostMapping("/add")
    public boolean addQuestion(@RequestBody Question question) {
        logService.addLog("添加题目: " + question.getContent(), null, null);
        return questionService.addQuestion(question);
    }

    /**
     * 修改题目
     */
    @PostMapping("/update")
    public boolean updateQuestion(@RequestBody QuestionRequest questionRequest) {
        logService.addLog("修改题目 ID: " + questionRequest.getQuestionId(), null, null);
        return questionService.updateQuestion(questionRequest);
    }

    /**
     * 删除题目
     */
    @GetMapping("/delete/{id}")
    public boolean deleteQuestion(@PathVariable("id") Long id) {
        logService.addLog("删除题目 ID: " + id, null, null);
        return questionService.deleteQuestion(id);
    }

    /**
     * 根据 ID 查询题目
     */
    @GetMapping("/{id}")
    public Question getById(@PathVariable("id") Long id) {
        return questionService.getQuestionById(id);
    }

    /**
     * 查询全部题目
     */
    @GetMapping("/list")
    public List<Question> list() {
        return questionService.getAllQuestions();
    }

    /**
     * 按科目查询（科目一 / 科目四）
     */
    @GetMapping("/subject/{subject}")
    public List<Question> listBySubject(@PathVariable String subject) {
        return questionService.getQuestionsBySubject(subject);
    }

    /**
     * 分页查询 100题一页
     */
    @GetMapping("/page/{pageNumber}")
    public List<Question> listByPage(@PathVariable int pageNumber) {
        return questionService.getQuestionsByPage(pageNumber);
    }

    /***
     * 按题型分页查询
     */
    @GetMapping("/type/{type}/page/{pageNumber}")
    public List<Question> listByTypeAndPage(@PathVariable String type, @PathVariable int pageNumber) {
        return questionService.getQuestionsByTypeAndPage(type, pageNumber);
    }

    /***
     * 按章节分页查询
     */
    @GetMapping("/chapter/{chapter}/page/{pageNumber}")
    public List<Question> listByChapterAndPage(@PathVariable String chapter, @PathVariable int pageNumber) {
        return questionService.getQuestionsByChapterAndPage(chapter, pageNumber);
    }

    /***
     * 对题干content模糊查询
     */
    @GetMapping("/search")
    public List<Question> searchByContent(@RequestParam String keyword) {
        return questionService.getQuestionsByContentKeyword(keyword);
    }
}