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
-- 조회
SELECT *
FROM TB_USER001M;
-- 강제 승인 취소 for 테스트
UPDATE TB_USER001M
SET ROLE_ID = 'GUEST',
    APPR_STS = 'WAIT'
WHERE USR_ID = 'badbergjr';

UPDATE TB_USER001M
SET ROLE_ID = 'ADMIN',
    APPR_STS = 'APPROVED'
WHERE USR_ID = 'badbergjr';

-- 강제 삭제
DELETE FROM tb_user001m
WHERE USR_ID = 'badbergjr';
-- 소프트 삭제 취소
UPDATE TB_USER001M
SET DEL_YN = 'N',
    DEL_AT = null
WHERE USR_ID = 'badbergjr';
