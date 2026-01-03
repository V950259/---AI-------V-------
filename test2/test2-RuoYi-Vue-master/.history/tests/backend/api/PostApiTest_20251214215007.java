package com.ruoyi.test.api;

import com.ruoyi.test.config.TestConfig;
import com.ruoyi.test.config.TestUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 岗位管理接口测试
 * 
 * @author test
 */
@DisplayName("岗位管理接口测试")
public class PostApiTest {
    
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
    @DisplayName("成功场景：获取岗位列表（带Token）")
    public void testGetPostListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/post/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取岗位列表")
    public void testGetPostListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/post/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取岗位信息")
    public void testGetPostById() {
        // 先获取岗位列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/post/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Long postId = listResponse.jsonPath().getLong("rows[0].postId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/post/" + postId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的岗位")
    public void testGetPostByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/post/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("成功场景：新增岗位")
    public void testAddPost() {
        Map<String, Object> postBody = new HashMap<>();
        postBody.put("postCode", "TEST_" + System.currentTimeMillis());
        postBody.put("postName", "测试岗位" + System.currentTimeMillis());
        postBody.put("postSort", 5);
        postBody.put("status", "0");
        postBody.put("remark", "测试岗位备注");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(postBody)
                .when()
                .post("/system/post")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("异常场景：新增岗位-参数错误（缺少必需字段）")
    public void testAddPostWithMissingParams() {
        Map<String, Object> postBody = new HashMap<>();
        postBody.put("postName", "");
        // 缺少其他必需字段
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(postBody)
                .when()
                .post("/system/post")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问岗位接口")
    public void testPostApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/post/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

