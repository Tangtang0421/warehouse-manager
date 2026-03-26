package com.qlx.oa.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    private static String secret;
    private static long expiration;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    @Value("${jwt.expiration}")
    public void setExpiration(long expiration) {
        JwtUtils.expiration = expiration;
    }

    // 【核心改进】：根据字符串生成一个安全的 SecretKey 对象
    private static SecretKey getSigningKey() {
        // 使用 getBytes 指定 UTF_8 编码，防止跨平台乱码
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // hmacShaKeyFor 会自动根据你的密钥长度和算法要求进行适配
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public static String generateToken(Integer userId, String no) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("no", no);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用生成的 Key 对象
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 验票也使用同样的 Key 对象
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}