-- 1. 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM001M`;
SET FOREIGN_KEY_CHECKS = 1;

USE usto;
SELECT * FROM TB_ITEM001M;

-- 2. 더미 데이터 삽입 (ApprStatus: WAIT, REQUEST, APPROVED, REJECTED 케이스 포함)
INSERT INTO `TB_ITEM001M`
(G2B_D_CD, ACQ_AT, ACQ_UPR, DEPT_CD, OPER_STS, DRB_YR, ACQ_QTY, ARRG_TY, APPR_STS, RMK, APLY_USR_ID, ORG_CD, CRE_BY, CRE_AT, DEL_YN)
VALUES
-- [ERICA 캠퍼스 데이터]
-- 1. 시설팀: 서버 구매 (WAIT - 수정/삭제/승인요청 가능해야 함)
('22766414', '2026-01-15', 15876000, 'ADM_FAC', 'ACQ', '5', 10, 'BUY', 'WAIT', '중앙 서버실 장비 교체(대기)', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 2. 관재팀: 방화장갑 기증 (APPROVED - 수정/삭제/승인취소 불가)
('25241236', '2026-01-10', 204000, 'ADM_ASSET', 'OPER', '3', 50, 'DONATE', 'APPROVED', '소방본부 기증품(확정)', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 3. SW융합대학RC: 시스템 개발 (REQUEST - 수정/삭제 불가, 승인취소 가능해야 함)
('24445466', '2026-01-18', 26400000, 'COL_SW_RC', 'ACQ', '5', 1, 'MAKE', 'REQUEST', '학생 관리 시스템 v2.0(요청중)', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 4. 캠퍼스안전팀: 보안 장비 (REJECTED - 수정 후 재요청 가능해야 함)
('25784402', '2026-01-05', 5000000, 'ADM_SAFE', 'ACQ', '5', 2, 'BUY', 'REJECTED', '예산 초과로 인한 반려 데이터', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 5. 총무인사팀: 삭제된 데이터 (목록 조회에서 절대 나오면 안 됨)
('25241236', '2026-01-01', 120000, 'ADM_HR', 'ACQ', '5', 5, 'BUY', 'WAIT', '논리 삭제 테스트용', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'Y'),


-- [SEOUL 캠퍼스 데이터]
-- 6. 학술기획운영팀: 서버 (WAIT)
('22766414', '2026-01-17', 12000000, 'LIB_PLAN', 'ACQ', '10', 2, 'BUY', 'WAIT', '학술정보관 메인 서버', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 7. 연구정보팀: 워크스테이션 (APPROVED)
('25784402', '2026-01-12', 4500000, 'LIB_RESEARCH', 'OPER', '5', 3, 'BUY', 'APPROVED', 'AI 연구용 장비', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 8. 시설팀: 작업 도구 (REQUEST)
('25241236', '2026-01-18', 15000, 'ADM_FAC', 'ACQ', '2', 200, 'BUY', 'REQUEST', '현장 작업용 소모품(요청중)', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 9. 관재팀: PC (WAIT)
('24733011', '2026-01-18', 1100000, 'ADM_ASSET', 'OPER', '5', 30, 'BUY', 'WAIT', '신규 교직원용 PC', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N');


-- 3. APPROVED 데이터에 승인 정보 강제 업데이트 (테스트 편의용)
UPDATE `TB_ITEM001M`
SET APPR_USR_ID = 'manager_user',
    APPR_AT = '2026-01-13'
WHERE APPR_STS = 'APPROVED';