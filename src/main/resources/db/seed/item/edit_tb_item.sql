-- ** 물품취득대장목록 필터링 조회 기능 테스트용 시드 **

-- 1. 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM001M`;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 더미 데이터 삽입
INSERT INTO `TB_ITEM001M`
(G2B_D_CD, ACQ_AT, ACQ_UPR, DEPT_CD, OPER_STS, DRB_YR, ACQ_QTY, ARRG_TY, APPR_STS, RMK, APLY_USR_ID, ORG_CD, CRE_BY, CRE_AT, DEL_YN)
VALUES
-- [ERICA 캠퍼스 데이터] -> 에리카캠퍼스 소속인 사용자는 이 데이터만 보여짐
-- 1. 시설팀: 서버 구매 (대기)
('43211501', '2026-01-15', 15876000, 'ADM_FAC', 'ACQ', '5', 10, 'BUY', 'WAIT', '중앙 서버실 장비 교체', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 2. 관재팀: 방화장갑 기증 (확정)
('46181504', '2026-01-10', 204000, 'ADM_ASSET', 'OPER', '3', 50, 'DONATE', 'APPROVED', '소방본부 기증품', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 3. 소프트웨어융합대학RC: 소프트웨어 제작 (대기)
('43232005', '2026-01-18', 26400000, 'COL_SW_RC', 'ACQ', '5', 1, 'MAKE', 'WAIT', '학생 관리 시스템 v2.0', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 4. 캠퍼스안전팀: 보안 장비 (반려)
('43211501', '2026-01-05', 5000000, 'ADM_SAFE', 'ACQ', '5', 2, 'BUY', 'REJECTED', '예산 초과로 인한 반려 처리', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'N'),

-- 5. 총무인사팀: 사무용 비품 (삭제됨 - 목록에 안나와야 함)
('43211501', '2026-01-01', 120000, 'ADM_HR', 'ACQ', '5', 5, 'BUY', 'WAIT', '잘못 입력된 데이터', 'dev-user', 'HANYANG_ERICA', 'dev-user', NOW(), 'Y'),


-- [SEOUL 캠퍼스 데이터] -> 서울캠퍼스 소속인 사용자는 이 데이터만 보여짐
-- 6. 학술기획운영팀: 도서관 서버 (대기)
('43211501', '2026-01-17', 12000000, 'LIB_PLAN', 'ACQ', '10', 2, 'BUY', 'WAIT', '학술정보관 메인 서버', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 7. 연구정보팀: 고사양 워크스테이션 (확정)
('43211501', '2026-01-12', 4500000, 'LIB_RESEARCH', 'OPER', '5', 3, 'BUY', 'APPROVED', 'AI 연구용 워크스테이션', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 8. 시설팀: 현장 장비 (대기)
('46181504', '2026-01-18', 15000, 'ADM_FAC', 'ACQ', '2', 200, 'BUY', 'WAIT', '현장 작업용 소모품', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N'),

-- 9. 관재팀: 사무용 PC (대기)
('43211501', '2026-01-18', 1100000, 'ADM_ASSET', 'OPER', '5', 30, 'BUY', 'WAIT', '신규 임용 교직원용', 'dev-user', 'HANYANG_SEOUL', 'dev-user', NOW(), 'N');


-- 3. 확정(APPROVED) 데이터에 승인 정보 업데이트
UPDATE `TB_ITEM001M`
SET APPR_USR_ID = 'admin_user',
    APPR_AT = '2026-01-13'
WHERE APPR_STS = 'APPROVED';