package com.usto.api.user.presentation.controller;

import com.usto.api.common.utils.ApiResponse;
import com.usto.api.user.application.ApprovalApplication;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalApplication approvalApplication;

    @GetMapping
    public ApiResponse<?> processApproval(
            @RequestParam String action,
            @RequestParam String userId,
            @RequestParam(required = false) String role
    )
    {
        String approverUsrId = "DEVELOPER";

        if ("approve".equals(action)) {
            if (role == null) {
                return ApiResponse.fail("역할을 지정해주세요.");
            }
            Role assignedRole;
            try {
                assignedRole = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ApiResponse.fail("유효하지 않은 역할입니다.");
            }
            approvalApplication.approveUserWithRole(userId, assignedRole, approverUsrId);
            return ApiResponse.ok("승인이 완료되었습니다.  (역할: " + assignedRole + ")", null);


        } else if ("reject". equals(action)) {
            approvalApplication.rejectUser(userId, approverUsrId);
            return ApiResponse.ok("반려가 완료되었습니다.", null);



        } else {
            return ApiResponse.fail("잘못된 요청입니다.");
        }
    }
}
