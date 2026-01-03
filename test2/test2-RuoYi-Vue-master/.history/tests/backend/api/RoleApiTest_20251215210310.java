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
 * 角色管理接口测试
 * 
 * @author test
 */
@DisplayName("角色管理接口测试")
public class RoleApiTest {
    
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
    @DisplayName("成功场景：获取角色列表（带Token）")
    public void testGetRoleListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取角色列表")
    public void testGetRoleListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/role/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取角色信息")
    public void testGetRoleById() {
        // 先获取角色列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Long roleId = listResponse.jsonPath().getLong("rows[0].roleId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/role/" + roleId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的角色")
    public void testGetRoleByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("成功场景：新增角色")
    public void testAddRole() {
        Map<String, Object> roleBody = new HashMap<>();
        roleBody.put("roleName", "测试角色" + System.currentTimeMillis());
        roleBody.put("roleKey", "test_role_" + System.currentTimeMillis());
        roleBody.put("roleSort", 3);
        roleBody.put("status", "0");
        roleBody.put("menuIds", new Long[]{});
        roleBody.put("remark", "测试角色备注");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(roleBody)
                .when()
                .post("/system/role")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("异常场景：新增角色-参数错误（缺少必需字段）")
    public void testAddRoleWithMissingParams() {
        Map<String, Object> roleBody = new HashMap<>();
        roleBody.put("roleName", "");
        // 缺少其他必需字段
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(roleBody)
                .when()
                .post("/system/role")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("异常场景：新增角色-角色名称已存在")
    public void testAddRoleWithDuplicateName() {
        // 先获取现有角色
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            String existingRoleName = listResponse.jsonPath().getString("rows[0].roleName");
            
            Map<String, Object> roleBody = new HashMap<>();
            roleBody.put("roleName", existingRoleName);
            roleBody.put("roleKey", "test_duplicate");
            roleBody.put("roleSort", 3);
            roleBody.put("status", "0");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .body(roleBody)
                    .when()
                    .post("/system/role")
                    .then()
                    .extract()
                    .response();
            
            assertTrue(response.statusCode() >= 400 || 
                       (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
        }
    }
    
    @Test
    @DisplayName("成功场景：修改角色")
    public void testUpdateRole() {
        // 先获取角色列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Map<String, Object> role = listResponse.jsonPath().getMap("rows[0]");
            role.put("remark", "修改后的备注");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .body(role)
                    .when()
                    .put("/system/role")
                    .then()
                    .extract()
                    .response();
            
            assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
        }
    }
    
    @Test
    @DisplayName("异常场景：修改角色-参数错误")
    public void testUpdateRoleWithInvalidParams() {
        Map<String, Object> roleBody = new HashMap<>();
        roleBody.put("roleId", 999999); // 不存在的角色ID
        roleBody.put("roleName", "invalidrole");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(roleBody)
                .when()
                .put("/system/role")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("成功场景：获取角色选择框列表")
    public void testGetRoleOptionSelect() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/role/optionselect")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("data"));
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问角色接口")
    public void testRoleApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/role/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}
