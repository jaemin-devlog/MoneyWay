package com.example.moneyway.user.service;

import com.example.moneyway.common.util.MailService;
import com.example.moneyway.common.exception.CustomException.CustomUserException;
import com.example.moneyway.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

/**
 * ✅ 이메일 인증코드 발송 및 검증을 담당하는 서비스
 * - 인증코드 생성 및 Redis 저장
 * - 이메일 전송
 * - 인증코드 검증 및 인증 상태 저장
 */
@Service
@RequiredArgsConstructor
public class EmailCodeService {

    private final RedisTemplate<String, String> redisTemplate; // Redis 연결 객체
    private final MailService mailService; // 실제 이메일 전송을 담당하는 유틸 서비스

    private static final Duration CODE_TTL = Duration.ofMinutes(5);  // 인증코드 유효기간: 5분

    /**
     * ✅ 인증코드를 생성하고 이메일로 발송한 뒤, Redis에 저장
     * @param email 인증 대상 이메일 주소
     */
    public void sendCode(String email) {
        String code = generateCode();
        String redisKey = buildRedisKey(email);

        redisTemplate.opsForValue().set(redisKey, code, CODE_TTL);

        // ✅ 일반 텍스트 대신 HTML 메일 전송
        mailService.sendVerificationCodeHtml(email, code);
    }


    /**
     * ✅ 사용자가 입력한 인증코드를 검증
     * @param email 사용자 이메일
     * @param inputCode 사용자가 입력한 인증코드
     */
    public void verifyCode(String email, String inputCode) {
        String redisKey = buildRedisKey(email);
        String savedCode = redisTemplate.opsForValue().get(redisKey); // Redis에서 저장된 코드 조회

        if (savedCode == null) {
            // 저장된 코드가 없다면 만료 또는 전송되지 않은 상태
            throw new CustomUserException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!savedCode.equals(inputCode)) {
            // 입력한 코드가 일치하지 않음
            throw new CustomUserException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        // 인증 성공: 기존 인증코드는 삭제
        redisTemplate.delete(redisKey);

        // 인증 상태 저장 (다음 단계에서 재확인 용도)
        redisTemplate.opsForValue().set("verified:" + email, "true", Duration.ofMinutes(10));
    }

    /**
     * ✅ 인증코드 생성 로직 (6자리 숫자)
     * @return 000000 ~ 999999 사이 문자열 코드
     */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    /**
     * ✅ 이메일 기반 Redis 키 생성
     * @param email 사용자 이메일
     * @return Redis Key 형식
     */
    private String buildRedisKey(String email) {
        return "pwd:verify:" + email;
    }
}
