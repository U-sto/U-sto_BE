/*******************************************************************************
 * 반납 및 불용 신청 테스트용 데이터 (SEED DATA)
 *******************************************************************************/
USE usto;

-- 1. 기존 데이터 전체 삭제 (초기화)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM002M`; -- 물품대장기본
TRUNCATE TABLE `TB_ITEM002D`; -- 물품대장상세
TRUNCATE TABLE `TB_ITEM006M`; -- 물품상태이력
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 공통 환경 변수 설정
SET @ACQ_ID_TEST = UNHEX('77777777777777777777777777777777');
SET @ORG_CD_ERICA = '7008277';

-- 3. 대장 기본 (002M) - 총 6개 수량
INSERT INTO `TB_ITEM002M` (ACQ_ID, G2B_D_CD, QTY, ACQ_AT, ARRG_AT, ORG_CD, ACQ_ARRG_TY, CRE_BY, CRE_AT)
VALUES (@ACQ_ID_TEST, '25241236', 6, '2026-01-15', '2026-01-16', @ORG_CD_ERICA, 'BUY', 'test-admin', NOW());

-- 4. 대장 상세 (002D)
INSERT INTO `TB_ITEM002D` (ITM_NO, ACQ_ID, G2B_D_CD, DEPT_CD, OPER_STS, ACQ_UPR, DRB_YR, RMK, ORG_CD, CRE_BY, CRE_AT)
VALUES
    -- [반납신청 테스트용: 현재 운용 중]
    ('M202600001', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600002', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600003', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),

    -- [불용신청 테스트용: 현재 반납 완료 상태]
    ('M202600004', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600005', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600006', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW());

-- 5. 상태 변경 이력 (006M) 기록
-- [STEP 1] 1~6번 물품 모두: 최초 취득 등록 이력 (ACQ -> OPER)
INSERT INTO `TB_ITEM006M` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600001', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600002', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600003', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600004', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600005', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600006', 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW());

-- [STEP 2] 4~6번 물품만 추가: 반납 완료 이력 (OPER -> RTN)
INSERT INTO `TB_ITEM006M` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600004', 'OPER', 'RTN', '불용 처리를 위한 사전 반납', 'test-user', '2026-01-20', 'admin', '2026-01-21', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600005', 'OPER', 'RTN', '불용 처리를 위한 사전 반납', 'test-user', '2026-01-20', 'admin', '2026-01-21', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600006', 'OPER', 'RTN', '불용 처리를 위한 사전 반납', 'test-user', '2026-01-20', 'admin', '2026-01-21', @ORG_CD_ERICA, 'system', NOW());