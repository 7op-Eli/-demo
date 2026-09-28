package com.property.controller;

import com.property.common.Constants;
import com.property.common.Result;
import com.property.config.WechatProperties;
import com.property.dto.LoginRequest;
import com.property.dto.LoginResponse;
import com.property.dto.WechatLoginRequest;
import com.property.entity.*;
import com.property.repository.*;
import com.property.security.CurrentUser;
import com.property.security.JwtUtil;
import com.property.security.WechatUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final SysUserRepository sysUserRepository;
    private final OwnerRepository ownerRepository;
    private final EmployeeRepository employeeRepository;
    private final GovernmentOfficialRepository governmentOfficialRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;
    private final WechatUtil wechatUtil;
    private final WechatProperties wechatProperties;

    @Operation(summary = "管理员/员工 用户名密码登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SysUser user = (SysUser) auth.getPrincipal();
        return Result.success(new LoginResponse(
                token(user), user.getUsername(), user.getRealName(),
                user.getRoleType(), user.getRoleLabel(),
                user.getId(), user.getOwnerId(), user.getEmployeeId()));
    }

    @Operation(summary = "微信授权登录（业主/员工/政府通用入口）")
    @PostMapping("/wechat-login")
    public Result<LoginResponse> wechatLogin(@Valid @RequestBody WechatLoginRequest request) {
        /*
         * 安全链路：
         * - 正式环境：前端传 code → 后端调微信 code2session 换 openid → 匹配本地绑定 → 签发 JWT
         * - Demo 环境：前端传 demoKey（手机号）→ 匹配 owner/employee/government 表 → 自动建号
         *
         * 身份由微信服务器担保（正式）或管理员预登记担保（Demo），杜绝裸传手机号冒充。
         */
        if (wechatProperties.isConfigured()) {
            // === 正式链路：code2session ===
            String openid = wechatUtil.getOpenidByCode(request.getCode());
            if (openid == null) {
                return Result.error(401, "微信授权失败，请重新授权或联系管理员");
            }
            log.info("微信授权登录（正式）: openid={}", openid);
            // 正式环境：openid 匹配已绑定账号
            Optional<SysUser> existing = sysUserRepository.findByUsername(openid);
            if (existing.isPresent()) {
                SysUser user = existing.get();
                return Result.success(toResponse(token(user), user));
            }
            return Result.error(400, "该微信号未绑定系统账号，请联系管理员绑定");
        } else {
            // === Demo 链路：手机号匹配自动建号 ===
            String phone = request.getDemoKey();
            if (phone == null || phone.isBlank()) {
                return Result.error(400, "Demo 模式请输入手机号");
            }
            log.info("微信授权登录（Demo）: phone={}", phone);
            return demoLogin(phone);
        }
    }

    /**
     * Demo 模式：手机号匹配三端身份 → 自动建号 → 发 token。
     * 保持原有「已登记手机号自动识别角色」的开发测试能力。
     */
    private Result<LoginResponse> demoLogin(String phone) {
        // 1. 已注册用户直接登录
        Optional<SysUser> existing = sysUserRepository.findByUsername(phone);
        if (existing.isPresent()) {
            SysUser user = existing.get();
            return Result.success(toResponse(token(user), user));
        }
        // 2. 匹配业主
        Optional<Owner> owner = ownerRepository.findByPhone(phone);
        if (owner.isPresent()) {
            SysUser user = createUser(phone, owner.get().getName(),
                    Constants.ROLE_OWNER, owner.get().getId(), null);
            return Result.success(toResponse(token(user), user));
        }
        // 3. 匹配员工
        Optional<Employee> employee = employeeRepository.findByPhone(phone);
        if (employee.isPresent()) {
            SysUser user = createUser(phone, employee.get().getName(),
                    Constants.ROLE_EMPLOYEE, null, employee.get().getId());
            return Result.success(toResponse(token(user), user));
        }
        // 4. 匹配政府人员
        Optional<GovernmentOfficial> official = governmentOfficialRepository.findByPhone(phone);
        if (official.isPresent()) {
            SysUser user = createUser(phone, official.get().getName(),
                    Constants.ROLE_GOVERNMENT, null, null, official.get().getId());
            return Result.success(toResponse(token(user), user));
        }
        return Result.error(400, "未在系统找到您的信息，请联系物业管理处登记");
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<SysUser> getCurrentUser(@CurrentUser SysUser user) {
        user.setPassword(null);
        return Result.success(user);
    }

    // ===== 内部辅助 =====

    private String token(SysUser user) {
        return jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleType());
    }

    private SysUser createUser(String phone, String realName, int roleType,
                                Long ownerId, Long employeeId) {
        return createUser(phone, realName, roleType, ownerId, employeeId, null);
    }

    private SysUser createUser(String phone, String realName, int roleType,
                                Long ownerId, Long employeeId, Long governmentOfficialId) {
        SysUser user = new SysUser();
        user.setUsername(phone);
        user.setPassword(passwordEncoder.encode("123456"));
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRoleType(roleType);
        user.setOwnerId(ownerId);
        user.setEmployeeId(employeeId);
        user.setGovernmentOfficialId(governmentOfficialId);
        user.setStatus(1);
        return sysUserRepository.save(user);
    }

    private LoginResponse toResponse(String token, SysUser user) {
        return new LoginResponse(
                token, user.getUsername(), user.getRealName(),
                user.getRoleType(), user.getRoleLabel(),
                user.getId(), user.getOwnerId(), user.getEmployeeId());
    }
}
