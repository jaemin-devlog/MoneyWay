package com.example.moneyway.user.controller;

import com.example.moneyway.user.domain.User;
import com.example.moneyway.user.dto.request.UpdateNicknameRequest;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인한 사용자의 정보 관리(마이페이지)를 전담하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserService userService;

    /**
     * ✅ 내 정보 조회
     * @AuthenticationPrincipal을 통해 인증된 사용자 객체를 직접 받아 사용합니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        // Spring Security의 principal에서 이메일을 꺼내 우리 시스템의 User 엔티티를 조회
        User user = userService.findByEmail(principal.getUsername());
        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * ✅ 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @Valid @RequestBody UpdateNicknameRequest request) {
        User user = userService.findByEmail(principal.getUsername());
        userService.updateNickname(user.getId(), request.getNewNickname());
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

    /**
     * ✅ 회원 탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdrawUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        User user = userService.findByEmail(principal.getUsername());
        userService.withdrawUser(user.getId());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }
}