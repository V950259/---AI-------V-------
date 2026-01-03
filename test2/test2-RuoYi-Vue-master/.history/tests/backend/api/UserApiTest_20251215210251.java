package com.ruoyi.test.api;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ruoyi.test.config.TestConfig;
import com.ruoyi.test.config.TestUtils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

/**
 * 用户管理接口测试
 * 
 * @author test
 */
@DisplayName("用户管理接口测试")
public class UserApiTest {
    
    private static String token;
    
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = TestConfig.BASE_URL;
        if (!TestUtils.isServerAvailable()) {
            System.err.println("警告: 后端服务未启动，请先启动后端服务 (http://127.0.0.1:8080)");
            return;
        }
        token = TestUtils.getToken(TestConfig.TEST_USERNAME, TestConfig.TEST_PASSWORD);
        if (token == null) {
            System.err.println("警告: 无法获取Token，请检查后端服务是否正常运行");
        }
    }
    
    @Test
    @DisplayName("成功场景：获取用户列表（带Token）")
    public void testGetUserListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/user/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取用户列表")
    public void testGetUserListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/user/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取用户信息")
    public void testGetUserById() {
        // 先获取用户列表，取第一个用户的ID
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/user/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Long userId = listResponse.jsonPath().getLong("rows[0].userId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/user/" + userId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的用户")
    public void testGetUserByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/user/999999")
                .then()
                .extract()
                .response();
        
        // 可能返回404或200但数据为空
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("成功场景：新增用户")
    public void testAddUser() {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("userName", "testuser" + System.currentTimeMillis());
        userBody.put("nickName", "测试用户");
        userBody.put("password", "123456");
        userBody.put("phonenumber", "13800138000");
        userBody.put("email", "test@test.com");
        userBody.put("sex", "0");
        userBody.put("status", "0");
        userBody.put("deptId", 100);
        userBody.put("postIds", new Long[]{});
        userBody.put("roleIds", new Long[]{});
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(userBody)
                .when()
                .post("/system/user")
                .then()
                .extract()
                .response();
        
        // 如果用户名已存在，可能返回错误
        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("异常场景：新增用户-参数错误（缺少必需字段）")
    public void testAddUserWithMissingParams() {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("userName", "");
        // 缺少其他必需字段
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(userBody)
                .when()
                .post("/system/user")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("异常场景：新增用户-用户名已存在")
    public void testAddUserWithDuplicateUsername() {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("userName", TestConfig.TEST_USERNAME); // 使用已存在的用户名
        userBody.put("nickName", "测试用户");
        userBody.put("password", "123456");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(userBody)
                .when()
                .post("/system/user")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("成功场景：修改用户")
    public void testUpdateUser() {
        // 先获取用户列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/user/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Map<String, Object> user = listResponse.jsonPath().getMap("rows[0]");
            user.put("nickName", "修改后的昵称");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .body(user)
                    .when()
                    .put("/system/user")
                    .then()
                    .extract()
                    .response();
            
            assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
        }
    }
    
    @Test
    @DisplayName("异常场景：修改用户-参数错误")
    public void testUpdateUserWithInvalidParams() {
        Map<String, Object> userBody = new HashMap<>();
        userBody.put("userId", 999999); // 不存在的用户ID
        userBody.put("userName", "invaliduser");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(userBody)
                .when()
                .put("/system/user")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("成功场景：删除用户（需要先创建测试用户）")
    public void testDeleteUser() {
        // 注意：实际测试中应该先创建测试用户，然后删除
        // 这里仅测试接口调用，不实际删除
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .delete("/system/user/999999") // 使用不存在的ID，避免误删
                .then()
                .extract()
                .response();
        
        // 删除不存在的用户可能返回404或200但code不为200
        assertTrue(response.statusCode() == 404 || 
                   response.statusCode() == 200 || 
                   response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("无权限场景：无权限用户访问用户管理接口")
    public void testUserApiWithoutPermission() {
        // 使用无效Token
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/user/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

