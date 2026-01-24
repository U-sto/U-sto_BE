/*******************************************************************************
 * 물품 취득 등록 목록 조회 테스트용 데이터 (SEED DATA)
 *******************************************************************************
 * - 대상 테이블: TB_ITEM001M (물품취득기본)
 * - 주요 내용:
 * 1. WAIT, REQUEST, APPROVED, REJECTED 상태별 시나리오 구성
 * 2. ERICA(7008277) / SEOUL(7002282) 캠퍼스별 데이터 분리
 * 3. Soft Delete(DEL_YN='Y') 처리된 데이터 포함
 * 4. PK: UUID (BINARY 16) - UNHEX 변환 포맷 적용
 *******************************************************************************/
USE usto;
-- 1. 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM001M`;
SET FOREIGN_KEY_CHECKS = 1;

USE usto;
SELECT * FROM TB_ITEM001M;

-- 2. 더미 데이터 삽입 (ApprStatus: WAIT, REQUEST, APPROVED, REJECTED 케이스 포함)
INSERT INTO `TB_ITEM001M`
(ACQ_ID, G2B_D_CD, ACQ_AT, ACQ_UPR, DEPT_CD, OPER_STS, DRB_YR, ACQ_QTY, ARRG_TY, APPR_STS, RMK, APLY_USR_ID, ORG_CD, CRE_BY, CRE_AT, DEL_YN)
VALUES
-- [ERICA 캠퍼스 데이터]
-- 1. 시설팀: 서버 구매 (WAIT - 수정/삭제/승인요청 가능해야 함)
(UNHEX(REPLACE(UUID(), '-', '')), '22766414', '2026-01-15', 15876000, 'A351', 'ACQ', '5', 10, 'BUY', 'WAIT', '중앙 서버실 장비 교체', 'dev-user', '7008277', 'dev-user', '2026-01-15 18:00:03', 'N'),

-- 2. 관재팀: 장갑 기증 (APPROVED - 수정/삭제/승인취소 불가)
(UNHEX(REPLACE(UUID(), '-', '')), '25241236', '2026-01-10', 255, 'A350', 'OPER', '3', 50, 'DONATE', 'APPROVED', '소방본부 기증품', 'dev-user', '7008277', 'dev-user', '2026-01-10 18:00:03', 'N'),

-- 3. SW융합대학RC: 윈도우 운영체제 구매(REQUEST - 수정/삭제 불가, 승인취소 가능해야 함)
(UNHEX(REPLACE(UUID(), '-', '')), '24445466', '2026-01-18', 225000, 'C354', 'ACQ', '5', 1, 'BUY', 'REQUEST', '윈도우 11', 'dev-user', '7008277', 'dev-user', '2026-01-18 18:00:03', 'N'),

-- 4. 캠퍼스안전팀: 보안 장비 (REJECTED - 수정 후 재요청 가능해야 함)
(UNHEX(REPLACE(UUID(), '-', '')), '20820837', '2026-01-05', 6506200, 'A349', 'ACQ', '5', 2, 'BUY', 'REJECTED', '예산 초과로 인한 반려 데이터', 'dev-user', '7008277', 'dev-user', '2026-01-07 18:00:03', 'N'),

-- 5. 총무인사팀: 삭제된 데이터 (목록 조회에서 절대 나오면 안 됨)
(UNHEX(REPLACE(UUID(), '-', '')), '25241236', '2026-01-01', 255, 'A348', 'ACQ', '5', 5, 'BUY', 'WAIT', '논리 삭제 테스트용', 'dev-user', '7008277', 'dev-user', '2026-01-02 18:00:03', 'Y'),


-- [SEOUL 캠퍼스 데이터]
-- 6. 학술기획운영팀: 서버 (WAIT)
(UNHEX(REPLACE(UUID(), '-', '')), '22766414', '2026-01-17', 15876000, 'B102', 'ACQ', '10', 2, 'BUY', 'WAIT', '학술정보관 메인 서버', 'dev-user', '7002282', 'dev-user', '2026-01-17 18:00:03', 'N'),

-- 7. 연구정보팀: 워크스테이션 (APPROVED)
(UNHEX(REPLACE(UUID(), '-', '')), '24120278', '2026-01-12', 1320000, 'B103', 'OPER', '5', 3, 'BUY', 'APPROVED', '연구용 장비', 'dev-user', '7002282', 'dev-user', '2026-01-12 18:00:03', 'N'),

-- 8. 시설팀: 청소 도구 (REQUEST)
(UNHEX(REPLACE(UUID(), '-', '')), '21067750', '2026-01-18', 1317000, 'A158', 'ACQ', '2', 200, 'BUY', 'REQUEST', '청소 도구함', 'dev-user', '7002282', 'dev-user', '2026-01-18 18:00:03', 'N'),

-- 9. 관재팀: PC (WAIT)
(UNHEX(REPLACE(UUID(), '-', '')), '24733011', '2026-01-18', 1625000, 'A157', 'OPER', '5', 30, 'BUY', 'WAIT', '신규 교직원용 PC', 'dev-user', '7002282', 'dev-user', '2026-01-19 18:00:03', 'N');


-- 3. APPROVED 데이터에 승인 정보 강제 업데이트 (테스트 편의용)
UPDATE `TB_ITEM001M`
SET APPR_USR_ID = 'dev_user',
    APPR_AT = '2026-01-13'
WHERE APPR_STS = 'APPROVED';