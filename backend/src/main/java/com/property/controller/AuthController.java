package com.property.controller;

import com.property.common.Result;
import com.property.dto.LoginRequest;
import com.property.dto.LoginResponse;
import com.property.entity.SysUser;
import com.property.security.CurrentUser;
import com.property.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SysUser user = (SysUser) auth.getPrincipal();

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRoleType());

        return Result.success(new LoginResponse(
                token, user.getUsername(), user.getRealName(),
                user.getRoleType(), user.getRoleLabel(),
                user.getId(), user.getOwnerId(), user.getEmployeeId()));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<SysUser> getCurrentUser(@CurrentUser SysUser user) {
        user.setPassword(null);
        return Result.success(user);
    }
}
