USE usto;

INSERT INTO TB_USER001M (
    USR_ID, USR_NM, PW_HASH, EMAIL, SMS, ROLE_ID, APPR_STS, APPR_USR_ID, APPR_AT,
    ORG_CD, DEL_YN, DEL_AT, CRE_BY, CRE_AT, UPD_BY, UPD_AT
) VALUES (
             'hyl0610',
             '황팀장',
             '$2a$10$QmFxeeEtHZICHdP0cuAciuOE.JLca/BQxCqKc0a8m265Sf524IHO6',
             'admin1@hanyang.ac.kr',
             '01000000000',
             'ADMIN',
             'APPROVED',
             NULL,
             NULL,
             '7008277',
             'N',
             NULL,
             'system',
             NOW(),
             NULL,
             NULL
         );

INSERT INTO TB_USER001M (
    USR_ID, USR_NM, PW_HASH, EMAIL, SMS, ROLE_ID, APPR_STS, APPR_USR_ID, APPR_AT,
    ORG_CD, DEL_YN, DEL_AT, CRE_BY, CRE_AT, UPD_BY, UPD_AT
) VALUES (
             'badbergjr',
             '박대리',
             '$2a$10$nt1NkwIvjiybvwx3TXwWo.T7ni0siaTI2KgVa7/nCt1EdHSHOrmRK',
             'manager1@hanyang.ac.kr',
             '01000000001',
             'MANAGER',
             'APPROVED',
             NULL,
             NULL,
             '7008277',
             'N',
             NULL,
             'system',
             NOW(),
             NULL,
             NULL
         );