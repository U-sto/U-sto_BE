# 전역 예외 처리 (Global Exception Handling)

## 개요
이 문서는 U-sto 백엔드 프로젝트에 도입된 전역 예외 처리 시스템에 대해 설명합니다.

## 목적
- 모든 API 엔드포인트에서 발생하는 예외를 일관된 형식으로 처리
- 프론트엔드가 항상 동일한 JSON 포맷의 에러 응답을 받을 수 있도록 보장
- 개발자가 예외 처리 로직을 반복 작성하지 않아도 되도록 지원

## 에러 응답 포맷
모든 예외는 다음과 같은 JSON 형식으로 응답됩니다:

```json
{
    "code": "ERROR_CODE",
    "message": "사용자에게 표시될 에러 메시지"
}
```

### 예시
```json
{
    "code": "LOGIN_FAILED",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다."
}
```

## 주요 컴포넌트

### 1. ErrorResponse
에러 응답을 나타내는 DTO 클래스입니다.
- 위치: `com.usto.api.common.dto.ErrorResponse`
- 필드:
  - `code`: 에러 코드 (String)
  - `message`: 에러 메시지 (String)

### 2. BusinessException
비즈니스 로직에서 발생하는 모든 커스텀 예외의 기본 클래스입니다.
- 위치: `com.usto.api.common.exception.BusinessException`
- 생성자:
  - `BusinessException(String errorCode, String message)`
  - `BusinessException(String errorCode, String message, Throwable cause)`

### 3. GlobalExceptionHandler
전역 예외 처리를 담당하는 핸들러입니다.
- 위치: `com.usto.api.common.exception.handler.GlobalExceptionHandler`
- `@RestControllerAdvice` 어노테이션으로 모든 컨트롤러의 예외를 처리합니다.

## 커스텀 예외 작성 방법

### 1. BusinessException을 상속받는 새로운 예외 클래스 생성

```java
package com.usto.api.common.exception;

public class UserNotFoundException extends BusinessException {
    
    private static final String ERROR_CODE = "USER_NOT_FOUND";
    private static final String ERROR_MESSAGE = "사용자를 찾을 수 없습니다.";
    
    public UserNotFoundException() {
        super(ERROR_CODE, ERROR_MESSAGE);
    }
    
    // 동적 메시지가 필요한 경우
    public UserNotFoundException(String userId) {
        super(ERROR_CODE, "사용자를 찾을 수 없습니다: " + userId);
    }
}
```

### 2. 비즈니스 로직에서 예외 발생

```java
@Service
public class UserService {
    
    public User findUser(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
```

### 3. 컨트롤러에서는 별도의 try-catch 불필요

```java
@RestController
public class UserController {
    
    @GetMapping("/api/users/{userId}")
    public ApiResponse<User> getUser(@PathVariable String userId) {
        User user = userService.findUser(userId);
        return ApiResponse.ok("조회 성공", user);
    }
}
```

예외가 발생하면 GlobalExceptionHandler가 자동으로 처리하여 다음과 같은 응답을 반환합니다:

```json
{
    "code": "USER_NOT_FOUND",
    "message": "사용자를 찾을 수 없습니다: user123"
}
```

## 기본 제공 에러 코드

GlobalExceptionHandler는 다음과 같은 Spring 기본 예외들을 자동으로 처리합니다:

| 예외 타입 | HTTP 상태 | 에러 코드 | 설명 |
|----------|----------|----------|------|
| `BusinessException` | 400 BAD_REQUEST | 커스텀 코드 | 비즈니스 로직 예외 |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST | VALIDATION_ERROR | @Valid 검증 실패 |
| `BindException` | 400 BAD_REQUEST | VALIDATION_ERROR | 바인딩 오류 |
| `MissingServletRequestParameterException` | 400 BAD_REQUEST | MISSING_PARAMETER | 필수 파라미터 누락 |
| `MethodArgumentTypeMismatchException` | 400 BAD_REQUEST | TYPE_MISMATCH | 파라미터 타입 불일치 |
| `HttpMessageNotReadableException` | 400 BAD_REQUEST | INVALID_REQUEST_BODY | JSON 파싱 오류 |
| `HttpRequestMethodNotSupportedException` | 405 METHOD_NOT_ALLOWED | METHOD_NOT_ALLOWED | 지원하지 않는 HTTP 메소드 |
| `NoHandlerFoundException` | 404 NOT_FOUND | NOT_FOUND | 존재하지 않는 엔드포인트 |
| `Exception` | 500 INTERNAL_SERVER_ERROR | INTERNAL_SERVER_ERROR | 기타 모든 예외 |

## 기존 예외 목록

### LoginFailedException
- 에러 코드: `LOGIN_FAILED`
- 메시지: "아이디 또는 비밀번호가 올바르지 않습니다."
- 사용 위치: `LoginApplication.login()`

## 마이그레이션 가이드

기존 코드에서 다음과 같이 변경할 수 있습니다:

### Before (기존 방식)
```java
@PostMapping("/api/auth/login")
public ApiResponse<?> login(@RequestBody LoginRequestDto request) {
    try {
        LoginUser user = loginApplication.login(request.getUsrId(), request.getPwd());
        return ApiResponse.ok("로그인 성공", new LoginResponseDto(user));
    } catch (LoginFailedException e) {
        return ApiResponse.fail("로그인 실패");
    }
}
```

### After (새로운 방식)
```java
@PostMapping("/api/auth/login")
public ApiResponse<?> login(@RequestBody LoginRequestDto request) {
    LoginUser user = loginApplication.login(request.getUsrId(), request.getPwd());
    return ApiResponse.ok("로그인 성공", new LoginResponseDto(user));
}
```

예외는 GlobalExceptionHandler가 자동으로 처리하여 다음과 같은 응답을 반환합니다:

```json
{
    "code": "LOGIN_FAILED",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다."
}
```

## 주의사항

1. **에러 코드 네이밍**: 에러 코드는 UPPER_SNAKE_CASE로 작성합니다.
   - 예: `USER_NOT_FOUND`, `INVALID_PASSWORD`, `EMAIL_ALREADY_EXISTS`

2. **에러 메시지**: 사용자에게 직접 표시될 수 있는 친절한 메시지를 작성합니다.
   - 기술적인 세부사항보다는 사용자 관점의 메시지 작성
   - 한글로 작성하여 프론트엔드에서 직접 표시 가능하도록 함

3. **로깅**: 
   - BusinessException은 WARN 레벨로 로깅됩니다.
   - 일반 Exception은 ERROR 레벨로 로깅되며 전체 스택 트레이스가 기록됩니다.

4. **보안**: 민감한 정보(비밀번호, 토큰 등)를 에러 메시지에 포함하지 않습니다.

## 테스트

GlobalExceptionHandler의 동작을 검증하는 단위 테스트가 제공됩니다:
- 위치: `src/test/java/com/usto/api/common/exception/handler/GlobalExceptionHandlerTest.java`
- 실행: `./gradlew test --tests GlobalExceptionHandlerTest`
