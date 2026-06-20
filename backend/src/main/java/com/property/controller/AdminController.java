package com.property.controller;

import com.property.common.PageResult;
import com.property.common.Result;
import com.property.entity.*;
import com.property.security.CurrentUser;
import com.property.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "系统管理（管理员端）")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final OwnerService ownerService;
    private final BuildingRoomService buildingRoomService;
    private final com.property.repository.SysUserRepository userRepository;
    private final com.property.repository.EmployeeRepository employeeRepository;
    private final com.property.repository.RepairOrderRepository repairOrderRepository;
    private final com.property.repository.VisitorRepository visitorRepository;
    private final com.property.repository.OwnerRepository ownerRepository;
    private final com.property.repository.BuildingRepository buildingRepository;
    private final PasswordEncoder passwordEncoder;

    // ========== 业主管理 ==========

    @Operation(summary = "业主列表（分页）")
    @GetMapping("/owners")
    public Result<PageResult<Owner>> getOwners(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Owner> result = ownerRepository.findAll(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "业主详情")
    @GetMapping("/owners/{id}")
    public Result<Owner> getOwner(@PathVariable Long id) {
        return Result.success(ownerService.findById(id));
    }

    @Operation(summary = "新增业主")
    @PostMapping("/owners")
    public Result<Owner> createOwner(@RequestBody Owner owner) {
        Owner saved = ownerService.create(owner);
        // 自动创建系统登录账号
        // TODO: security — default password "123456" should be randomized and
        // communicated out-of-band (SMS/email). Force password change on first login.
        if (!userRepository.existsByUsername(owner.getPhone())) {
            SysUser user = new SysUser();
            user.setUsername(owner.getPhone());
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName(owner.getName());
            user.setPhone(owner.getPhone());
            user.setRoleType(com.property.common.Constants.ROLE_OWNER);
            user.setOwnerId(saved.getId());
            userRepository.save(user);
        }
        return Result.success(saved);
    }

    @Operation(summary = "更新业主信息")
    @PutMapping("/owners/{id}")
    public Result<Owner> updateOwner(@PathVariable Long id, @RequestBody Owner owner) {
        return Result.success(ownerService.update(id, owner));
    }

    @Operation(summary = "删除业主")
    @DeleteMapping("/owners/{id}")
    public Result<?> deleteOwner(@PathVariable Long id) {
        ownerService.delete(id);
        return Result.success();
    }

    // ========== 楼栋管理 ==========

    @Operation(summary = "楼栋列表（分页）")
    @GetMapping("/buildings")
    public Result<PageResult<Building>> getBuildings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Building> result = buildingRepository.findAll(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "新建楼栋")
    @PostMapping("/buildings")
    public Result<Building> createBuilding(@RequestBody Building building) {
        return Result.success(buildingRoomService.createBuilding(building));
    }

    @Operation(summary = "更新楼栋")
    @PutMapping("/buildings/{id}")
    public Result<Building> updateBuilding(@PathVariable Long id, @RequestBody Building building) {
        return Result.success(buildingRoomService.updateBuilding(id, building));
    }

    // ========== 房间管理 ==========

    @Operation(summary = "楼栋下的房间列表")
    @GetMapping("/buildings/{buildingId}/rooms")
    public Result<List<Room>> getRooms(@PathVariable Long buildingId) {
        return Result.success(buildingRoomService.getRoomsByBuilding(buildingId));
    }

    @Operation(summary = "新建房间")
    @PostMapping("/rooms")
    public Result<Room> createRoom(@RequestBody Room room) {
        return Result.success(buildingRoomService.createRoom(room));
    }

    @Operation(summary = "更新房间")
    @PutMapping("/rooms/{id}")
    public Result<Room> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return Result.success(buildingRoomService.updateRoom(id, room));
    }

    // ========== 员工管理 ==========

    @Operation(summary = "员工列表（分页）")
    @GetMapping("/employees")
    public Result<PageResult<Employee>> getEmployees(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Employee> result = employeeRepository.findAll(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "新增员工")
    @PostMapping("/employees")
    public Result<Employee> createEmployee(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        // TODO: security — same default-password issue as createOwner above.
        if (!userRepository.existsByUsername(employee.getPhone())) {
            SysUser user = new SysUser();
            user.setUsername(employee.getPhone());
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName(employee.getName());
            user.setPhone(employee.getPhone());
            user.setRoleType(com.property.common.Constants.ROLE_EMPLOYEE);
            user.setEmployeeId(saved.getId());
            userRepository.save(user);
        }
        return Result.success(saved);
    }

    // ========== 仪表盘统计 ==========

    @Operation(summary = "首页统计概览")
    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        return Result.success(java.util.Map.of(
                "totalOwners", ownerService.findAll().size(),
                "totalEmployees", employeeRepository.count(),
                "pendingRepairs", repairOrderRepository.countByStatus(com.property.common.Constants.REPAIR_SUBMITTED),
                "pendingVisitors", visitorRepository.countByStatus(com.property.common.Constants.VISITOR_PENDING)
        ));
    }
}
