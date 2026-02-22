/*******************************************************************************
 * 물품보유현황 목록 및 상세정보 조회용 SEED 데이터
 *******************************************************************************
 * 취득 1건 (10개 물품)
 * - 5개: 운용부서·운용상태·취득금액·내용연수·비고 모두 동일 (그룹핑 테스트용)
 * - 2개: 불용 상태 (보유현황에 포함됨, 별도 그룹)
 * - 3개: 반납 상태 (보유현황에 포함됨, 별도 그룹)
 *******************************************************************************/
USE usto;

-- 1. 기존 데이터 전체 삭제 (초기화)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `TB_ITEM001M`; -- 취득기본
TRUNCATE TABLE `TB_ITEM002`;  -- 물품대장
TRUNCATE TABLE `TB_ITEM003M`; -- 운용기본
TRUNCATE TABLE `TB_ITEM003D`; -- 운용상세
TRUNCATE TABLE `TB_ITEM004M`; -- 반납기본
TRUNCATE TABLE `TB_ITEM004D`; -- 반납상세
TRUNCATE TABLE `TB_ITEM005M`; -- 불용기본
TRUNCATE TABLE `TB_ITEM005D`; -- 불용상세
TRUNCATE TABLE `TB_ITEM007`;  -- 상태이력
SET FOREIGN_KEY_CHECKS = 1;

-- 2. 공통 환경 변수 설정
SET @ACQ_ID_INV = UNHEX('11111111111111111111111111111111');
SET @ORG_CD_ERICA = '7008277';

-- 3. 취득 기본 (001M) - 승인 완료된 취득 1건 (총 10개 물품)
INSERT INTO `TB_ITEM001M` (
    ACQ_ID, G2B_D_CD, ACQ_AT, ACQ_UPR, DEPT_CD, DRB_YR, ACQ_QTY,
    ACQ_ARRG_TY, APPR_STS, APPR_AT, ORG_CD, APLY_USR_ID, CRE_BY, CRE_AT
)
VALUES (
           @ACQ_ID_INV,
           '25241236',           -- G2B 식별코드
           '2024-01-15',         -- 취득일자
           2000000,              -- 취득단가
           'A350',               -- 취득부서
           '5',                  -- 내용연수
           10,                   -- 취득수량
           'BUY',                -- 취득정리구분
           'APPROVED',           -- 승인상태
           '2024-01-20',         -- 정리일자
           @ORG_CD_ERICA,
           'test-admin',
           'test-admin',
           NOW()
       );

-- 4. 물품 대장 (002) - 10개 물품 생성
INSERT INTO `TB_ITEM002` (
    ITM_NO, ACQ_ID, G2B_D_CD, DEPT_CD, OPER_STS, ACQ_UPR, DRB_YR, RMK,
    ORG_CD, CRE_BY, CRE_AT
)
VALUES
    -- [그룹 1: 5개 물품 - 모든 속성 동일 → 수량 5로 묶여야 함]
    ('M202600001', @ACQ_ID_INV, '25241236', 'A350', 'OPER', 2000000, '5', '정상 사용중', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600002', @ACQ_ID_INV, '25241236', 'A350', 'OPER', 2000000, '5', '정상 사용중', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600003', @ACQ_ID_INV, '25241236', 'A350', 'OPER', 2000000, '5', '정상 사용중', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600004', @ACQ_ID_INV, '25241236', 'A350', 'OPER', 2000000, '5', '정상 사용중', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600005', @ACQ_ID_INV, '25241236', 'A350', 'OPER', 2000000, '5', '정상 사용중', @ORG_CD_ERICA, 'dev-user', NOW()),

    -- [그룹 2: 2개 물품 - 불용 상태 → 보유현황에 포함됨]
    ('M202600006', @ACQ_ID_INV, '25241236', 'NONE', 'DSU', 2000000, '5', '고장으로 불용', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600007', @ACQ_ID_INV, '25241236', 'NONE', 'DSU', 2000000, '5', '고장으로 불용', @ORG_CD_ERICA, 'dev-user', NOW()),

    -- [그룹 3: 3개 물품 - 반납 상태]
    ('M202600008', @ACQ_ID_INV, '25241236', 'NONE', 'RTN', 2000000, '5', '반납 완료', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600009', @ACQ_ID_INV, '25241236', 'NONE', 'RTN', 2000000, '5', '반납 완료', @ORG_CD_ERICA, 'dev-user', NOW()),
    ('M202600010', @ACQ_ID_INV, '25241236', 'NONE', 'RTN', 2000000, '5', '반납 완료', @ORG_CD_ERICA, 'dev-user', NOW());

-- 5. 상태 변경 이력 (007) 기록
-- 모든 물품 최초 취득 이력 (1~10번)
INSERT INTO `TB_ITEM007` (
    ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN,
    REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT
)
SELECT
    UNHEX(REPLACE(UUID(), '-', '')),
    ITM_NO,
    NULL,
    'OPER',
    '취득 승인',
    'admin',
    '2024-01-20',
    'admin',
    '2024-01-20',
    @ORG_CD_ERICA,
    'system',
    NOW()
FROM `TB_ITEM002`;

-- 8~10번 물품 반납 이력 (OPER -> RTN)
INSERT INTO `TB_ITEM007` (
    ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN,
    REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT
)
SELECT
    UNHEX(REPLACE(UUID(), '-', '')),
    ITM_NO,
    'OPER',
    'RTN',
    '사업종료',
    'dev-user',
    '2024-02-01',
    'admin',
    '2024-02-02',
    @ORG_CD_ERICA,
    'system',
    NOW()
FROM `TB_ITEM002`
WHERE ITM_NO IN ('M202600008', 'M202600009', 'M202600010');

-- 6~7번 물품 불용 처리 이력 (OPER -> DSU)
INSERT INTO `TB_ITEM007` (
    ITEM_HIS_ID, ITM_NO, PREV_STS, NEW_STS, CHG_RSN,
    REQ_USR_ID, REQ_AT, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY, CRE_AT
)
SELECT
    UNHEX(REPLACE(UUID(), '-', '')),
    ITM_NO,
    'OPER',
    'DSU',
    '구형화',
    'dev-user',
    '2024-03-01',
    'admin',
    '2024-03-02',
    @ORG_CD_ERICA,
    'system',
    NOW()
FROM `TB_ITEM002`
WHERE ITM_NO IN ('M202600006', 'M202600007');

/*******************************************************************************
 * 예상 조회 결과
 *******************************************************************************
 *
 * [물품보유현황 목록 조회 시] - 총 3개 행
 * 1행: 취득일 2024-01-15 | 부서: A350 | 상태: 운용 | 금액: 2000000 | 연수: 5년 | 비고: 정상 사용중 | 수량: 5대
 * 2행: 취득일 2024-01-15 | 부서: 없음 | 상태: 불용 | 금액: 2000000 | 연수: 5년 | 비고: 고장으로 불용 | 수량: 2대
 * 3행: 취득일 2024-01-15 | 부서: 없음 | 상태: 반납 | 금액: 2000000 | 연수: 5년 | 비고: 반납 완료 | 수량: 3대
 *
 * [1행 클릭 시 상세 조회]
 * - itmNos: ["M202600001", "M202600002", "M202600003", "M202600004", "M202600005"]
 * - qty: 5
 * - operSts: 운용
 *
 * [2행 클릭 시 상세 조회]
 * - itmNos: ["M202600006", "M202600007"]
 * - qty: 2
 * - operSts: 불용
 *
 *******************************************************************************/