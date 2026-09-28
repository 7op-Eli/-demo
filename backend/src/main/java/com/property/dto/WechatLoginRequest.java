package com.property.dto;

import lombok.Data;

/**
 * 微信授权登录请求。
 *
 * 安全约定：
 * - 正式链路只信任 {@code code}（微信小程序 uni.login() 返回的临时凭证），
 *   后端用 code + AppSecret 调微信 code2session 换 openid，再匹配本地账号。
 * - Demo 链路：{@code demoKey} 为手机号，后端匹配 owner/employee/government 表
 *   自动建号并分配正确角色，仅当未配置正式微信 AppID 时启用。
 */
@Data
public class WechatLoginRequest {

    /** 微信小程序 uni.login() 返回的临时 code（5 分钟有效），正式链路必填 */
    private String code;

    /**
     * Demo 模式标识（开发自测用）。
     * 仅当未配置正式微信 AppID 时启用，后端会生成 wx_demo_{demoKey} 账号，
     * 不会与真实手机号匹配的业主/员工账号产生交集。
     */
    private String demoKey;

    /** 经度，demo 环境可选 */
    private Double longitude;

    /** 纬度，demo 环境可选 */
    private Double latitude;
}
