USE usto;

INSERT INTO TB_ORG002M (
    ORG_CD,
    DEPT_CD,
    DEPT_NM,
    DEPT_TY,
    CRE_BY
) VALUES
-- 총무관리처 계열
('7008277', 'ADM_HR',     '총무인사팀',                 '총무관리처', 'SYSTEM'),
('7008277', 'ADM_SAFE',   '캠퍼스안전팀',               '총무관리처', 'SYSTEM'),
('7008277', 'ADM_ASSET',  '관재팀',                     '총무관리처', 'SYSTEM'),
('7008277', 'ADM_FAC',    '시설팀',                     '총무관리처', 'SYSTEM'),

-- 단과대학 계열
('7008277', 'COL_SW_RC',  '소프트웨어융합대학RC 행정팀', '단과대학',   'SYSTEM');

-- 타 조직의 운용부서
INSERT INTO TB_ORG002M (
    ORG_CD,
    DEPT_CD,
    DEPT_NM,
    DEPT_TY,
    CRE_BY
) VALUES
('7002282', 'LIB_PLAN', '학술기획운영팀', '백남학술정보관', 'SYSTEM'),
('7002282', 'LIB_RESEARCH', '연구정보팀', '백남학술정보관', 'SYSTEM'),
('7002282', 'ADM_FAC',  '시설팀', '관리처', 'SYSTEM'),
('7002282', 'ADM_ASSET',  '관재팀', '관리처', 'SYSTEM');

-- 확인
SELECT *
FROM TB_ORG002M
ORDER BY DEPT_CD;