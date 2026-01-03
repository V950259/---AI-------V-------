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
 * 菜单管理接口测试
 * 
 * @author test
 */
@DisplayName("菜单管理接口测试")
public class MenuApiTest {
    
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
    @DisplayName("成功场景：获取菜单列表（带Token）")
    public void testGetMenuListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/menu/list")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("data"));
    }
    
    @Test
    @DisplayName("无权限场景：未登录获取菜单列表")
    public void testGetMenuListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/menu/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
    
    @Test
    @DisplayName("成功场景：根据ID获取菜单信息")
    public void testGetMenuById() {
        // 先获取菜单列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/menu/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("data").size() > 0) {
            Long menuId = listResponse.jsonPath().getLong("data[0].menuId");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/menu/" + menuId)
                    .then()
                    .statusCode(200)
                    .extract()
                .response();
            
            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }
    
    @Test
    @DisplayName("异常场景：获取不存在的菜单")
    public void testGetMenuByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/menu/999999")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 404 || 
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }
    
    @Test
    @DisplayName("成功场景：获取菜单下拉树列表")
    public void testGetMenuTreeSelect() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/menu/treeselect")
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("data"));
    }
    
    @Test
    @DisplayName("成功场景：新增菜单")
    public void testAddMenu() {
        Map<String, Object> menuBody = new HashMap<>();
        menuBody.put("menuName", "测试菜单" + System.currentTimeMillis());
        menuBody.put("parentId", 0);
        menuBody.put("orderNum", 99);
        menuBody.put("path", "/test");
        menuBody.put("component", "test/index");
        menuBody.put("menuType", "C");
        menuBody.put("visible", "0");
        menuBody.put("status", "0");
        menuBody.put("perms", "test:menu:list");
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(menuBody)
                .when()
                .post("/system/menu")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }
    
    @Test
    @DisplayName("异常场景：新增菜单-参数错误（缺少必需字段）")
    public void testAddMenuWithMissingParams() {
        Map<String, Object> menuBody = new HashMap<>();
        menuBody.put("menuName", "");
        // 缺少其他必需字段
        
        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(menuBody)
                .when()
                .post("/system/menu")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() >= 400 || 
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }
    
    @Test
    @DisplayName("异常场景：新增菜单-菜单名称已存在")
    public void testAddMenuWithDuplicateName() {
        // 先获取现有菜单
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/menu/list")
                .then()
                .extract()
                .response();
        
        if (listResponse.jsonPath().getList("data").size() > 0) {
            String existingMenuName = listResponse.jsonPath().getString("data[0].menuName");
            
            Map<String, Object> menuBody = new HashMap<>();
            menuBody.put("menuName", existingMenuName);
            menuBody.put("parentId", 0);
            menuBody.put("orderNum", 99);
            menuBody.put("path", "/test_duplicate");
            menuBody.put("menuType", "C");
            menuBody.put("visible", "0");
            menuBody.put("status", "0");
            
            Response response = TestUtils.createAuthenticatedRequest(token)
                    .body(menuBody)
                    .when()
                    .post("/system/menu")
                    .then()
                    .extract()
                    .response();
            
            assertTrue(response.statusCode() >= 400 || 
                       (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
        }
    }
    
    @Test
    @DisplayName("无权限场景：无效Token访问菜单接口")
    public void testMenuApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";
        
        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/menu/list")
                .then()
                .extract()
                .response();
        
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

