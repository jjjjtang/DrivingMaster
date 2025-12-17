package com.tzc.drivemasterserver.mapper;

import com.tzc.drivemasterserver.model.Log;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LogMapper {

    /**
     * 新增日志
     */
    @Insert("""
        INSERT INTO log
        (content, create_time, user_id, username)
        VALUES
        (#{content}, #{createTime}, #{userId}, #{username})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "logId")
    int insert(Log log);

    /**
     * 根据 ID 查询日志
     */
    @Select("""
        SELECT
            log_id   AS logId,
            content,
            create_time AS createTime,
            user_id  AS userId,
            username
        FROM log
        WHERE log_id = #{logId}
    """)
    Log selectById(Long logId);

    /**
     * 查询全部日志（按时间倒序）
     */
    @Select("""
        SELECT
            log_id   AS logId,
            content,
            create_time AS createTime,
            user_id  AS userId,
            username
        FROM log
        ORDER BY create_time DESC
    """)
    List<Log> selectAll();

    /**
     * 删除日志
     */
    @Delete("""
        DELETE FROM log
        WHERE log_id = #{logId}
    """)
    int deleteById(Long logId);
}