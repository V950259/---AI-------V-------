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
 * 字典管理接口测试
 * 
 * @author test
 */
@DisplayName("字典管理接口测试")
public class DictApiTest {
    
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
    @DisplayName("成功场景：获取字典类型列表（带Token）")
    public void testGetDictTypeListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dict/type/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取字典类型列表")
    public void testGetDictTypeListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/dict/type/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：获取字典数据列表（带Token）")
    public void testGetDictDataListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dict/data/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取字典数据列表")
    public void testGetDictDataListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/dict/data/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的字典类型")
    public void testGetDictTypeByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dict/type/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问字典接口")
    public void testDictApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/dict/type/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

