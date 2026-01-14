use usto;

-- TB_ITEM001M 물품취득기본 (취득 당시 스냅샷)
CREATE TABLE `TB_ITEM001M` (
  `ACQ_ID`        BIGINT          NOT NULL AUTO_INCREMENT COMMENT '취득ID',
  `G2B_D_CD`      CHAR(8)         NOT NULL COMMENT '물품식별코드',
  `ACQ_AT`        DATETIME        NOT NULL COMMENT '취득일자',
  `ACQ_UPR`       DECIMAL(20,0)   NOT NULL COMMENT '취득단가',
  `DEPT_CD`       VARCHAR(50)     NOT NULL COMMENT '운용부서코드',
  `DRB_YR`        VARCHAR(20)     NOT NULL COMMENT '내용연수',
  `ACQ_QTY`       INT             NOT NULL COMMENT '취득수량',
  `ARRG_TY`       VARCHAR(20)     NOT NULL COMMENT '정리구분',                 -- BUY/DONATE/MAKE (자체구입/기증/자체제작)
  `APPR_STS`      VARCHAR(20)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',  -- WAIT/REJECTED/APPROVED (대기/반려/확정)
  `RMK`           VARCHAR(500)    NULL     COMMENT '비고',
  `ACQ_USR_ID`    VARCHAR(30)     NOT NULL COMMENT '담당자ID',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `APPR_AT`       DATETIME        NULL     COMMENT '확정일자(정리일자)',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`  VARCHAR(100)   NOT NULL COMMENT '생성자ID',
  `CRE_AT`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`  VARCHAR(100)   NULL COMMENT '수정자ID',
  `UPD_AT`  DATETIME       NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',

  PRIMARY KEY (`ACQ_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품취득기본';


-- TB_ITEM002M 물품대장기본 (취득 묶음 단위로 관리하는 대장 헤더, 속성 중 수량을 제외하고 전부 취득 시점과 동일)
CREATE TABLE `TB_ITEM002M` (
  `ACQ_ID`        BIGINT          NOT NULL COMMENT '취득ID',
  `G2B_M_CD`      CHAR(8)         NOT NULL COMMENT '물품분류코드',
  `G2B_D_CD`      CHAR(8)         NOT NULL COMMENT '물품식별코드',
  `QTY`           INT             NOT NULL COMMENT '수량',  -- 일부 물품이 처분될 시 수량이 감소함 (초기값: TB_ITEM001M의 '취득수량')
  `ACQ_AT`        DATETIME        NOT NULL COMMENT '취득일자',
  `ACQ_UPR`       DECIMAL(20,0)   NOT NULL COMMENT '취득단가',
  `APPR_AT`       DATETIME        NOT NULL COMMENT '확정일자(정리일자)',
  `DRB_YR`        VARCHAR(20)     NOT NULL COMMENT '내용연수',
  `ARRG_TY`       VARCHAR(20)     NOT NULL COMMENT '정리구분',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`  VARCHAR(100)   NOT NULL COMMENT '생성자ID',
  `CRE_AT`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`  VARCHAR(100)   NULL COMMENT '수정자ID',
  `UPD_AT`  DATETIME       NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',

  PRIMARY KEY (`ACQ_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품대장기본';


-- TB_ITEM002D 물품대장상세 (개별 물품 단위)
CREATE TABLE `TB_ITEM002D` (
  `ITM_NO`        BIGINT          NOT NULL AUTO_INCREMENT COMMENT '물품고유번호',
  `ACQ_ID`        BIGINT          NOT NULL COMMENT '취득ID',
  `G2B_M_CD`      CHAR(8)         NOT NULL COMMENT '물품분류코드',
  `G2B_D_CD`      CHAR(8)         NOT NULL COMMENT '물품식별코드',
  `DEPT_CD`       VARCHAR(50)     NOT NULL COMMENT '운용부서코드',
  `OPER_STS`      VARCHAR(30)     NOT NULL COMMENT '운용상태',  -- ACQ/OPER/RTN/DSU (취득/운용/반납/불용)
  `RMK`           VARCHAR(500)    NULL     COMMENT '비고',
  `PRINT_YN`      CHAR(1)         NOT NULL DEFAULT 'N' COMMENT '출력여부',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',
    --
  `CRE_BY`  VARCHAR(100)   NOT NULL COMMENT '생성자ID',
  `CRE_AT`  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일자',
  `UPD_BY`  VARCHAR(100)   NULL COMMENT '수정자ID',
  `UPD_AT`  DATETIME       NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',

  PRIMARY KEY (`ITM_NO`),
  CONSTRAINT
    FOREIGN KEY (`ACQ_ID`) REFERENCES TB_ITEM002M (`ACQ_ID`)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품대장상세';


-- TB_ITEM003M/D 물품 반납 (M:신청서, D:상세 물품)
CREATE TABLE `TB_ITEM003M` (
  `RTRN_M_ID`     BIGINT          NOT NULL AUTO_INCREMENT COMMENT '반납ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '신청자ID',
  `APLY_AT`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '반납(신청)일자',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `CHG_RSN`       VARCHAR(200)    NULL     COMMENT '사유',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `RTRN_APPR_AT`  DATETIME        NULL     COMMENT '반납확정일자',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',

  PRIMARY KEY (`RTRN_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품반납기본';

CREATE TABLE `TB_ITEM003D` (
  `RTRN_D_ID`     BIGINT          NOT NULL AUTO_INCREMENT COMMENT '반납상세ID',
  `RTRN_M_ID`     BIGINT          NOT NULL COMMENT '반납ID',
  `ITM_NO`        BIGINT          NOT NULL COMMENT '물품고유번호',
  `DEPT_CD`       VARCHAR(50)     NOT NULL COMMENT '운용부서코드',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',

  PRIMARY KEY (`RTRN_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품반납상세';


-- TB_ITEM004M/D 물품 불용 (M:신청서, D:상세 물품)
CREATE TABLE `TB_ITEM004M` (
  `DSU_M_ID`      BIGINT          NOT NULL AUTO_INCREMENT COMMENT '불용ID',
  `APLY_USR_ID`   VARCHAR(30)     NOT NULL COMMENT '신청자ID',
  `APLY_AT`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '불용(신청)일자',
  `ITEM_STS`      VARCHAR(30)     NOT NULL COMMENT '물품상태',
  `CHG_RSN`       VARCHAR(200)    NULL     COMMENT '사유',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `DSU_APPR_AT`   DATETIME        NULL     COMMENT '불용확정일자',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',

  PRIMARY KEY (`DSU_M_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품불용기본';

CREATE TABLE `TB_ITEM004D` (
  `DSU_D_ID`      BIGINT          NOT NULL AUTO_INCREMENT COMMENT '불용상세ID',
  `DSU_M_ID`      BIGINT          NOT NULL COMMENT '불용ID',
  `ITM_NO`        BIGINT          NOT NULL COMMENT '물품고유번호',
  `DEPT_CD`       VARCHAR(50)     NOT NULL COMMENT '운용부서코드',
  `APPR_STS`      VARCHAR(30)     NOT NULL DEFAULT 'WAIT' COMMENT '승인상태',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',

  PRIMARY KEY (`DSU_D_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품불용상세';


-- TB_ITEM005M 상태 변경 이력
CREATE TABLE `TB_ITEM005M` (
  `ITEM_HIS_ID`   BIGINT          NOT NULL AUTO_INCREMENT COMMENT '상태이력ID',
  `ITM_NO`        BIGINT          NOT NULL COMMENT '물품고유번호',
  `PREV_STS`      VARCHAR(30)     NOT NULL COMMENT '이전상태',
  `NEW_STS`       VARCHAR(30)     NOT NULL COMMENT '변경상태',
  `CHG_RSN`       VARCHAR(200)    NULL     COMMENT '변경사유',
  `REQ_USR_ID`    VARCHAR(30)     NOT NULL COMMENT '등록자ID',
  `REQ_AT`        DATETIME        NOT NULL COMMENT '등록일자',
  `APPR_USR_ID`   VARCHAR(30)     NULL     COMMENT '확정자ID',
  `APPR_AT`       DATETIME        NULL     COMMENT '확정일자',
  `ORG_CD`        VARCHAR(50)     NOT NULL COMMENT '조직코드',

  PRIMARY KEY (`ITEM_HIS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품상태이력';


--
CREATE TABLE `TB_ITEM006M` (
                               `ITEM_HIS_ID`   BIGINT          NOT NULL AUTO_INCREMENT COMMENT '처분이력ID',
                               `ITM_NO`        BIGINT          NOT NULL COMMENT '물품고유번호',
                               `CHG_RSN`       VARCHAR(200)    NULL,
                               `REQ_USR_ID`    VARCHAR(30)     NOT NULL,
                               `REQ_AT`        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `APPR_USR_ID`   VARCHAR(30)     NOT NULL,
                               `APPR_AT`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `ORG_CD`        VARCHAR(50)     NOT NULL,
                               `DISP_TYPE`     VARCHAR(30)     NULL     COMMENT '처분방식',

                               PRIMARY KEY (`ITEM_HIS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='물품최종처분이력';