package com.property.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private String realName;
    private Integer roleType;
    private String roleLabel;
    private Long userId;
    private Long ownerId;
    private Long employeeId;
}
