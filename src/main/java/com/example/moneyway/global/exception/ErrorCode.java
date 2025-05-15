package com.example.moneyway.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ================= USER =================

    // ================= USER: 회원가입 =================
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다. 다른 이메일을 사용해주세요."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다. 다른 닉네임을 입력해주세요."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다. 예: example@email.com"),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 8자 이상이어야 합니다."),
    EMPTY_SIGNUP_FIELD(HttpStatus.BAD_REQUEST, "이메일, 비밀번호, 닉네임을 모두 입력해주세요."),

    // ================= USER: 로그인 =================
    EMAIL_NOT_FOUND(HttpStatus.UNAUTHORIZED, "입력하신 이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다. 다시 입력해주세요."),
    KAKAO_ACCOUNT_LOGIN(HttpStatus.BAD_REQUEST, "해당 이메일은 카카오 로그인 전용 계정입니다. 카카오 로그인을 이용해주세요."),
    EMPTY_LOGIN_FIELD(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 모두 입력해주세요."),

    // ================= USER: 내 정보 =================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다. 로그인 후 다시 시도해주세요."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // ================= USER: 비밀번호 재설정 =================
    PASSWORD_RESET_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "입력하신 이메일로 가입된 계정이 없습니다."),
    PASSWORD_RESET_NAME_MISMATCH(HttpStatus.BAD_REQUEST, "입력한 이름이 계정 정보와 일치하지 않습니다."),
    RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호 재설정 시간이 만료되었습니다. 다시 요청해주세요."),
    PASSWORD_SAME_AS_BEFORE(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호는 사용할 수 없습니다."),
    NEW_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "새로운 비밀번호를 입력해주세요."),
    INVALID_PASSWORD_RESET_REQUEST(HttpStatus.BAD_REQUEST, "비밀번호 재설정 요청이 잘못되었습니다."),
    

    // ================= USER: 이메일/닉네임 중복 확인 =================
    EMAIL_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 중복 확인 중 오류가 발생했습니다."),
    NICKNAME_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "닉네임 중복 확인 중 오류가 발생했습니다."),
    EMPTY_EMAIL_FOR_CHECK(HttpStatus.BAD_REQUEST, "중복 확인할 이메일을 입력해주세요."),
    EMPTY_NICKNAME_FOR_CHECK(HttpStatus.BAD_REQUEST, "중복 확인할 닉네임을 입력해주세요."),

    // ================= USER: 인증 관련 =================
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 정보가 만료되었습니다. 다시 로그인해주세요."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다. 다시 로그인해주세요."),

    // ================= USER: 기타 =================
    UNKNOWN_USER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 처리 중 예기치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),









    // ================= REVIEW =================
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."),
    UNAUTHORIZED_REVIEW_ACCESS(HttpStatus.FORBIDDEN, "해당 리뷰에 대한 권한이 없습니다."),
    DUPLICATE_REVIEW(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성했습니다."),

    // ================= PLAN =================
    PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "여행 계획을 찾을 수 없습니다."),
    INVALID_PLAN_ACCESS(HttpStatus.FORBIDDEN, "이 계획에 접근할 수 없습니다."),

    // ================= PLACE =================
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "장소 정보를 찾을 수 없습니다."),
    DUPLICATE_PLACE(HttpStatus.BAD_REQUEST, "이미 등록된 장소입니다."),

    // ================= COMMON =================
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
