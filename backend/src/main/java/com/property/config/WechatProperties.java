package com.property.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置。
 *
 * 安全要求：appid 与 secret 必须通过环境变量注入（如 WECHAT_APPID / WECHAT_SECRET），
 * 严禁硬编码到代码库。当 appid 为空时，系统进入 Demo 模式（仅生成独立 demo 账号）。
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WechatProperties {

    /** 微信小程序 AppID，为空表示未配置（走 Demo 模式） */
    private String appid;

    /** 微信小程序 AppSecret，必须通过环境变量注入 */
    private String secret;

    /**
     * 是否已配置正式微信凭据。appid 与 secret 均非空时才算正式链路。
     */
    public boolean isConfigured() {
        return appid != null && !appid.isBlank()
                && secret != null && !secret.isBlank();
    }
}
