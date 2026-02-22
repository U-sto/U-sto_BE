/*******************************************************************************
 * 운용 대장 및 상태 이력 테스트용 데이터 (SEED DATA)
 *******************************************************************************
 * 1. TB_ITEM001M (물품취득기본) - 취득 승인 및 정리 정보 (구 002M 역할 포함)
 * 2. TB_ITEM002  (물품대장)    - 개별 물품 상세 (구 002D)
 * 3. TB_ITEM007  (물품상태이력) - 상태 변경 이력 (구 006M)
 *******************************************************************************/
USE usto;

-- 1. 기존 데이터 초기화
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM001M`;
TRUNCATE TABLE `TB_ITEM002`;
TRUNCATE TABLE `TB_ITEM007`;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 임의로 취득 ID 설정
SET @ACQ_ID_ERICA = UNHEX('11111111111111111111111111111111'); -- ERICA 기증 건
SET @ACQ_ID_SEOUL = UNHEX('22222222222222222222222222222222'); -- SEOUL 구매 건

-- -----------------------------------------------------------------------------
-- [ERICA 캠퍼스] 관재팀: 장갑 기증 건 (50개 중 일부 샘플링)
-- -----------------------------------------------------------------------------

-- 취득 기본 (001M) - 대장의 부모 정보
INSERT INTO `TB_ITEM001M` (ACQ_ID, G2B_D_CD, ACQ_QTY, ACQ_AT, APPR_AT, ORG_CD, ACQ_ARRG_TY, APLY_USR_ID, ACQ_UPR, DEPT_CD, DRB_YR, APPR_STS, CRE_BY)
VALUES (@ACQ_ID_ERICA, '25241236', 50, '2026-01-10', '2026-01-13', '7008277', 'DONATE', 'dev-user', 255, 'A350', '3', 'APPROVED', 'dev-user');

-- 대장 상세 (002D) - 50개 중 3개만 샘플링
INSERT INTO `TB_ITEM002` (ITM_NO, ACQ_ID, G2B_D_CD, DEPT_CD, OPER_STS, ACQ_UPR, DRB_YR, RMK, ORG_CD, CRE_BY)
VALUES
    ('M202600001', @ACQ_ID_ERICA, '25241236', 'A350', 'OPER', 255, '3', '소방본부 기증품-1', '7008277', 'dev-user'),
    ('M202600002', @ACQ_ID_ERICA, '25241236', 'A350', 'OPER', 255, '3', '소방본부 기증품-2', '7008277', 'dev-user'),
    ('M202600003', @ACQ_ID_ERICA, '25241236', 'NONE', 'RTN',  255, '3', '기증품', '7008277', 'dev-user');

-- -----------------------------------------------------------------------------
-- [SEOUL 캠퍼스] 연구정보팀: 워크스테이션 구매 건
-- -----------------------------------------------------------------------------

-- 취득 기본 (001M)
INSERT INTO `TB_ITEM001M` (ACQ_ID, G2B_D_CD, ACQ_QTY, ACQ_AT, APPR_AT, ORG_CD, ACQ_ARRG_TY, APLY_USR_ID, ACQ_UPR, DEPT_CD, DRB_YR, APPR_STS, CRE_BY)
VALUES (@ACQ_ID_SEOUL, '24120278', 3, '2026-01-12', '2026-01-13', '7002282', 'BUY', 'dev-user', 1320000, 'B103', '5', 'APPROVED', 'dev-user');

-- 물품 대장 (002)
INSERT INTO `TB_ITEM002` (ITM_NO, ACQ_ID, G2B_D_CD, DEPT_CD, OPER_STS, ACQ_UPR, DRB_YR, RMK, ORG_CD, CRE_BY)
VALUES
    ('M202600001', @ACQ_ID_SEOUL, '24120278', 'B103', 'OPER', 1320000, '5', '연구용 장비-A', '7002282', 'dev-user'),
    ('M202600002', @ACQ_ID_SEOUL, '24120278', 'B103', 'OPER', 1320000, '5', '연구용 장비-B', '7002282', 'dev-user'),
    ('M202600003', @ACQ_ID_SEOUL, '24120278', 'NONE', 'DSU',  1320000, '5', '불용테스트용', '7002282', 'dev-user');


-- -----------------------------------------------------------------------------
-- [상태 변경 이력] TB_ITEM007 (구 006M)
-- -----------------------------------------------------------------------------

INSERT INTO `TB_ITEM007` (ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN, REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY)
VALUES
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600001', NULL, 'OPER', '최초 취득 등록', 'dev-user', '2026-01-13', 'admin', '2026-01-13', '7008277', 'system'),
    (UNHEX(REPLACE(UUID(), '-', '')), 'M202600001', 'OPER', 'RTN', '프로젝트 종료로 인한 반납', 'dev-user', '2026-01-20', 'admin', '2026-01-21', '7008277', 'system');