package com.example.moneyway.user.controller;

import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.user.dto.request.UpdateNicknameRequest;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.UserResponse;
import com.example.moneyway.user.service.MyPageService;
import com.example.moneyway.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final MyPageService myPageService;

    /**
     * ✅ 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        UserResponse response = UserResponse.from(userService.findByEmail(principal.getUsername()));
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 닉네임 변경
     */
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @Valid @RequestBody UpdateNicknameRequest request) {
        myPageService.updateNickname(principal.getUsername(), request.getNewNickname());
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

    /**
     * ✅ 회원 탈퇴
     */
    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdrawUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        myPageService.withdrawUser(principal.getUsername());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }

    /**
     * ✅ 내가 작성한 글 목록 조회
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<PostSummaryResponse>> getMyPosts(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myPosts = myPageService.getMyPosts(principal.getUsername(), pageable);
        return ResponseEntity.ok(myPosts);
    }

    /**
     * ✅ 내가 스크랩한 글 목록 조회
     */
    @GetMapping("/scraps")
    public ResponseEntity<Page<PostSummaryResponse>> getMyScraps(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myScraps = myPageService.getMyScraps(principal.getUsername(), pageable);
        return ResponseEntity.ok(myScraps);
    }
}