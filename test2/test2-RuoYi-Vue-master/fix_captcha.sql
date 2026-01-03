-- 临时禁用验证码，解决Redis未启动导致的登录问题
-- 执行此脚本前请确保已连接到ruoyi数据库

UPDATE sys_config 
SET config_value = 'false', 
    remark = '是否开启验证码功能（true开启，false关闭）- 临时关闭以解决Redis问题'
WHERE config_key = 'sys.account.captchaEnabled';

-- 查询修改结果
SELECT config_key, config_value, remark 
FROM sys_config 
WHERE config_key = 'sys.account.captchaEnabled';