package com.usto.api.common.code.domain;

/**
 * Enum을 코드 그룹으로 변환하기 위한 인터페이스
 * 프론트가 드롭다운/라디오/체크박스로 선택해야 하는 공통코드 Enum은 이 인터페이스를 구현해야함.
 * 백엔드 내부 로직에만 쓰이는 Enum은 해당 x
 */
public interface CodeGroup {
    String getCode();

    String getDescription();
}