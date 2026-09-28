package com.property.security;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.property.config.WechatProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 微信小程序 code2session 调用工具。
 *
 * 标准链路：前端 uni.login() 拿到 code → 后端用 code + AppSecret 调微信接口
 * → 微信返回 openid（用户在该小程序下的唯一标识）。
 *
 * 安全要点：
 * - 身份由微信服务器担保，前端不传、也不需要传手机号。
 * - AppSecret 只在后端使用，永不下发给前端。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WechatUtil {

    private static final String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final WechatProperties wechatProperties;

    /**
     * 用微信 code 换取 openid。
     *
     * @param code 微信小程序 uni.login() 返回的临时 code（5 分钟有效）
     * @return 微信 openid，失败返回 null
     */
    public String getOpenidByCode(String code) {
        if (!wechatProperties.isConfigured()) {
            log.warn("微信 AppID/Secret 未配置，无法走 code2session 链路");
            return null;
        }
        if (code == null || code.isBlank()) {
            log.warn("微信 code 为空，无法换取 openid");
            return null;
        }

        // 拼装请求参数：appid + secret + js_code + grant_type=authorization_code
        String url = CODE2SESSION_URL
                + "?appid=" + wechatProperties.getAppid()
                + "&secret=" + wechatProperties.getSecret()
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        try {
            String resp = HttpUtil.get(url, 5000);
            log.debug("微信 code2session 响应: {}", resp);
            JSONObject json = JSONUtil.parseObj(resp);

            // 微信接口错误时返回 errcode
            Integer errcode = json.getInt("errcode");
            if (errcode != null && errcode != 0) {
                log.error("微信 code2session 失败: errcode={}, errmsg={}", errcode, json.getStr("errmsg"));
                return null;
            }

            String openid = json.getStr("openid");
            if (openid == null || openid.isBlank()) {
                log.error("微信 code2session 未返回 openid: {}", resp);
                return null;
            }
            return openid;
        } catch (Exception e) {
            log.error("调用微信 code2session 异常", e);
            return null;
        }
    }
}
