-- 반려는 넣을까 고민했지만, 굳이 싶어서(담당관 많아야 2명임) 주석처리
/* 4) 반려(REJECTED) : 로그인 차단 케이스(Disabled/승인반려됨)
INSERT INTO TB_USER001M
(USR_ID, USR_NM, PW_HASH, EMAIL, SMS, ROLE_ID, APPR_STS, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY)
VALUES
    ('rejected01', '반려유저', '$2b$10$N9qo8uLOickgx2ZMRZo5i.ej3xGJdZQzQ0jrISFRCGDpa2BkLomf6',
     'user_rej@usto.com', '01000000004', 'GUEST', 'REJECTED',
     NULL, NULL, 'HANYANG_ERICA', 'SYSTEM');
*/
USE usto;
-- 검증
INSERT INTO TB_USER001M
(USR_ID, USR_NM, PW_HASH, EMAIL, SMS, ROLE_ID, APPR_STS, APPR_USR_ID, APPR_AT, ORG_CD, CRE_BY)
VALUES
    ('test', '테스트유저', '$2b$10$N9qo8uLOickgx2ZMRZo5i.ej3xGJdZQzQ0jrISFRCGDpa2BkLomf6',
     'test@usto.com', '01000000004', 'GUEST', 'REJECTED',
     NULL, NULL, 'HANYANG_ERICA', 'SYSTEM');
SELECT *
FROM TB_USER001M;