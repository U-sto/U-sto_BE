USE usto;

INSERT INTO TB_ORG002M (
    ORG_CD,
    DEPT_CD,
    DEPT_NM,
    DEPT_TY,
    CRE_BY
) VALUES
-- 총무관리처 계열
('HANYANG_ERICA', 'ADM_HR',     '총무인사팀',                 '총무관리처', 'SYSTEM'),
('HANYANG_ERICA', 'ADM_SAFE',   '캠퍼스안전팀',               '총무관리처', 'SYSTEM'),
('HANYANG_ERICA', 'ADM_ASSET',  '관재팀',                     '총무관리처', 'SYSTEM'),
('HANYANG_ERICA', 'ADM_FAC',    '시설팀',                     '총무관리처', 'SYSTEM'),

-- 단과대학 계열
('HANYANG_ERICA', 'COL_SW_RC',  '소프트웨어융합대학RC 행정팀', '단과대학',   'SYSTEM');

-- 타 조직의 운용부서 (운용부서 api 테스트용으로 추가함)
INSERT INTO TB_ORG002M (
    ORG_CD,
    DEPT_CD,
    DEPT_NM,
    DEPT_TY,
    CRE_BY
) VALUES
('HANYANG_SEOUL', 'LIB_PLAN', '학술기획운영팀', '백남학술정보관', 'SYSTEM'),
('HANYANG_SEOUL', 'LIB_RESEARCH', '연구정보팀', '백남학술정보관', 'SYSTEM'),
('HANYANG_SEOUL', 'ADM_FAC',  '시설팀', '관리처', 'SYSTEM'),
('HANYANG_SEOUL', 'ADM_ASSET',  '관재팀', '관리처', 'SYSTEM');

-- 확인
SELECT *
FROM TB_ORG002M
ORDER BY DEPT_CD;