package com.ruoyi.test.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 验证码配置测试
 * 
 * @author ruoyi
 */
@SpringBootTest
@ActiveProfiles("test")
public class CaptchaConfigTest 
{
    @Autowired
    private RuoYiConfig ruoyiConfig;
    
    @Autowired
    private ISysConfigService configService;
    
    @Autowired
    private SysLoginService sysLoginService;
    
    /**
     * 测试验证码配置是否正确读取
     */
    @Test
    public void testCaptchaConfig()
    {
        // 验证配置文件中的验证码开关是否为false
        assertFalse(ruoyiConfig.isCaptchaEnabled(), "验证码应该被禁用");
        
        // 验证通过配置服务读取的验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        assertFalse(captchaEnabled, "通过配置服务读取的验证码应该被禁用");
        
        System.out.println("✓ 验证码配置测试通过 - 验证码已禁用");
    }
    
    /**
     * 测试验证码类型配置
     */
    @Test
    public void testCaptchaTypeConfig()
    {
        String captchaType = ruoyiConfig.getCaptchaType();
        assertEquals("math", captchaType, "验证码类型应该为math");
        
        System.out.println("✓ 验证码类型配置测试通过 - 类型: " + captchaType);
    }
}