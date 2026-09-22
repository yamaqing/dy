package com.tuling.tulingmall.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtTokenUtil
 *
 * M0-2 迁移说明：原类位于幻影模块 tulingmall-security（目录不存在，无法迁移）。
 * 此处为按原包名内联重建的最小实现（HMAC + jjwt 0.10.5），保证 admin 模块
 * 原 import 与调用（generateToken/refreshHeadToken 等）零改动编译通过。
 * TODO(M3)：统一认证中心（jjwt 0.12.5 + RSA 非对称密钥）接管，本类届时废弃。
 */
public class JwtTokenUtil {

    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";
    /** TODO(M3)：密钥迁移至配置中心，并由统一认证中心签发 */
    private static final String SECRET = "turingmall-secret-m0-stub";
    /** 过期时间：7 天（秒） */
    private static final Long EXPIRATION = 604800L;

    /**
     * 根据用户信息生成 token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从 token 中获取登录用户名
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 校验 token 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * 刷新 token（原 token 未过期时生成新 token）
     */
    public String refreshHeadToken(String oldToken) {
        if (isTokenExpired(oldToken)) {
            return null;
        }
        Claims claims = getClaimsFromToken(oldToken);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 根据负载生成 token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 从 token 中获取负载
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 生成 token 过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION * 1000);
    }

    /**
     * 判断 token 是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从 token 中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }
}
