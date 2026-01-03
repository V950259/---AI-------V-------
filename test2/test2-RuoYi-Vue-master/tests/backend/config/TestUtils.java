package com.ruoyi.test.config;

import java.util.HashMap;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 测试工具类
 */
public class TestUtils {

    /**
     * 获取登录Token
     */
    public static String getToken(String username, String password) {
        try {
            Map<String, Object> loginBody = new HashMap<>();
            loginBody.put("username", username);
            loginBody.put("password", password);
            loginBody.put("code", ""); // 验证码为空（需要后端开启验证码白名单或关闭验证码）
            loginBody.put("uuid", "");

            Response response = RestAssured.given()
                    .baseUri(com.ruoyi.test.config.TestConfig.BASE_URL)
                    .contentType("application/json")
                    .body(loginBody)
                    .when()
                    .post("/login") // 确保路径没有 /dev-api 前缀
                    .then()
                    .extract()
                    .response();

            if (response.statusCode() == 200) {
                // 尝试直接获取 token
                String token = response.jsonPath().getString("token");
                // 如果为空，尝试从 data 对象中获取（适配不同版本的若依）
                if (token == null) {
                    token = response.jsonPath().getString("data.token");
                }
                // 再没有就返回 null
                if (token == null && response.jsonPath().get("token") != null) {
                    token = response.jsonPath().get("token").toString();
                }
                return token;
            } else {
                System.err.println("登录失败，状态码: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 创建带Token的请求
     */
    public static RequestSpecification createAuthenticatedRequest(String token) {
        return RestAssured.given()
                .baseUri(com.ruoyi.test.config.TestConfig.BASE_URL)
                .header(com.ruoyi.test.config.TestConfig.TOKEN_HEADER, com.ruoyi.test.config.TestConfig.TOKEN_PREFIX + token)
                .contentType("application/json");
    }

    /**
     * 创建不带Token的请求
     */
    public static RequestSpecification createUnauthenticatedRequest() {
        return RestAssured.given()
                .baseUri(com.ruoyi.test.config.TestConfig.BASE_URL)
                .contentType("application/json");
    }

    /**
     * 检查服务是否可用
     */
    public static boolean isServerAvailable() {
        try {
            return RestAssured.given()
                    .baseUri(com.ruoyi.test.config.TestConfig.BASE_URL)
                    .when().get("/")
                    .then().extract().statusCode() != 0;
        } catch (Exception e) {
            return false;
        }
    }
}