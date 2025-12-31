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
 DROP TABLE IF EXISTS TB_G2B_RAW;
 DROP TABLE IF EXISTS TB_G2B001D;
 DROP TABLE IF EXISTS TB_G2B001M;
 SET FOREIGN_KEY_CHECKS = 1;
/*
 를 별도로 진행하시고 프로그램을 실행하셔야합니다. (그냥 커서 두고 실행하면 실행 됩니다)
 이후 잘 되면 더 이상 이 작업 필요 없습니다
 (flyway는 성공한 V_n은 더 이상 실행하지 않음)
 */