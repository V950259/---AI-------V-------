package com.ruoyi.test.config;

/**
 * 测试配置类
 * 
 * @author test
 */
public class TestConfig {
    
    // 测试服务器地址 - 请确保后端服务已启动
    public static final String BASE_URL = "http://127.0.0.1:8080";
    
    // 测试用户名和密码
    public static final String TEST_USERNAME = "admin";
    public static final String TEST_PASSWORD = "admin123";
    
    // Token Header名称
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}

