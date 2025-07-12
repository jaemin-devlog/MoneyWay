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

    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    private static final String VERIFICATION_KEY_PREFIX = "pwd:verify:";
    private static final String VERIFIED_STATUS_KEY_PREFIX = "verified:";
    private static final Duration CODE_TTL = Duration.ofMinutes(5);
    private static final Duration VERIFIED_STATUS_TTL = Duration.ofMinutes(10);

    /**
     * ✅ 인증코드를 생성하고 이메일로 발송한 뒤, Redis에 저장
     * @param email 인증 대상 이메일 주소
     */
    public void sendCode(String email) {
        String code = generateCode();
        String redisKey = buildRedisKey(email);

        redisTemplate.opsForValue().set(redisKey, code, CODE_TTL);
        mailService.sendVerificationCodeHtml(email, code);
    }

    /**
     * ✅ 사용자가 입력한 인증코드를 검증
     * @param email 사용자 이메일
     * @param inputCode 사용자가 입력한 인증코드
     */
    public void verifyCode(String email, String inputCode) {
        String redisKey = buildRedisKey(email);
        String savedCode = redisTemplate.opsForValue().get(redisKey);

        if (savedCode == null) {
            throw new CustomUserException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (!savedCode.equals(inputCode)) {
            throw new CustomUserException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        redisTemplate.delete(redisKey);
        redisTemplate.opsForValue().set(VERIFIED_STATUS_KEY_PREFIX + email, "true", VERIFIED_STATUS_TTL);
    }

    /**
     * 비밀번호 재설정을 위해 이메일 인증이 완료되었는지 확인합니다.
     *    확인 후에는 재사용을 막기 위해 인증 상태를 삭제합니다.
     * @param email 검증할 이메일
     */
    public void checkVerificationStatus(String email) {
        String verifiedKey = VERIFIED_STATUS_KEY_PREFIX + email;
        String isVerified = redisTemplate.opsForValue().get(verifiedKey);

        // ErrorCode에 EMAIL_NOT_VERIFIED("이메일 인증이 필요합니다.") 추가 필요
        if (!"true".equals(isVerified)) {
            throw new CustomUserException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 성공적으로 확인 후, 재사용을 막기 위해 인증 상태 키를 삭제
        redisTemplate.delete(verifiedKey);
    }

    /**
     * ✅ 인증코드 생성 로직 (5자리 숫자)
     * @return 00000 ~ 99999 사이 문자열 코드
     */
    private String generateCode() {
        return String.format("%05d", new Random().nextInt(100000));
    }

    /**
     * ✅ 이메일 기반 Redis 키 생성
     * @param email 사용자 이메일
     * @return Redis Key 형식
     */
    private String buildRedisKey(String email) {
        return VERIFICATION_KEY_PREFIX + email;
    }
}