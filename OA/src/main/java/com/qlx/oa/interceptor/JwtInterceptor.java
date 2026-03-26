package com.qlx.oa.interceptor;

import com.qlx.oa.common.BusinessException;
import com.qlx.oa.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 允许OPTIONS预检请求直接通过
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String headerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(headerToken) || !headerToken.startsWith("Bearer ")) {
            throw new BusinessException(401, "无访问权限或Token格式错误，请先登录");
        }

        try {

            String token = headerToken.substring(7);


            Claims claims = JwtUtils.parseToken(token);

            String redisKey = "login:token:" + token;
            String cacheData = stringRedisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(cacheData)) {
                throw new BusinessException(401, "登录已过期，请重新登录");
            }

            stringRedisTemplate.expire(redisKey, 2, TimeUnit.HOURS);
            request.setAttribute("currentUserId", claims.get("userId"));

            return true;

        } catch (Exception e) {
            throw new BusinessException(401, "Token 无效、被篡改或已过期");
        }
    }
}
