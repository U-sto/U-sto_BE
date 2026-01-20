/*
 [V-n 작업 일괄 취소]
 싱크가 안 맞는 문제가 발생한다면
 */
 USE usto;
 SET FOREIGN_KEY_CHECKS = 0; -- 외래 키 검사 끄기
 DROP TABLE IF EXISTS flyway_schema_history; -- Flyway 실패기록 삭제
 DROP TABLE IF EXISTS TB_USER001M;
 DROP TABLE IF EXISTS TB_ORG002M;
 DROP TABLE IF EXISTS TB_ORG001M;
 DROP TABLE IF EXISTS TB_VERIF001M;
-- G2B
 DROP TABLE IF EXISTS TB_G2B_RAW;
 DROP TABLE IF EXISTS TB_G2B001D;
 DROP TABLE IF EXISTS TB_G2B001M;
-- ITEM
 DROP TABLE IF EXISTS
    TB_ITEM001M,
    TB_ITEM002M, TB_ITEM002D,
    TB_ITEM003M, TB_ITEM003D,
    TB_ITEM004M, TB_ITEM004D,
    TB_ITEM005M, TB_ITEM005D,
    TB_ITEM006M;
 DROP DATABASE usto;
 CREATE DATABASE usto;
 USE usto;
 DROP TABLE IF EXISTS flyway_schema_history; -- 개발환경에서만 가능한....(V1부터 다시 실행시키는 방법)
 SET FOREIGN_KEY_CHECKS = 1;
/*
 를 별도로 진행하시고 프로그램을 실행하셔야합니다. (그냥 커서 두고 실행하면 실행 됩니다)
 이후 잘 되면 더 이상 이 작업 필요 없습니다
 (flyway는 성공한 V_n은 더 이상 실행하지 않음)
 */

-- Flyway에게 "V5 실행한 적 없다"고 거짓말을 해서 기억을 지웁니다.
