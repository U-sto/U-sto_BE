# U-sto Backend

> **대학 물품 관리 시스템 — 백엔드 서버**  
> U-sto BackEnd API v1.8.3

대학 내 물품의 취득부터 운용, 반납, 불용, 처분까지 전체 생애주기를 관리하는 백엔드 API 서버입니다.  
공공데이터포털(G2B) 연동, AI 기반 수요 예측 및 AI 챗봇, QR 코드 라벨 출력, PDF 생성, 이메일/SMS 인증 등 다양한 기능을 포함합니다.

---

## 📌 목차

- [기술 스택](#기술-스택)
- [시스템 아키텍처](#시스템-아키텍처)
- [물품 생애주기](#물품-생애주기)
- [구현 기능 상세](#구현-기능-상세)
- [관련 레포지토리](#관련-레포지토리)

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security (Session 기반) |
| ORM | Spring Data JPA + QueryDSL 5.1 |
| DB Migration | Flyway |
| Database | MySQL |
| API Docs | SpringDoc OpenAPI (Swagger UI) 2.8.5 |
| HTTP Client | Spring WebFlux (WebClient) |
| Template Engine | Thymeleaf |
| AI | Spring AI 1.0.0-M5 (OpenAI) |
| SMS 인증 | NuriGo SDK 4.3.0 |
| QR 코드 | Google ZXing 3.5.1 |
| PDF 생성 | Apache PDFBox 2.0.29 |
| Build | Gradle |

---

## 시스템 아키텍처

```
[클라이언트 (FE / AI팀)]
         │
         ▼
[Spring Boot API Server]
  ├── 인증/인가 (Spring Security + JSESSIONID)
  ├── 물품 생애주기 관리
  │     취득 → 운용 전환 → 반납 → 불용 → 처분
  ├── 물품 운용 대장 조회 / 개별 물품 상세 조회
  ├── 물품 보유 현황 집계 조회
  ├── G2B 공공데이터 연동 (품목 조회 / 내용연수 동기화)
  ├── AI 수요 예측 (Spring AI + OpenAI)
  ├── AI 챗봇 (AI팀 RAG 서버 연동)
  ├── QR 라벨 PDF 출력 (ZXing + PDFBox)
  └── 이메일 / SMS 인증
         │
         ▼
     [MySQL + Flyway]
```

---

## 물품 생애주기

```
              [운용 전환]      ◄──────────────────┐
                    │                           │
                    ▼                           │
[취득 등록] ──► [운용 대장]                    [반납 완료]
                    │                           │
                    ▼                           │
              [반납 신청] ────────────────────────┘
                    │
                    ▼
              [불용 신청]
                    │
                    ▼
              [처분 신청]
```

### 모든 신청의 공통 결재 상태 흐름

```
WAIT (작성중)
    │  승인요청
    ▼
REQUEST (승인 요청) ──── 취소 ────► (소프트 삭제)
    │
    ├── APPROVED (승인 확정)
    └── REJECTED (반려)
```

> WAIT 상태에서는 수정 및 삭제가 가능하며, REQUEST 이후에는 요청 취소(소프트 삭제)를 통해서만 되돌릴 수 있습니다.

---

## 구현 기능 상세

### 1. 회원 및 인증 (`/api/auth`, `/api/users`)

#### 로그인 / 로그아웃
- `POST /api/auth/login` — 로그인 (JSESSIONID 쿠키 발급)
- `POST /api/auth/logout` — 로그아웃

#### 회원가입 / 탈퇴
- `POST /api/users/sign-up` — 회원가입 (아이디, 이름, 비밀번호, 소속 조직 입력)
- `DELETE /api/users/delete` — 회원 탈퇴 (현재 비밀번호 확인 후 처리)

#### 회원 정보 조회 / 수정
- `GET /api/users/info` — 내 정보 조회 (아이디, 이름, 이메일, 전화번호, 조직, 권한)
- `PATCH /api/users/update/password` — 비밀번호 변경 (기존 비밀번호 확인 필요)
- `PATCH /api/users/update/sms` — 휴대폰 번호 변경

#### 중복 확인
- `GET /api/users/exists/user-id` — 아이디 중복 확인
- `GET /api/users/exists/email` — 이메일 중복 확인
- `GET /api/users/exists/sms` — 전화번호 중복 확인

#### 아이디 찾기 / 비밀번호 재설정
- `POST /api/auth/find/user-id` — 아이디 찾기 (이메일 인증 후 반환)
- `POST /api/auth/find/password` — 비밀번호 재설정

#### 이메일 / SMS 인증
- `POST /api/auth/verification/email/send` — 이메일 인증번호 전송
- `POST /api/auth/verification/email/check` — 이메일 인증번호 확인
- `POST /api/auth/verification/sms/send` — 휴대폰 인증번호 전송 (회원가입/아이디찾기/비밀번호재설정 목적 구분)
- `POST /api/auth/verification/sms/check` — 휴대폰 인증번호 확인

> 비밀번호 정책: 영문 대소문자 + 숫자 + 특수문자 포함 8자 이상

---

### 2. 조직 / 부서 관리 (`/api/organization`)

- `GET /api/organization/organizations` — 전체 조직 목록 조회 (회원가입 시 소속 선택에 사용)
- `GET /api/organization/departments` — 현재 로그인 사용자 소속 조직의 부서 목록 조회 (드롭다운용)

---

### 3. G2B 공공데이터 연동 (`/api/g2b`)

조달청 나라장터(G2B) 데이터를 연동하여 물품 품목 정보를 제공합니다.

- `GET /api/g2b/categories` — G2B 물품 분류 조회 (물품분류코드 또는 분류명으로 검색, 미입력 시 전체 반환)
- `GET /api/g2b/items` — G2B 물품 품목 조회 (물품분류코드, 물품식별코드, 품목명으로 검색, 미입력 시 빈 리스트 반환)
- `PUT /api/g2b/sync` — G2B 물품 목록 정보 최신화 (자동 동기화)
- `PUT /api/g2b/add-drbYr` — 물품별 내용연수 최신화

---

### 4. 물품 취득 관리 (`/api/item/acquisitions`)

물품을 신규 등록하는 프로세스입니다. G2B 물품식별코드를 기반으로 등록하며, 수량만큼 물품고유번호(예: `M202600001`)가 자동 생성됩니다.

| 메서드 | 엔드포인트 | 설명 | 권한 |
|--------|-----------|------|------|
| GET | `/api/item/acquisitions` | 취득 목록 조회 (페이징 / G2B, 날짜, 부서, 상태 필터) | ALL |
| POST | `/api/item/acquisitions` | 취득 등록 (G2B식별코드, 취득일자, 수량, 정리구분, 부서) | MANAGER |
| PATCH | `/api/item/acquisitions/{acqId}` | 취득 정보 수정 (WAIT 상태만 가능) | MANAGER |
| DELETE | `/api/item/acquisitions/{acqId}` | 취득 삭제 / 소프트 딜리트 (WAIT 상태만 가능) | MANAGER |
| POST | `/api/item/acquisitions/{acqId}/request` | 승인 요청 | MANAGER |
| POST | `/api/item/acquisitions/{acqId}/cancel` | 승인 요청 취소 | MANAGER |
| PUT | `/api/item/acquisitions/admin/approval` | 취득 일괄 승인 확정 | ADMIN |
| PUT | `/api/item/acquisitions/admin/reject` | 취득 일괄 반려 | ADMIN |

**정리구분(arrgTy):** `BUY`(자체구입), `DONATE`(기증), `MAKE`(자체제작)

---

### 5. 물품 운용 전환 관리 (`/api/item/operations`)

취득 승인 또는 반납 완료된 물품을 운용 상태로 전환합니다. 여러 물품을 한 번에 신청할 수 있습니다.

| 메서드 | 엔드포인트 | 설명 | 권한 |
|--------|-----------|------|------|
| GET | `/api/item/operations` | 운용 등록 목록 조회 (페이징, 기본 30개) | ALL |
| GET | `/api/item/operations/{operMId}/items` | 특정 운용 신청의 물품 목록 조회 | ALL |
| POST | `/api/item/operations` | 운용 신청 등록 (운용일자, 물품상태, 부서, 물품번호 목록) | MANAGER |
| PATCH | `/api/item/operations/{operMId}` | 운용 신청 수정 (WAIT 상태만) | MANAGER |
| DELETE | `/api/item/operations/{operMId}` | 운용 신청 삭제 (WAIT 상태만) | MANAGER |
| POST | `/api/item/operations/{operMId}/request` | 승인 요청 | MANAGER |
| POST | `/api/item/operations/{operMId}/cancel` | 승인 요청 취소 | MANAGER |
| PUT | `/api/item/operations/admin/{operMId}/approve` | 운용 승인 확정 | ADMIN |
| PUT | `/api/item/operations/admin/{operMId}/reject` | 운용 반려 | ADMIN |

---

### 6. 물품 운용 대장 관리 (`/api/item/assets`)

현재 운용 중인 전체 물품 현황을 조회하고 개별 물품 정보를 관리합니다.

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/api/item/assets` | 운용 대장 목록 조회 (G2B, 취득일자, 정리일자, 부서, 운용상태, 물품번호 필터) |
| GET | `/api/item/assets/{itmNo}` | 개별 물품 상세 조회 + 상태 이력 목록 |
| PATCH | `/api/item/assets/{itmNo}` | 개별 물품 정보 수정 (취득단가, 내용연수, 비고 / 불용·삭제(처분) 상태 제외) |
| GET | `/api/item/assets/print` | QR 출력 관리 목록 조회 (출력 여부 Y/N 필터 포함) |
| POST | `/api/item/assets/print` | 선택된 물품들의 QR 라벨을 PDF로 생성하여 다운로드 |

**운용상태(operSts):** `OPER`(운용), `RTN`(반납), `DSU`(불용), `DISP`(처분)

> 개별 물품 상세 조회 시 해당 물품의 전체 상태 변경 이력(이전상태 → 변경상태, 변경사유, 등록자, 확정자, 일자)도 함께 반환됩니다.

---

### 7. 물품 반납 관리 (`/api/item/returnings`)

운용 중인 물품을 반납 처리합니다. 여러 물품을 한 번에 신청할 수 있습니다.

| 메서드 | 엔드포인트 | 설명 | 권한 |
|--------|-----------|------|------|
| GET | `/api/item/returnings` | 반납 목록 조회 (페이징) | ALL |
| GET | `/api/item/returnings/{rtrnMId}/items` | 특정 반납 신청의 물품 목록 조회 | ALL |
| POST | `/api/item/returnings` | 반납 신청 등록 (반납일자, 물품상태, 반납사유, 물품번호 목록) | MANAGER |
| PATCH | `/api/item/returnings/{rtrnMId}` | 반납 신청 수정 (WAIT 상태만) | MANAGER |
| DELETE | `/api/item/returnings/{rtrnMId}` | 반납 신청 삭제 (WAIT 상태만) | MANAGER |
| POST | `/api/item/returnings/{rtrnMId}/request` | 승인 요청 | MANAGER |
| POST | `/api/item/returnings/{rtrnMId}/cancel` | 승인 요청 취소 | MANAGER |
| PUT | `/api/item/returnings/admin/{rtrnMId}/approval` | 반납 승인 확정 | ADMIN |
| PUT | `/api/item/returnings/admin/{rtrnMId}/reject` | 반납 반려 | ADMIN |

**반납사유(rtrnRsn):** `PROJECT_ENDED`(사업종료), `SURPLUS`(잉여물품), `COMMON_CONVERSION`(공용전환), `BROKEN`(고장/파손)

---

### 8. 물품 불용 관리 (`/api/item/disuses`)

반납(RTN) 상태의 물품을 불용 처리합니다. 여러 물품을 한 번에 신청할 수 있습니다.

| 메서드 | 엔드포인트 | 설명 | 권한 |
|--------|-----------|------|------|
| GET | `/api/item/disuses` | 불용 목록 조회 (페이징) | ALL |
| GET | `/api/item/disuses/{dsuMId}/items` | 특정 불용 신청의 물품 목록 조회 | ALL |
| POST | `/api/item/disuses` | 불용 신청 등록 (불용일자, 물품상태, 불용사유, 물품번호 목록) | MANAGER |
| PATCH | `/api/item/disuses/{dsuMId}` | 불용 신청 수정 (WAIT 상태만) | MANAGER |
| DELETE | `/api/item/disuses/{dsuMId}` | 불용 신청 삭제 (WAIT 상태만) | MANAGER |
| POST | `/api/item/disuses/{dsuMId}/request` | 승인 요청 | MANAGER |
| POST | `/api/item/disuses/{dsuMId}/cancel` | 승인 요청 취소 | MANAGER |
| PUT | `/api/item/disuses/admin/{dsuMId}/approval` | 불용 승인 확정 | ADMIN |
| PUT | `/api/item/disuses/admin/{dsuMId}/reject` | 불용 반려 | ADMIN |

**불용사유(dsuRsn):** `LIFE_EXPIRED`(내용연수경과), `OBSOLETE`(구형화), `NO_DEPT`(활용부서부재), `HIGH_REPAIR`(수리비용과다), `DAMAGED`(고장/파손), `DETERIORATED`(노후화)

---

### 9. 물품 처분 관리 (`/api/item/disposals`)

불용(DSU) 상태의 물품을 최종 처분합니다. 여러 물품을 한 번에 신청할 수 있습니다.

| 메서드 | 엔드포인트 | 설명 | 권한 |
|--------|-----------|------|------|
| GET | `/api/item/disposals` | 처분 목록 조회 (페이징) | ALL |
| GET | `/api/item/disposals/{dispMId}/items` | 특정 처분 신청의 물품 목록 조회 | ALL |
| POST | `/api/item/disposals` | 처분 신청 등록 (처분일자, 처분유형, 물품번호 목록) | MANAGER |
| PATCH | `/api/item/disposals/{dispMId}` | 처분 신청 수정 (WAIT 상태만) | MANAGER |
| DELETE | `/api/item/disposals/{dispMId}` | 처분 신청 삭제 (WAIT 상태만) | MANAGER |
| POST | `/api/item/disposals/{dispMId}/request` | 승인 요청 | MANAGER |
| POST | `/api/item/disposals/{dispMId}/cancel` | 승인 요청 취소 | MANAGER |
| PUT | `/api/item/disposals/admin/{dispMId}/approval` | 처분 승인 확정 | ADMIN |
| PUT | `/api/item/disposals/admin/{dispMId}/reject` | 처분 반려 | ADMIN |

**처분유형(dispType):** `DISCARD`(폐기), `SALE`(매각), `LOSS`(분실), `THEFT`(도난)

---

### 10. 물품 보유 현황 조회 (`/api/item/asset-inventory-status`)

승인된 취득 건 기준으로 현재 보유 현황을 집계합니다. 같은 속성(운용부서, 운용상태, 취득금액, 내용연수, 비고)을 가진 물품들을 그룹핑하여 수량으로 표시합니다.

- `GET /api/item/asset-inventory-status` — 보유현황 목록 조회 (G2B, 부서, 취득일자, 정리일자 필터 / 페이징)
- `GET /api/item/asset-inventory-status/detail` — 특정 그룹의 상세 정보 + 해당 그룹 내 모든 물품고유번호 목록 조회

---

### 11. AI 수요 예측 (`/api/ai/forecast`)

연도, 학기, 캠퍼스, 학과, 카테고리, 리스크 레벨 조건을 기반으로 물품 수요를 예측하고 조달 권고안을 생성합니다.

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| POST | `/api/ai/forecast` | 통계 예측 분석 실행 |
| GET | `/api/ai/forecast` | 이전 예측 기록 목록 조회 |
| GET | `/api/ai/forecast/contents` | 특정 예측 기록 내용 조회 |
| DELETE | `/api/ai/forecast` | 예측 기록 삭제 |

**예측 결과 응답 구조:**
- `section_1_time_series` — 월별 수요량 시계열 데이터 (ROP 도달 여부 포함)
- `section_2_portfolio` — 물품별 RUL(잔여 수명) vs 중요도 포트폴리오 데이터
- `section_3_recommendations` — 조달 권고안 (품목명, 수량, 예산, 권고 발주일, AI 인사이트)

**리스크 레벨(risk_level):** `HIGH`, `MEDIUM`, `LOW`

---

### 12. AI 챗봇 (`/api/ai/chat`)

AI팀(U-sto_AI)의 RAG 기반 챗봇 서버와 연동하여 물품 관련 질의응답을 처리합니다. 쓰레드(채팅방) 단위로 대화 맥락을 유지합니다.

| 메서드 | 엔드포인트 | 설명                             |
|--------|-----------|--------------------------------|
| POST | `/api/ai/chat` | 챗봇과 대화 (threadId로 대화 맥락 유지)    |
| GET | `/api/ai/chat/threads` | 채팅 쓰레드 목록 조회                   |
| DELETE | `/api/ai/chat/threads` | 채팅 쓰레드 삭제                      |
| GET | `/api/ai/chat/threads/{threadId}/messages` | 특정 쓰레드의 이전 대화 맥락 조회 (채팅방 입장 시) |
| GET | `/api/ai/chat/messages/search` | 전체 대화 내용 키워드 검색                |

---

### 13. AI용 물품 조회 (`/api/ai/item/assets`)

AI팀에서 RAG 파이프라인을 통해 물품 정보를 조회할 때 사용하는 전용 엔드포인트입니다.

- `GET /api/ai/item/assets` — G2B 목록명, G2B 목록번호, 물품고유번호 중 하나 이상으로 물품 전체 정보 조회

---

### 14. 공통 코드 조회 (`/api/codes`)

시스템 전반에서 사용하는 공통 코드 그룹을 조회합니다.

- `GET /api/codes` — 전체 공통 코드 그룹 조회
- `GET /api/codes/{groupName}` — 특정 코드 그룹 조회

---

## 관련 레포지토리

| 레포 | 설명 |
|------|------|
| [U-sto_BE](https://github.com/U-sto/U-sto_BE) | 백엔드 API 서버 (현재 레포) |
| [U-sto_AI](https://github.com/U-sto/U-sto_AI) | AI 예측 파이프라인 (Manual JSON → QA → Embedding → ChromaDB → LLM) |
