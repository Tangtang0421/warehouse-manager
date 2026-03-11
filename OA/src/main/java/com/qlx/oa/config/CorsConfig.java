package com.qlx.oa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 拦截所有的前端请求路径
        registry.addMapping("/**")
                // 允许哪些域名的前端来访问后端
                .allowedOriginPatterns("*")
                // 允许前端携带身份凭证
                .allowCredentials(true)
                // 允许前端使用的HTTP 动作
                .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
                // 允许前端携带的请求头
                .allowedHeaders("*")
                // 跨域探测请求的有效期（秒），避免前端频繁发 OPTIONS 预检请求
                .maxAge(3600);
    }
}