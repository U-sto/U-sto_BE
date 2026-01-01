/*
 * SmsUtil
 * - 역할: 외부 SMS 게이트웨이(Solapi/CoolSMS SDK 등)를 통해 인증번호 문자를 전송하는 유틸리티 래퍼입니다.
 * - 설정: application.properties의 coolsms.api.key/secret/from 값을 사용합니다.
 * - 주의: 운영 환경에서는 예외 처리/재시도/로깅/레이트 리밋 등의 보강이 필요합니다.
 */
package com.usto.api.common.utils;

import jakarta.annotation.PostConstruct;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CoolSMS SDK를 이용한 단문 메시지 발송 유틸리티
 * 우리 프로젝트 상황에 맞게 발신번호/문구를 설정해두었습니다.
 */
@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${coolsms.api.from:}")
    private String defaultFrom;

    private DefaultMessageService messageService;

    @PostConstruct
    private void init() {
        this.messageService =
                NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    /**
     * 인증번호 단일 발송
     * @param target 수신자 번호(01012345678 형식)
     * @param code 발송할 인증번호
     */
    public SingleMessageSentResponse sendVerificationCode(String target, String code) {
        Message message = new Message();
        message.setFrom(defaultFrom);
        message.setTo(target);
        message.setText("[U-sto]\n본인확인인증번호\n["+code+"]입니다.\n*타인 노출 금지*");
        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
