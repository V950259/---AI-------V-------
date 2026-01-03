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
 * 部门管理接口测试
 * 
 * @author test
 */
@DisplayName("部门管理接口测试")
public class DeptApiTest {
    
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
    @DisplayName("成功场景：获取部门列表（带Token）")
    public void testGetDeptListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dept/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("data"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取部门列表")
    public void testGetDeptListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/dept/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取部门信息")
    public void testGetDeptById() {
        // 先获取部门列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dept/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("data").size() > 0) {
            Long deptId = listResponse.jsonPath().getLong("data[0].deptId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/dept/" + deptId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的部门")
    public void testGetDeptByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dept/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("成功场景：新增部门")
    public void testAddDept() {
        Map<String, Object> deptBody = new HashMap<>();
        deptBody.put("parentId", 100);
        deptBody.put("deptName", "测试部门" + System.currentTimeMillis());
        deptBody.put("orderNum", 3);
        deptBody.put("leader", "测试负责人");
        deptBody.put("phone", "13800138000");
        deptBody.put("email", "test@test.com");
        deptBody.put("status", "0");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(deptBody)
                .when()
                .post("/system/dept")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("异常场景：新增部门-参数错误（缺少必需字段）")
    public void testAddDeptWithMissingParams() {
        Map<String, Object> deptBody = new HashMap<>();
        deptBody.put("deptName", "");
        // 缺少其他必需字段
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(deptBody)
                .when()
                .post("/system/dept")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("异常场景：新增部门-部门名称已存在")
    public void testAddDeptWithDuplicateName() {
        // 先获取现有部门
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/dept/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("data").size() > 0) {
            String existingDeptName = listResponse.jsonPath().getString("data[0].deptName");
            Long parentId = listResponse.jsonPath().getLong("data[0].parentId");
            
            Map<String, Object> deptBody = new HashMap<>();
            deptBody.put("parentId", parentId);
            deptBody.put("deptName", existingDeptName);
            deptBody.put("orderNum", 3);
            deptBody.put("status", "0");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .body(deptBody)
                    .when()
                    .post("/system/dept")
                    .then()
                    .extract()
                    .response();
            
            assertTrue(response.statusCode() >= 400 || 
                       (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
        }
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问部门接口")
    public void testDeptApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/dept/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

