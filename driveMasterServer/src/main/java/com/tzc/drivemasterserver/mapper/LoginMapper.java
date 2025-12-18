package com.tzc.drivemasterserver.mapper;

import com.tzc.drivemasterserver.model.Login;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LoginMapper {

    @Insert("""
        INSERT INTO user
        (username, password, avatar, email, admin)
        VALUES
        (#{username}, #{password}, #{avatar}, #{email}, #{admin})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "userId", keyColumn = "user_id")
    int insert(Login login);

    @Update("""
        UPDATE user SET
            password = #{password},
            avatar = #{avatar},
            email = #{email},
            admin = #{admin}
        WHERE username = #{username}
    """)
    int update(Login login);

    @Delete("""
        DELETE FROM user
        WHERE username = #{username}
    """)
    int deleteByUsername(String username);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE username = #{username}
    """)
    Login selectByUsername(String username);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE user_id = #{userId}
    """)
    Login selectById(@Param("userId") Long userId);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE email = #{email}
    """)
    Login selectByEmail(String email);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
    """)
    List<Login> selectAll();

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE username = #{username} AND password = #{password}
    """)
    Login selectByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Update("""
        UPDATE user SET
            password = #{password}
        WHERE username = #{username}
    """)
    int updatePassword(@Param("username") String username, @Param("password") String password);

    @Update("""
        UPDATE user SET
            avatar = #{avatar}
        WHERE username = #{username}
    """)
    int updateAvatar(@Param("username") String username, @Param("avatar") String avatar);

    @Update("""
        UPDATE user SET
            email = #{email}
        WHERE username = #{username}
    """)
    int updateEmail(@Param("username") String username, @Param("email") String email);

    @Select("""
        SELECT COUNT(*)
        FROM user
        WHERE username = #{username}
    """)
    int countByUsername(String username);

    @Select("""
        SELECT COUNT(*)
        FROM user
        WHERE email = #{email}
    """)
    int countByEmail(String email);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Login> selectByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE admin = #{admin}
    """)
    List<Login> selectByAdmin(@Param("admin") String admin);

    /***
     * 按管理员状态分页查询
     */
    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE admin = #{admin}
        LIMIT #{pageSize} OFFSET #{offset}
    """)
    List<Login> selectByAdminAndPage(@Param("admin") String admin,
                                     @Param("offset") int offset,
                                     @Param("pageSize") int pageSize);

    /***
     * 对用户名模糊查询
     */
    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE username LIKE CONCAT('%', #{keyword}, '%')
    """)
    List<Login> selectByUsernameKeyword(@Param("keyword") String keyword);

    /***
     * 对邮箱模糊查询
     */
    @Select("""
        SELECT
            user_id AS userId,
            username,
            password,
            avatar,
            email,
            admin
        FROM user
        WHERE email LIKE CONCAT('%', #{keyword}, '%')
    """)
    List<Login> selectByEmailKeyword(@Param("keyword") String keyword);

    /***
     * 获取用户总数
     */
    @Select("""
        SELECT COUNT(*)
        FROM user
    """)
    int countAll();

    /***
     * 按管理员状态统计
     */
    @Select("""
        SELECT COUNT(*)
        FROM user
        WHERE admin = #{admin}
    """)
    int countByAdminStatus(@Param("admin") String admin);

    /***
     * 检查用户名和邮箱组合是否存在
     */
    @Select("""
        SELECT COUNT(*)
        FROM user
        WHERE username = #{username} AND email = #{email}
    """)
    int countByUsernameAndEmail(@Param("username") String username, @Param("email") String email);

    /***
     * 批量删除用户
     */
    @Delete("""
        <script>
        DELETE FROM user
        WHERE username IN
        <foreach collection="usernames" item="username" open="(" separator="," close=")">
            #{username}
        </foreach>
        </script>
    """)
    int batchDelete(@Param("usernames") List<String> usernames);

    /***
     * 批量插入用户
     */
    @Insert("""
        <script>
        INSERT INTO user
        (username, password, avatar, email, admin)
        VALUES
        <foreach collection="users" item="item" separator=",">
            (#{item.username}, #{item.password}, #{item.avatar}, #{item.email}, #{item.admin})
        </foreach>
        </script>
    """)
    int batchInsert(@Param("users") List<Login> users);
}