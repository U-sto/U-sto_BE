USE usto;

-- 1. 테이블 삭제
DROP TABLE IF EXISTS tb_chat001d, tb_chat001m, tb_fc001m;

-- 2. Flyway 기록 삭제
DELETE FROM flyway_schema_history WHERE version = 10;