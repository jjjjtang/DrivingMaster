package com.tzc.drivemasterserver.mapper;

import com.tzc.drivemasterserver.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("""
        INSERT INTO user
        (username, password, avatar, email, admin, average_score, total_num)
        VALUES
        (#{username}, #{password}, #{avatar}, #{email}, #{admin},
         #{average_score}, #{total_num})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int insert(User user);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin,
            average_score,
            total_num
        FROM user
        WHERE username = #{username}
    """)
    User selectByUsername(String username);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin,
            average_score,
            total_num
        FROM user
        WHERE user_id = #{userId}
    """)
    User selectById(Long userId);

}