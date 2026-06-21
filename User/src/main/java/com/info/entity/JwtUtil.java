package com.info.entity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey}")
    private String secret;

    @Value("${jwt.expire:86400000}")
    private Long expire;

    // 生成token
    public String generateToken(Integer userId,String username) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expire);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // 获取用户 id
    public Integer getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return Integer.parseInt(claims.getSubject());

        }catch (Exception e){
            log.error("获取用户 id 失败：{}", e.getMessage());
            return null;
        }
    }

    // 获取用户名

    public String getUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("username", String.class);
        } catch (Exception e) {
            log.error("获取用户名失败：{}", e.getMessage());
            return null;
        }
    }

    // 验证 token是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("验证 token 失败：{}", e.getMessage());
            return false;
        }
    }

    // 获取token过期时间
    public Long getExpireTime(String token) {
        return expire;
    }

}
