package com.ruoyi.test.api;

import com.ruoyi.test.config.TestConfig;
import com.ruoyi.test.config.TestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 监控接口测试
 * 
 * @author test
 */
@DisplayName("监控接口测试")
public class MonitorApiTest {
    
    private static String token;
    
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = TestConfig.BASE_URL;
        if (!TestUtils.isServerAvailable()) {
            System.err.println("警告: 后端服务未启动，请先启动后端服务 (http://localhost:8080)");
            return;
        }
        token = TestUtils.getToken(TestConfig.TEST_USERNAME, TestConfig.TEST_PASSWORD);
        if (token == null) {
            System.err.println("警告: 无法获取Token，请检查后端服务是否正常运行");
        }
    }
    
    @Test
    @DisplayName("成功场景：获取在线用户列表（带Token）")
    public void testGetOnlineUserListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/monitor/online/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取在线用户列表")
    public void testGetOnlineUserListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/monitor/online/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：获取登录日志列表（带Token）")
    public void testGetLoginLogListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/monitor/logininfor/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取登录日志列表")
    public void testGetLoginLogListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/monitor/logininfor/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：获取操作日志列表（带Token）")
    public void testGetOperLogListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/monitor/operlog/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取操作日志列表")
    public void testGetOperLogListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/monitor/operlog/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：获取服务器信息（带Token）")
    public void testGetServerInfoWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/monitor/server")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("data"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取服务器信息")
    public void testGetServerInfoWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/monitor/server")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问监控接口")
    public void testMonitorApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/monitor/online/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

