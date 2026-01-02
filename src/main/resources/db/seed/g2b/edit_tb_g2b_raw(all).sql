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

-- 3) 검증
SELECT COUNT(*) AS row_count
FROM TB_G2B_RAW;

SELECT COUNT(DISTINCT G2B_D_CD)
FROM TB_G2B_RAW;

SELECT *
FROM TB_G2B_RAW
WHERE G2B_D_CD = '24612653';

SELECT *
FROM TB_G2B_RAW
LIMIT 100;
