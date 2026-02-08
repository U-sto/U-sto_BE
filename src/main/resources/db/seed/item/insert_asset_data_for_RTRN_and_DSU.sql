/*******************************************************************************
 * 반납, 불용, 처분 신청 테스트용 데이터 (SEED DATA)
 *******************************************************************************/
USE usto;

-- 1. 기존 데이터 전체 삭제 (초기화)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM002M`; -- 물품대장기본
TRUNCATE TABLE `TB_ITEM002D`; -- 물품대장상세
TRUNCATE TABLE `TB_ITEM004M`; -- 물품불용기본 (추가)
TRUNCATE TABLE `TB_ITEM004D`; -- 물품불용상세 (추가)
TRUNCATE TABLE `TB_ITEM006M`; -- 물품상태이력
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 공통 환경 변수 설정
SET @ACQ_ID_TEST = UNHEX('77777777777777777777777777777777');
SET @DSU_M_ID_TEST = UNHEX('88888888888888888888888888888888'); -- 승인된 불용 마스터 ID
SET @ORG_CD_ERICA = '7008277';

-- 3. 대장 기본 (002M) - 총 9개 수량 (반납 3, 불용 3, 처분 3)
INSERT INTO `TB_ITEM002M` (ACQ_ID, G2B_D_CD, QTY, ACQ_AT, ARRG_AT, ORG_CD, ACQ_ARRG_TY, CRE_BY, CRE_AT)
VALUES (@ACQ_ID_TEST, '25241236', 9, '2026-01-15', '2026-01-16', @ORG_CD_ERICA, 'BUY', 'test-admin', NOW());

-- 4. 대장 상세 (002D)
INSERT INTO `TB_ITEM002D` (ITM_NO, ACQ_ID, G2B_D_CD, DEPT_CD, OPER_STS, ACQ_UPR, DRB_YR, RMK, ORG_CD, CRE_BY, CRE_AT)
VALUES
    -- [반납신청 테스트용: 운용 중]
    ('M202600001', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600002', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600003', @ACQ_ID_TEST, '25241236', 'A350', 'OPER', 500000, '5', '반납테스트용(운용)', @ORG_CD_ERICA, 'dev-user', NOW()),

    -- [불용신청 테스트용: 반납 완료]
    ('M202600004', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600005', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600006', @ACQ_ID_TEST, '25241236', 'NONE', 'RTN',  800000, '5', '불용테스트용(반납)', @ORG_CD_ERICA, 'dev-user', NOW()),

    -- [처분신청 테스트용: 불용 승인 완료]
    ('M202600007', @ACQ_ID_TEST, '25241236', 'NONE', 'DSU', 1200000, '5', '처분테스트용(불용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600008', @ACQ_ID_TEST, '25241236', 'NONE', 'DSU', 1200000, '5', '처분테스트용(불용)', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600009', @ACQ_ID_TEST, '25241236', 'NONE', 'DSU', 1200000, '5', '처분테스트용(불용)', @ORG_CD_ERICA, 'dev-user', NOW());

-- 5. 불용 승인 데이터 생성 (처분 신청 로직에서 조회하기 위함)
-- 7~9번 물품에 대한 승인된 불용 신청서
INSERT INTO `TB_ITEM004M` (DSU_M_ID, APLY_USR_ID, APLY_AT, ITEM_STS, CHG_RSN, DSU_APPR_AT, APPR_STS, ORG_CD, CRE_BY, CRE_AT)
VALUES (@DSU_M_ID_TEST, 'dev-user', '2026-02-01', 'USED', 'LIFE_EXPIRED', '2026-02-02', 'APPROVED', @ORG_CD_ERICA, 'system', NOW());

INSERT INTO `TB_ITEM004D` (DSU_D_ID, DSU_M_ID, ITM_NO, DEPT_CD, ORG_CD, CRE_BY, CRE_AT)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), @DSU_M_ID_TEST, 'M202600007', 'NONE', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), @DSU_M_ID_TEST, 'M202600008', 'NONE', @ORG_CD_ERICA, 'system', NOW()),
    (UNHEX(REPLACE(UUID(), '-', '')), @DSU_M_ID_TEST, 'M202600009', 'NONE', @ORG_CD_ERICA, 'system', NOW());

-- 6. 상태 변경 이력 (006M) 기록
-- 모든 물품 최초 취득 (1~9번)
INSERT INTO `TB_ITEM006M` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT)
SELECT UNHEX(REPLACE(UUID(), '-', '')), ITM_NO, 'ACQ', 'OPER', '자산 신규 등록', 'admin', '2026-01-16', 'admin', '2026-01-16', @ORG_CD_ERICA, 'system', NOW()
FROM `TB_ITEM002D`;

-- 4~9번 물품 반납 이력 (OPER -> RTN)
INSERT INTO `TB_ITEM006M` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT)
SELECT UNHEX(REPLACE(UUID(), '-', '')), ITM_NO, 'OPER', 'RTN', '불용 전 사전 반납', 'dev-user', '2026-01-20', 'admin', '2026-01-21', @ORG_CD_ERICA, 'system', NOW()
FROM `TB_ITEM002D` WHERE ITM_NO BETWEEN 'M202600004' AND 'M202600009';

-- 7~9번 물품 불용 승인 이력 (RTN -> DSU)
INSERT INTO `TB_ITEM006M` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT)
SELECT UNHEX(REPLACE(UUID(), '-', '')), ITM_NO, 'RTN', 'DSU', '불용 승인 완료', 'dev-user', '2026-02-01', 'admin', '2026-02-02', @ORG_CD_ERICA, 'system', NOW()
FROM `TB_ITEM002D` WHERE ITM_NO BETWEEN 'M202600007' AND 'M202600009';