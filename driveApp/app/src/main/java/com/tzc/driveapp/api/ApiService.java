package com.tzc.driveapp.api;

import com.tzc.driveapp.model.IncorrectQuestion;
import com.tzc.driveapp.model.Question;
import com.tzc.driveapp.model.Record;
import com.tzc.driveapp.model.User;
import com.tzc.driveapp.model.dao.user.UserLoginRequest;
import com.tzc.driveapp.model.dao.user.UserLoginResponse;
import com.tzc.driveapp.model.dao.user.UserRegisterRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 用户相关接口
    @POST("api/user/login")
    Call<UserLoginResponse> login(@Body UserLoginRequest request);

    @POST("api/user/register")
    Call<UserLoginResponse> register(@Body UserRegisterRequest request);

    @GET("api/user/info")
    Call<User> getUserInfo(@Query("userId") Long userId);

    // 题目相关接口
    @GET("api/question/list")
    Call<List<Question>> getQuestionList(
            @Query("subject") String subject,
            @Query("chapter") String chapter,
            @Query("type") String type,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/question/list")
    Call<List<Question>> getAllQuestions();

    // 获取题目列表
    @GET("api/question/list")
    Call<List<Question>> getQuestionList(
            @Query("subject") String subject,
            @Query("page") Integer page,
            @Query("size") Integer size
    );

    // 搜索题目
    @GET("api/question/search")
    Call<List<Question>> searchQuestions(
            @Query("keyword") String keyword
    );

    // 获取章节题目列表
    @GET("api/question/chapter/{chapterName}/page/{page}")
    Call<List<Question>> getChapterQuestions(
            @Path("chapterName") String chapterName,
            @Path("page") int page
    );

    // 在ApiService接口中添加更完整的题型刷题接口
    @GET("api/question/type/{type}/page/{page}")
    Call<List<Question>> getQuestionsByType(
            @Path("type") String type,  // "判断题"、"单选题"、"多选题"
            @Path("page") int page
    );

    // 可选：根据科目和题型获取题目
    @GET("api/question/type/{type}/subject/{subject}/page/{page}")
    Call<List<Question>> getQuestionsByTypeAndSubject(
            @Path("type") String type,
            @Path("subject") String subject,  // "1" 或 "4"
            @Path("page") int page
    );
    @GET("api/question/detail")
    Call<Question> getQuestionDetail(@Query("questionId") Long questionId);

    // 记录相关接口
    @POST("api/record/save")
    Call<Void> saveRecord(@Body Record record);

    @GET("api/record/list")
    Call<List<Record>> getRecordList(
            @Query("userId") Long userId,
            @Query("subject") String subject,
            @Query("method") String method
    );

    // 获取我的历史记录（需要 token）
    @GET("api/record/my")
    Call<List<Record>> getMyRecords();

    // 错题相关接口
    // 错题相关接口 - 修正版本
    @POST("api/incorrect/save")
    Call<Void> saveIncorrectQuestion(@Body IncorrectQuestion incorrectQuestion);

    // 方法1：获取我的错题（需要认证）
    @GET("api/incorrect/my")
    Call<List<IncorrectQuestion>> getMyIncorrectQuestions();

    // 方法2：通过userId获取错题（需要userId参数）
    @GET("api/incorrect/list")
    Call<List<IncorrectQuestion>> getIncorrectListByUserId(@Query("userId") Long userId);

    @DELETE("api/incorrect/delete")
    Call<Void> deleteIncorrectQuestion(@Query("id") Long id);
}