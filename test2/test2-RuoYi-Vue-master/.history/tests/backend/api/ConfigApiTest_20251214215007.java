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
 * 参数配置接口测试
 * 
 * @author test
 */
@DisplayName("参数配置接口测试")
public class ConfigApiTest {
    
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
    @DisplayName("成功场景：获取参数配置列表（带Token）")
    public void testGetConfigListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/config/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取参数配置列表")
    public void testGetConfigListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/config/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取参数配置信息")
    public void testGetConfigById() {
        // 先获取参数配置列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/config/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Long configId = listResponse.jsonPath().getLong("rows[0].configId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/config/" + configId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的参数配置")
    public void testGetConfigByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/config/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问参数配置接口")
    public void testConfigApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/config/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

