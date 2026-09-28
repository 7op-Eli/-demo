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
    private final com.property.repository.GovernmentOfficialRepository governmentOfficialRepository;
    private final com.property.repository.GovernmentFeedbackRepository governmentFeedbackRepository;
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

    @Operation(summary = "更新员工")
    @PutMapping("/employees/{id}")
    public Result<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("员工不存在"));
        existing.setName(employee.getName());
        existing.setPhone(employee.getPhone());
        existing.setPosition(employee.getPosition());
        existing.setDepartment(employee.getDepartment());
        existing.setEntryDate(employee.getEntryDate());
        existing.setStatus(employee.getStatus());
        return Result.success(employeeRepository.save(existing));
    }

    @Operation(summary = "删除员工")
    @DeleteMapping("/employees/{id}")
    public Result<?> deleteEmployee(@PathVariable Long id) {
        Employee emp = employeeRepository.findById(id).orElse(null);
        if (emp != null) {
            userRepository.findByUsername(emp.getPhone()).ifPresent(u -> {
                u.setStatus(0);
                userRepository.save(u);
            });
        }
        employeeRepository.deleteById(id);
        return Result.success();
    }

    // ========== 政府人员管理 ==========

    @Operation(summary = "政府人员列表（分页）")
    @GetMapping("/government-officials")
    public Result<PageResult<GovernmentOfficial>> getGovernmentOfficials(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GovernmentOfficial> result = governmentOfficialRepository.findAll(PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "新增政府人员")
    @PostMapping("/government-officials")
    public Result<GovernmentOfficial> createGovernmentOfficial(@RequestBody GovernmentOfficial official) {
        GovernmentOfficial saved = governmentOfficialRepository.save(official);
        // 自动创建登录账号
        if (!userRepository.existsByUsername(official.getPhone())) {
            SysUser user = new SysUser();
            user.setUsername(official.getPhone());
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRealName(official.getName());
            user.setPhone(official.getPhone());
            user.setRoleType(com.property.common.Constants.ROLE_GOVERNMENT);
            user.setGovernmentOfficialId(saved.getId());
            userRepository.save(user);
        }
        return Result.success(saved);
    }

    @Operation(summary = "更新政府人员")
    @PutMapping("/government-officials/{id}")
    public Result<GovernmentOfficial> updateGovernmentOfficial(@PathVariable Long id,
                                                                @RequestBody GovernmentOfficial official) {
        GovernmentOfficial existing = governmentOfficialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("人员不存在"));
        existing.setName(official.getName());
        existing.setPhone(official.getPhone());
        existing.setDepartment(official.getDepartment());
        existing.setPosition(official.getPosition());
        existing.setStatus(official.getStatus());
        return Result.success(governmentOfficialRepository.save(existing));
    }

    @Operation(summary = "删除政府人员")
    @DeleteMapping("/government-officials/{id}")
    public Result<?> deleteGovernmentOfficial(@PathVariable Long id) {
        GovernmentOfficial official = governmentOfficialRepository.findById(id).orElse(null);
        if (official != null) {
            userRepository.findByUsername(official.getPhone()).ifPresent(u -> {
                u.setStatus(0);
                userRepository.save(u);
            });
        }
        governmentOfficialRepository.deleteById(id);
        return Result.success();
    }

    // ========== 政府反馈管理 ==========

    @Operation(summary = "政府反馈列表（分页）")
    @GetMapping("/government-feedbacks")
    public Result<PageResult<GovernmentFeedback>> getGovernmentFeedbacks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<GovernmentFeedback> result = governmentFeedbackRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(page - 1, size));
        return Result.success(PageResult.of(result, result.getContent()));
    }

    @Operation(summary = "回复政府反馈")
    @PutMapping("/government-feedbacks/{id}/reply")
    public Result<GovernmentFeedback> replyGovernmentFeedback(@PathVariable Long id,
                                                               @RequestBody java.util.Map<String, String> body,
                                                               @CurrentUser SysUser user) {
        GovernmentFeedback feedback = governmentFeedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("反馈不存在"));
        feedback.setReply(body.get("reply"));
        feedback.setRepliedBy(user.getEmployeeId());
        feedback.setRepliedAt(java.time.LocalDateTime.now());
        feedback.setStatus(2); // 已回复
        return Result.success(governmentFeedbackRepository.save(feedback));
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
