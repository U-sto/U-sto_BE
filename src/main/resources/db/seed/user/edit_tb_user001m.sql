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
     'test@usto.com', '01000000004', 'GUEST', 'WAIT',
     NULL, NULL, 'HANYANG_ERICA', 'SYSTEM');
SELECT *
FROM TB_USER001M;

-- 로그인 확인을 위한 박도현 계정 강제 승인처리
UPDATE TB_USER001M
SET ROLE_ID = 'GUEST',
    APPR_STS = 'WAIT'
WHERE USR_ID = 'badbergjr';

DELETE FROM tb_user001m
WHERE USR_ID = 'badbergjr';

UPDATE TB_USER001M
SET EMAIL = 'badbergjrr@naver.com',
    SMS = '01099569414'
WHERE USR_ID = 'badbergjr';


-- 테스트를 위한 삭제 취소(이건 어거지)
UPDATE TB_USER001M
SET DEL_YN = 'N',
    DEL_AT = null
WHERE USR_ID = 'badbergjr';

-- 충돌나면 이걸 먼저 하고나서 돌려주세요
USE usto;
-- 1. 인덱스 삭제
DROP INDEX idx_user_del_yn ON tb_user001m;

-- 2. 컬럼 삭제
ALTER TABLE tb_user001m
    DROP COLUMN DEL_YN,
    DROP COLUMN DEL_AT;
-- 실패한 V5 기록 삭제 (로컬에서만!)
DELETE
FROM flyway_schema_history
WHERE version = '5';
