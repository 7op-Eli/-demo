package com.property.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.property.common.Constants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@Entity
@Table(name = "sys_user")
public class SysUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String avatar;

    @Column(name = "role_type", nullable = false)
    private Integer roleType;

    @Column(nullable = false)
    private Integer status = 1;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ====== UserDetails 接口实现 ====== */

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role;
        switch (roleType) {
            case Constants.ROLE_ADMIN:
                role = "ROLE_ADMIN";
                break;
            case Constants.ROLE_EMPLOYEE:
                role = "ROLE_EMPLOYEE";
                break;
            default:
                role = "ROLE_OWNER";
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return status == 1;
    }

    /** 前端角色标签 */
    public String getRoleLabel() {
        switch (roleType) {
            case Constants.ROLE_ADMIN: return "管理员";
            case Constants.ROLE_EMPLOYEE: return "员工";
            case Constants.ROLE_OWNER: return "业主";
            default: return "未知";
        }
    }
}
