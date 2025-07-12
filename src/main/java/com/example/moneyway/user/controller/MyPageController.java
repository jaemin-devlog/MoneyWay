package com.example.moneyway.user.controller;

import com.example.moneyway.auth.userdetails.UserDetailsImpl;
import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.user.dto.request.ChangePasswordRequest;
import com.example.moneyway.user.dto.request.UpdateNicknameRequest;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.MyPageResponse;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;

    @GetMapping
    public ResponseEntity<MyPageResponse> getMyPageInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        MyPageResponse response = myPageService.getMyPageInfo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponse response = UserResponse.from(userService.findByEmail(userDetails.getUsername()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostSummaryResponse>> getMyPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myPosts = myPageService.getMyPosts(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(myPosts);
    }

    @GetMapping("/scraps")
    public ResponseEntity<Page<PostSummaryResponse>> getMyScraps(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myScraps = myPageService.getMyScraps(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(myScraps);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateNicknameRequest request) {
        myPageService.updateNickname(userDetails.getUsername(), request.getNewNickname());
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

    @PatchMapping("/password")
    public ResponseEntity<MessageResponse> changePassword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {

        myPageService.changePassword(
                userDetails.getUsername(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return ResponseEntity.ok(new MessageResponse("비밀번호가 성공적으로 변경되었습니다."));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdrawUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        myPageService.withdrawUser(userDetails.getUsername());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }
}