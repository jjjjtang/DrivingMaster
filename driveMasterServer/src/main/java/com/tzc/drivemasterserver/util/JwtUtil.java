package com.tzc.drivemasterserver.util;

import io.jsonwebtoken.*;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "DriveMasterSecretKey";
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 24; // 1 天

    /** 生成 JWT */
    public static String generateToken(Long userId, String username, String password) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("username", username)
                .claim("password", password)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /** 解析 JWT */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    public static String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    public static String getPassword(String token) {
        return parseToken(token).get("password", String.class);
    }
}