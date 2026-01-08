# Gemini AI 어시스턴트 통합 가이드

## 개요

U-sto 백엔드 시스템에 Google Gemini AI 어시스턴트가 통합되었습니다. 이 기능을 통해 사용자는 AI 어시스턴트와 대화하고 질문에 대한 답변을 받을 수 있습니다.

## 설정 방법

### 1. Gemini API 키 발급

1. [Google AI Studio](https://makersuite.google.com/app/apikey)에 접속합니다.
2. "Get API Key" 버튼을 클릭하여 새 API 키를 생성합니다.
3. 생성된 API 키를 안전한 곳에 보관합니다.

### 2. 환경 변수 설정

개발 환경에서 다음 환경 변수를 설정해야 합니다:

```bash
export GEMINI_API_KEY="your-api-key-here"
```

또는 IDE 설정에서 환경 변수를 추가할 수 있습니다:
- IntelliJ IDEA: Run > Edit Configurations > Environment variables

### 3. 애플리케이션 속성

`application-dev.properties` 파일에 다음 설정이 추가되어 있습니다:

```properties
# gemini ai
gemini.api.key=${GEMINI_API_KEY}
gemini.api.model=gemini-pro
```

## API 사용 방법

### 엔드포인트

**POST** `/api/gemini/ask`

Gemini AI에게 질문하고 응답을 받습니다.

### 요청 예시

```bash
curl -X POST http://localhost:8080/api/gemini/ask \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "U-sto 시스템에 대해 설명해주세요."
  }'
```

### 요청 본문

```json
{
  "prompt": "사용자 질문"
}
```

### 응답 예시

```json
{
  "success": true,
  "message": "AI 응답 생성 성공",
  "data": {
    "response": "U-sto는 공공조달을 위한 시스템으로..."
  }
}
```

## 아키텍처

프로젝트는 Clean Architecture를 따르며, 다음과 같은 레이어로 구성되어 있습니다:

```
com.usto.api.gemini/
├── domain/
│   └── service/
│       └── GeminiAssistantService.java        # 서비스 인터페이스
├── application/
│   └── GeminiAssistantServiceImpl.java       # 서비스 구현체 (REST API 사용)
├── presentation/
│   ├── controller/
│   │   └── GeminiAssistantController.java    # REST 컨트롤러
│   └── dto/
│       ├── GeminiRequestDto.java             # 요청 DTO
│       └── GeminiResponseDto.java            # 응답 DTO
└── infrastructure/
    └── GeminiConfig.java                     # 설정 클래스
```

## 기술 구현

### REST API 호출

Google Gemini API는 REST API를 통해 호출됩니다:
- API URL: `https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent`
- HTTP Method: POST
- Content-Type: application/json

### 에러 처리

`GeminiException`이 발생하면 `GlobalExceptionHandler`에서 처리되어 통일된 응답 형식으로 반환됩니다.

### 보안

- 개발 환경(`dev` 프로파일)에서는 인증 없이 접근 가능합니다.
- 운영 환경에서는 인증이 필요하도록 `SecurityConfig`를 수정할 수 있습니다.

## Swagger UI

Swagger UI를 통해 API를 테스트할 수 있습니다:

1. 애플리케이션을 실행합니다.
2. 브라우저에서 `http://localhost:8080/swagger-ui.html`에 접속합니다.
3. "gemini-assistant-controller" 섹션을 찾습니다.
4. `/api/gemini/ask` 엔드포인트를 선택하고 "Try it out"을 클릭합니다.
5. 요청 본문에 질문을 입력하고 "Execute"를 클릭합니다.

## 예시 사용 사례

### 1. 일반 질문

```json
{
  "prompt": "Java Spring Boot의 주요 특징은 무엇인가요?"
}
```

### 2. 코드 관련 질문

```json
{
  "prompt": "Spring Security에서 CORS를 설정하는 방법을 알려주세요."
}
```

### 3. 시스템 관련 질문

```json
{
  "prompt": "U-sto 시스템에서 사용자 인증은 어떻게 처리되나요?"
}
```

## 주의사항

1. **API 키 보안**: API 키는 절대 코드에 하드코딩하지 마세요. 항상 환경 변수를 사용하세요.
2. **API 사용량**: Google Gemini API는 사용량에 따라 과금될 수 있으므로 주의하세요.
3. **응답 시간**: AI 응답 생성에는 수 초가 걸릴 수 있습니다.
4. **에러 처리**: 네트워크 오류나 API 오류에 대비한 에러 처리가 구현되어 있습니다.

## 문제 해결

### API 키 오류

```
AI 응답 생성에 실패했습니다.
```

- `GEMINI_API_KEY` 환경 변수가 올바르게 설정되었는지 확인하세요.
- API 키가 유효한지 Google AI Studio에서 확인하세요.

### 연결 오류

```
Gemini API 호출 실패
```

- 인터넷 연결을 확인하세요.
- Google Gemini API 서비스 상태를 확인하세요.

## 추가 자료

- [Google Gemini API 문서](https://ai.google.dev/docs)
- [Google AI Studio](https://makersuite.google.com/)
- [Gemini API 가격 정보](https://ai.google.dev/pricing)
