USE usto;
/**
 * TB_ITEM007 (물품상태이력)
 * 최초 취득 시에는 이전 상태가 존재하지 않으므로 NULL 허용으로 변경
 */
ALTER TABLE `TB_ITEM007`
    MODIFY COLUMN `PREV_STS` VARCHAR(30) NULL COMMENT '이전상태';