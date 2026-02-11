USE usto;

-- ============================================
-- 1. 기존 테이블 전체 삭제 (데이터 백업 권장)
-- ============================================

-- 기존 테이블 삭제
DROP TABLE IF EXISTS `TB_ITEM006M`;
DROP TABLE IF EXISTS `TB_ITEM005D`;
DROP TABLE IF EXISTS `TB_ITEM005M`;
DROP TABLE IF EXISTS `TB_ITEM004D`;
DROP TABLE IF EXISTS `TB_ITEM004M`;
DROP TABLE IF EXISTS `TB_ITEM003D`;
DROP TABLE IF EXISTS `TB_ITEM003M`;
DROP TABLE IF EXISTS `TB_ITEM002D`;
DROP TABLE IF EXISTS `TB_ITEM002M`;
DROP TABLE IF EXISTS `TB_ITEM001M`;


-- ============================================
-- 2. 신규 테이블 생성
-- ============================================

-- TB_ITEM001M 물품취득기본
CREATE TABLE `TB_ITEM001M` (
  `ACQ_ID`        BINARY(16)      NOT NULL COMMENT '취득ID',
  `G2B_D_CD`      CHAR(8)         NOT NULL COMMENT '물품식별코드',
  `ACQ_AT`        DATE            NOT NULL COMMENT '취득일자',
  `ACQ_UPR`       DECIMAL(20,0)   NOT NULL COMMENT '취득단가(기본)',
  `DEPT_CD`       CHAR(5)         NOT NULL COMMENT '운용부서코드',
  `DRB_YR`        VARCHAR(20)     NOT NULL COMMENT '내용연수(기본)',
  `ACQ_QTY`       INT             NOT NULL COMMENT '취득수량',
  `ACQ_ARRG_TY`   VARCHAR(20)     NOT NULL COMMENT '취득정리구분',
  `APPR_STS`      VARCHAR(20)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `RMK`           VARCHAR(500)    NULL     COMMENT '비고',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `APPR_AT`       DATE            NULL     COMMENT '확정일자(정리일자)',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`ACQ_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품취득기본';


-- TB_ITEM002 물품대장
CREATE TABLE `TB_ITEM002` (
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `ACQ_ID`        BINARY(16)      NOT NULL COMMENT '취득ID',
  `G2B_D_CD`      CHAR(8)         NOT NULL COMMENT '물품식별코드',
  `DEPT_CD`       CHAR(5)         NOT NULL COMMENT '운용부서코드',
  `OPER_STS`      VARCHAR(30)     NOT NULL COMMENT '운용상태',
  `ACQ_UPR`       DECIMAL(20,0)   NOT NULL COMMENT '취득단가',
  `DRB_YR`        VARCHAR(20)     NOT NULL COMMENT '내용연수',
  `RMK`           VARCHAR(500)    NULL     COMMENT '비고',
  `PRINT_YN`      CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '출력여부',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`ITM_NO`, `ORG_CD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품대장';


-- TB_ITEM003M 물품운용기본
CREATE TABLE `TB_ITEM003M` (
  `OPER_M_ID`     BINARY(16)      NOT NULL COMMENT '운용ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `APLY_AT`       DATE            NOT NULL COMMENT '운용(등록)일자',
  `DEPT_CD`       CHAR(5)         NOT NULL COMMENT '운용부서코드',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `OPER_APPR_AT`  DATE            NULL     COMMENT '운용확정일자',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`OPER_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품운용기본';


-- TB_ITEM003D 물품운용상세
CREATE TABLE `TB_ITEM003D` (
  `OPER_D_ID`     BINARY(16)      NOT NULL COMMENT '운용상세ID',
  `OPER_M_ID`     BINARY(16)      NOT NULL COMMENT '운용ID',
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`OPER_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품운용상세';


-- TB_ITEM004M 물품반납기본
CREATE TABLE `TB_ITEM004M` (
  `RTRN_M_ID`     BINARY(16)      NOT NULL COMMENT '반납ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `APLY_AT`       DATE            NOT NULL COMMENT '반납(등록)일자',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `CHG_RSN`       VARCHAR(30)     NOT NULL COMMENT '사유',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `RTRN_APPR_AT`  DATE            NULL     COMMENT '반납확정일자',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`RTRN_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품반납기본';


-- TB_ITEM004D 물품반납상세
CREATE TABLE `TB_ITEM004D` (
  `RTRN_D_ID`     BINARY(16)      NOT NULL COMMENT '반납상세ID',
  `RTRN_M_ID`     BINARY(16)      NOT NULL COMMENT '반납ID',
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `DEPT_CD`       CHAR(5)         NOT NULL COMMENT '운용부서코드',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`RTRN_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품반납상세';


-- TB_ITEM005M 물품불용기본
CREATE TABLE `TB_ITEM005M` (
  `DSU_M_ID`      BINARY(16)      NOT NULL COMMENT '불용ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `APLY_AT`       DATE            NOT NULL COMMENT '불용(등록)일자',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `CHG_RSN`       VARCHAR(30)     NOT NULL COMMENT '사유',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `DSU_APPR_AT`   DATE            NULL     COMMENT '불용확정일자',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`DSU_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품불용기본';


-- TB_ITEM005D 물품불용상세
CREATE TABLE `TB_ITEM005D` (
  `DSU_D_ID`      BINARY(16)      NOT NULL COMMENT '불용상세ID',
  `DSU_M_ID`      BINARY(16)      NOT NULL COMMENT '불용ID',
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `DEPT_CD`       CHAR(5)         NOT NULL COMMENT '운용부서코드',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`DSU_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품불용상세';


-- TB_ITEM006M 물품처분기본
CREATE TABLE `TB_ITEM006M` (
  `DISP_M_ID`     BINARY(16)      NOT NULL COMMENT '처분ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `APLY_AT`       DATE            NOT NULL COMMENT '처분(등록)일자',
  `DISP_ARRG_TY`  VARCHAR(30)     NOT NULL COMMENT '처분정리구분',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `DISP_APPR_AT`  DATE            NULL     COMMENT '처분확정일자',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`DISP_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품처분기본';


-- TB_ITEM006D 물품처분상세
CREATE TABLE `TB_ITEM006D` (
  `DISP_D_ID`     BINARY(16)      NOT NULL COMMENT '처분상세ID',
  `DISP_M_ID`     BINARY(16)      NOT NULL COMMENT '처분ID',
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `CHG_RSN`       VARCHAR(30)     NOT NULL COMMENT '사유',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`DISP_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품처분상세';


-- TB_ITEM007 물품상태이력
CREATE TABLE `TB_ITEM007` (
  `ITEM_HIS_ID`   BINARY(16)      NOT NULL COMMENT '상태이력ID',
  `ITM_NO`        CHAR(10)        NOT NULL COMMENT '물품고유번호',
  `PREV_STS`      VARCHAR(30)     NOT NULL COMMENT '이전상태',
  `NEW_STS`       VARCHAR(30)     NOT NULL COMMENT '변경상태',
  `CHG_RSN`       VARCHAR(200)    NULL     COMMENT '변경사유',
  `REQ_USR_ID`    VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `REQ_AT`        DATE            NOT NULL COMMENT '등록일자',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `APPR_AT`       DATE            NULL     COMMENT '확정일자',
  `ORG_CD`        CHAR(7)         NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`        VARCHAR(100)    NOT NULL COMMENT '생성자ID',
  `CRE_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`        VARCHAR(100)    NULL COMMENT '수정자ID',
  `UPD_AT`        DATETIME        NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
    --
  `DEL_YN`        CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '삭제여부(Y/N)',
  `DEL_AT`        DATETIME        NULL     COMMENT '삭제일시',

  PRIMARY KEY (`ITEM_HIS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품상태이력';