// C:/Users/jjm02/IdeaProjects/MoneyWay1/src/main/java/com/example/moneyway/user/dto/response/EmailCodeResponse.java
package com.example.moneyway.user.dto.response;

import lombok.Getter;

/**
 * 이메일 인증코드 검증 완료 응답 DTO
 */
@Getter
public class EmailCodeResponse {
    private final boolean success = true;
    private final String message = "이메일 인증이 완료되었습니다.";
}