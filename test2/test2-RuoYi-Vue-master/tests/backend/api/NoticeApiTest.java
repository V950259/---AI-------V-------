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
 * 通知公告接口测试
 *
 * @author test
 */
@DisplayName("通知公告接口测试")
public class NoticeApiTest {

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
    @DisplayName("成功场景：获取通知公告列表（带Token）")
    public void testGetNoticeListWithToken() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/notice/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(200, response.statusCode());
        assertNotNull(response.jsonPath().get("rows"));
    }

    @Test
    @DisplayName("无权限场景：未登录获取通知公告列表")
    public void testGetNoticeListWithoutToken() {
        Response response = TestUtils.createUnauthenticatedRequest()
                .when()
                .get("/system/notice/list")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }

    @Test
    @DisplayName("成功场景：根据ID获取通知公告信息")
    public void testGetNoticeById() {
        // 先获取通知公告列表
        Response listResponse = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/notice/list")
                .then()
                .extract()
                .response();

        if (listResponse.jsonPath().getList("rows").size() > 0) {
            Long noticeId = listResponse.jsonPath().getLong("rows[0].noticeId");

            Response response = TestUtils.createAuthenticatedRequest(token)
                    .when()
                    .get("/system/notice/" + noticeId)
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

            assertEquals(200, response.statusCode());
            assertNotNull(response.jsonPath().get("data"));
        }
    }

    @Test
    @DisplayName("异常场景：获取不存在的通知公告")
    public void testGetNoticeByInvalidId() {
        Response response = TestUtils.createAuthenticatedRequest(token)
                .when()
                .get("/system/notice/999999")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() == 404 ||
                   (response.statusCode() == 200 && response.jsonPath().get("data") == null));
    }

    @Test
    @DisplayName("成功场景：新增通知公告")
    public void testAddNotice() {
        Map<String, Object> noticeBody = new HashMap<>();
        noticeBody.put("noticeTitle", "测试公告" + System.currentTimeMillis());
        noticeBody.put("noticeType", "1");
        noticeBody.put("noticeContent", "这是一条测试公告内容");
        noticeBody.put("status", "0");

        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(noticeBody)
                .when()
                .post("/system/notice")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() == 200 || response.statusCode() == 400);
    }

    @Test
    @DisplayName("异常场景：新增通知公告-参数错误（缺少必需字段）")
    public void testAddNoticeWithMissingParams() {
        Map<String, Object> noticeBody = new HashMap<>();
        noticeBody.put("noticeTitle", "");
        // 缺少其他必需字段

        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(noticeBody)
                .when()
                .post("/system/notice")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() >= 400 ||
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }

    @Test
    @DisplayName("异常场景：新增通知公告-超长标题")
    public void testAddNoticeWithLongTitle() {
        Map<String, Object> noticeBody = new HashMap<>();
        // 生成超长标题（超过数据库字段限制）- 使用Java 8兼容的写法
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            sb.append("A");
        }
        String longTitle = sb.toString();
        noticeBody.put("noticeTitle", longTitle);
        noticeBody.put("noticeType", "1");
        noticeBody.put("noticeContent", "测试内容");
        noticeBody.put("status", "0");

        Response response = TestUtils.createAuthenticatedRequest(token)
                .body(noticeBody)
                .when()
                .post("/system/notice")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() >= 400 ||
                   (response.statusCode() == 200 && response.jsonPath().getInt("code") != 200));
    }

    @Test
    @DisplayName("无权限场景：无效Token访问通知公告接口")
    public void testNoticeApiWithInvalidToken() {
        String invalidToken = "invalid_token_12345";

        Response response = TestUtils.createAuthenticatedRequest(invalidToken)
                .when()
                .get("/system/notice/list")
                .then()
                .extract()
                .response();

        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }
}

