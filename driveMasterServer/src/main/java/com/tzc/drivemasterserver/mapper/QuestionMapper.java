package com.tzc.drivemasterserver.mapper;

import com.tzc.drivemasterserver.model.Question;
import com.tzc.drivemasterserver.model.dao.QuestionRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("""
        INSERT INTO question
        (question_no, `type`, content, chapter, `option`, difficuity, `explain`, img, subject)
        VALUES
        (#{questionNo}, #{type}, #{content}, #{chapter},
         #{option}, #{difficuity}, #{explain}, #{img}, #{subject})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    int insert(Question question);

    @Update("""
        UPDATE question SET
            content = #{content},
            chapter = #{chapter},
            `option` = #{option},
            difficuity = #{difficuity},
            subject = #{subject}
        WHERE question_id = #{questionId}
    """)
    int update(QuestionRequest questionRequest);

    @Delete("""
        DELETE FROM question
        WHERE question_id = #{questionId}
    """)
    int deleteById(Long questionId);

    @Select("""
        SELECT
            question_id   AS questionId,
            question_no   AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE question_id = #{questionId}
    """)
    Question selectById(Long questionId);

    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
    """)
    List<Question> selectAll();

    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE subject = #{subject}
    """)
    List<Question> selectBySubject(String subject);

    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Question> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE `type`= #{type}
    """)
    List<Question> selectByType(@Param("type") String type);


    /***
     * 按题型分页查询
     */
    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE `type`= #{type}
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Question> selectByTypeAndPage(@Param("type") String type, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /***
     * 按照章节分页查询
     */
    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE chapter = #{chapter}
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Question> selectByChapterAndPage(@Param("chapter") String chapter, @Param("offset") int offset, @Param("pageSize") int pageSize);

    /***
     * 对题干content模糊查询
     */
    @Select("""
        SELECT
            question_id AS questionId,
            question_no AS questionNo,
            `type`,
            content,
            chapter,
            `option`,
            difficuity,
            `explain`,
            img,
            subject
        FROM question
        WHERE content LIKE CONCAT('%', #{keyword}, '%')
    """)
    List<Question> selectByContentKeyword(@Param("keyword") String keyword);

}