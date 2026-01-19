USE usto;

SHOW VARIABLES LIKE 'local_infile'; -- ON이여야합니다.
SET GLOBAL local_infile = 1;

LOAD DATA LOCAL INFILE './src/main/resources/db/data/g2b_list.csv'
    INTO TABLE TB_G2B_RAW
    CHARACTER SET utf8mb4
    FIELDS TERMINATED BY '\t'
    ENCLOSED BY '"'
    LINES TERMINATED BY '\n'
    IGNORE 1 ROWS
    (G2B_M_CD, G2B_M_NM, G2B_D_CD, G2B_D_NM, @upr_raw)
    SET
        G2B_UPR_RAW = REPLACE(@upr_raw, '\r', ''),
        G2B_UPR=
                CASE
                    WHEN REPLACE(@upr_raw, '\r', '') IS NULL OR TRIM(REPLACE(@upr_raw, '\r', '')) = '' THEN NULL
                    WHEN REPLACE(REPLACE(TRIM(@upr_raw), ',', ''), '\r', '') REGEXP '^[0-9]+$'
                        THEN CAST(REPLACE(REPLACE(TRIM(@upr_raw), ',', ''), '\r', '') AS DECIMAL(20,0))
                    ELSE NULL
                    END;

INSERT INTO TB_G2B001M (
    G2B_M_CD,
    G2B_M_NM,
    CRE_BY,
    CRE_AT
)
SELECT DISTINCT
    G2B_M_CD,
    G2B_M_NM,
    'SYSTEM',
    NOW()
FROM TB_G2B_RAW
WHERE G2B_M_CD IS NOT NULL;

INSERT INTO TB_G2B001D (
    G2B_D_CD,
    G2B_M_CD,
    G2B_D_NM,
    G2B_UPR,
    CRE_BY,
    CRE_AT
)
SELECT
    G2B_D_CD,
    ANY_VALUE(G2B_M_CD),
    ANY_VALUE(G2B_D_NM),
    MAX(G2B_UPR),
    'SYSTEM',
    NOW()
FROM TB_G2B_RAW
WHERE G2B_D_CD IS NOT NULL
  AND G2B_M_CD IS NOT NULL
  AND G2B_UPR IS NOT NULL
GROUP BY G2B_D_CD; -- 매우 오래걸립니다.(렉 아니에요)

SELECT COUNT(*) AS row_count FROM TB_G2B001D;
SELECT * FROM TB_G2B001D LIMIT 100;
