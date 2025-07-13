package com.example.moneyway.common.exception;

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

    // ================= USER: 비밀번호 변경 (로그인 상태) =================
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_BEFORE(HttpStatus.BAD_REQUEST, "이전과 동일한 비밀번호는 사용할 수 없습니다."),
    NEW_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "새로운 비밀번호를 입력해주세요."),

    // ================= USER: 비밀번호 재설정 (비로그인 상태) =================
    PASSWORD_RESET_EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "입력하신 이메일로 가입된 계정이 없습니다."),
    PASSWORD_RESET_NAME_MISMATCH(HttpStatus.BAD_REQUEST, "입력한 이름이 계정 정보와 일치하지 않습니다."),
    RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호 재설정 시간이 만료되었습니다. 다시 요청해주세요."),
    INVALID_PASSWORD_RESET_REQUEST(HttpStatus.BAD_REQUEST, "비밀번호 재설정 요청이 잘못되었습니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "인증코드가 올바르지 않습니다. 다시 확인해주세요."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.UNAUTHORIZED, "인증코드가 만료되었습니다. 다시 요청해주세요."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다. 인증을 먼저 진행해주세요."),

    // ================= USER: 인증 및 권한 =================
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "로그인 정보가 만료되었습니다. 다시 로그인해주세요."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 존재하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh 토큰이 만료되었습니다. 다시 로그인해주세요."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다. 로그인 후 다시 시도해주세요."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    // ================= USER: 탈퇴 =================
    ALREADY_WITHDRAWN(HttpStatus.BAD_REQUEST, "이미 탈퇴한 계정입니다."),
    USER_WITHDRAWN(HttpStatus.FORBIDDEN, "탈퇴한 사용자는 접근할 수 없습니다."),

    // ================= USER: 기타 조회 및 확인 =================
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 정보를 찾을 수 없습니다."),
    EMAIL_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 중복 확인 중 오류가 발생했습니다."),
    NICKNAME_CHECK_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "닉네임 중복 확인 중 오류가 발생했습니다."),
    EMPTY_EMAIL_FOR_CHECK(HttpStatus.BAD_REQUEST, "중복 확인할 이메일을 입력해주세요."),
    EMPTY_NICKNAME_FOR_CHECK(HttpStatus.BAD_REQUEST, "중복 확인할 닉네임을 입력해주세요."),
    UNKNOWN_USER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 처리 중 예기치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),


    // ================== AI / GPT ==================
    GPT_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 여행 계획 생성 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),


    // ================== FILE (파일) ==================
    FILE_IS_EMPTY(HttpStatus.BAD_REQUEST, "업로드된 파일이 비어있습니다."),
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 처리 중 오류가 발생했습니다. 파일 형식을 확인해주세요."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "파일 형식이 올바르지 않거나 필수 헤더가 누락되었습니다."),


    // ================== COMMUNITY: POST ==================
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    POST_FORBIDDEN_UPDATE(HttpStatus.FORBIDDEN, "작성자만 해당 게시글을 수정할 수 있습니다."),
    POST_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "작성자만 해당 게시글을 삭제할 수 있습니다."),
    UNAUTHORIZED_POST_ACCESS(HttpStatus.FORBIDDEN, "게시글에 대한 접근 권한이 없습니다."),
    POST_IMAGE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 이미지 저장에 실패했습니다."),
    POST_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 생성에 실패했습니다."),
    POST_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 수정에 실패했습니다."),
    POST_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 삭제에 실패했습니다."),
    INVALID_POST_INPUT(HttpStatus.BAD_REQUEST, "게시글 입력 값이 올바르지 않습니다."),
    USER_NICKNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 닉네임을 찾을 수 없습니다."),
    POST_IMAGE_URL_INVALID(HttpStatus.BAD_REQUEST, "잘못된 이미지 URL 형식입니다."),

    // ================== COMMUNITY: COMMENT ==================
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),
    COMMENT_FORBIDDEN_DELETE(HttpStatus.FORBIDDEN, "작성자만 해당 댓글을 삭제할 수 있습니다."),
    COMMENT_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 생성에 실패했습니다."),
    COMMENT_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "댓글 삭제에 실패했습니다."),
    COMMENT_INVALID_INPUT(HttpStatus.BAD_REQUEST, "댓글 입력 값이 올바르지 않습니다."),

    // ================== CART (장바구니) ==================
    ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "이미 장바구니에 담긴 장소입니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에서 해당 항목을 찾을 수 없습니다."),
    INVALID_PLACE_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 장소 타입입니다."),
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장소입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}