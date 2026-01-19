package com.usto.api.user.application;

import com.usto.api.organization.infrastructure.entity.OrganizationJpaEntity;
import com.usto.api.organization.infrastructure.repository.OrganizationJpaRepository;
import com.usto.api.user.domain.model.Role;
import com.usto.api.user.domain.model.User;
import com.usto.api.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalApplication {

    private final UserRepository userRepository;
    private final EmailSendApplication emailSendApplication;
    private final OrganizationJpaRepository organizationJpaRepository;

    @Transactional
    public void approveUserWithRole(String usrId, Role assignedRole ,String apprUsrId) {
        User user = userRepository. getByUsrId(usrId);

        // 승인 + 역할 지정
        User approved = user.approve(assignedRole,apprUsrId);

        userRepository. save(approved);

        String orgName = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm) // 여기서 이름을 꺼냄
                .orElse("알 수 없는 조직");

        emailSendApplication.sendApprovalCompleteEmail(approved, orgName,assignedRole);

        log.info("[APPROVAL] 회원 승인 완료 - usrId: {}, role: {}, approver: {}",
                usrId, assignedRole);
    }

    /**
     * 회원 반려
     */

    //이미 승인된 사용자는 반려하면 안됌 -> 회의때 협의 예정
    @Transactional
    public void rejectUser(String usrId,String apprUsrId) {
        User user = userRepository.getByUsrId(usrId);

        User rejected = user.reject(apprUsrId);

        userRepository.save(rejected);

        String orgName = organizationJpaRepository.findByOrgCd(user.getOrgCd())
                .map(OrganizationJpaEntity::getOrgNm) // 여기서 이름을 꺼냄
                .orElse("알 수 없는 조직");

        emailSendApplication.sendApprovalRejectedEmail(rejected,orgName);

        log.info("[APPROVAL] 회원 반려 완료 - usrId: {}, approver: {}", usrId);
    }
}
