Use usto;

SELECT *
FROM TB_ITEM001M;

SELECT *
FROM TB_ITEM001M
WHERE ACQ_ID = '3c745568-f9cc-11f0-ae8f-8c554a43c9a1';

SELECT *
FROM TB_G2B001D
WHERE G2B_D_CD = '24120278';



SELECT *
FROM TB_ITEM002M;

SELECT *
FROM TB_ITEM002D;

DELETE FROM TB_ITEM002D
WHERE DEPT_CD = 'B103' ;

SELECT
    HEX(ACQ_ID) AS ACQ_ID_HEX,  -- 바이너리를 문자열로 변환하여 확인
    G2B_D_CD,
    APPR_STS,
    ACQ_AT
FROM TB_ITEM001M;

select * from TB_ITEM002D;

SELECT
    d.ITM_NO,
    d.ORG_CD,
    g.G2B_D_NM,
    CONCAT(c.G2B_M_CD, '-', g.G2B_D_CD) AS g2bItemNo,
    d.ACQ_UPR,
    m.ACQ_AT,
    m.ARRG_AT,
    d.OPER_STS,
    d.DRB_YR,
    dept.DEPT_NM,
    m.QTY,
    d.RMK
FROM TB_ITEM002D d
         INNER JOIN TB_ITEM002M m
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
  AND m.DEL_YN = 'N'
;

select * from TB_ITEM002D;