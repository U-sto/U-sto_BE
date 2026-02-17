Use usto;

-- 취득 기본 조회
SELECT * FROM TB_ITEM001M;

-- 특정 취득 ID 조회 (바이너리 타입 고려)
SELECT * FROM TB_ITEM001M WHERE ACQ_ID = UNHEX(REPLACE('3c745568-f9cc-11f0-ae8f-8c554a43c9a1', '-', ''));

-- G2B 식별코드 조회
SELECT * FROM TB_G2B001D WHERE G2B_D_CD = '25241236';
SELECT * FROM TB_G2B001M WHERE G2B_M_CD = '53102504';

-- [수정] 통합된 물품대장 조회
SELECT * FROM TB_ITEM002;

SELECT M.DRB_YR FROM TB_G2B001M M
                         JOIN TB_G2B001D D ON M.G2B_M_CD = D.G2B_M_CD
    AND D.G2B_D_CD = '25241236';

-- [수정] 물품대장 삭제 (002D -> 002)
DELETE FROM TB_ITEM002 WHERE DEPT_CD = 'B103' ;

-- 취득 ID 확인용
SELECT HEX(ACQ_ID) AS ACQ_ID_HEX, G2B_D_CD, APPR_STS, ACQ_AT FROM TB_ITEM001M;

-- [수정] 물품 상세 정보 조회 (001M + 002 조인)
SELECT
    d.ITM_NO,
    d.ORG_CD,
    g.G2B_D_NM,
    CONCAT(c.G2B_M_CD, '-', g.G2B_D_CD) AS g2bItemNo,
    d.ACQ_UPR,
    m.ACQ_AT,
    m.APPR_AT AS ARRG_AT, -- 확정일자가 정리일자 역할
    d.OPER_STS,
    d.DRB_YR,
    dept.DEPT_NM,
    m.ACQ_QTY AS QTY,     -- 수량은 001M에서 가져옴
    d.RMK
FROM TB_ITEM002 d
         INNER JOIN TB_ITEM001M m
                    ON m.ACQ_ID = d.ACQ_ID
                        AND m.DEL_YN = 'N'
         INNER JOIN TB_G2B001D g
                    ON g.G2B_D_CD = d.G2B_D_CD
         INNER JOIN TB_G2B001M c
                    ON c.G2B_M_CD = g.G2B_M_CD
         INNER JOIN TB_ORG002M dept
                    ON dept.DEPT_CD = d.DEPT_CD
                        AND dept.ORG_CD = d.ORG_CD
WHERE d.ITM_NO = 'M202600001'
  AND d.ORG_CD = '7008277'
  AND d.DEL_YN = 'N'
  AND m.DEL_YN = 'N';

-- [수정] 불용 기본 조회 (005M)
select * from TB_ITEM005M;