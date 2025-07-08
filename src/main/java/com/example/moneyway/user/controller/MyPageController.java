package com.example.moneyway.user.controller;

import com.example.moneyway.community.dto.response.PostSummaryResponse;
import com.example.moneyway.user.dto.request.UpdateNicknameRequest;
import com.example.moneyway.user.dto.response.MessageResponse;
import com.example.moneyway.user.dto.response.MyPageResponse; // [추가]
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

    /**
     * ✅ [신규] 마이페이지 통합 정보 조회 (초기 로딩용)
     * 사용자 정보, 내가 쓴 글(첫 페이지), 내가 스크랩한 글(첫 페이지)을 한번에 반환합니다.
     */
    @GetMapping
    public ResponseEntity<MyPageResponse> getMyPageInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        MyPageResponse response = myPageService.getMyPageInfo(principal.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 내 정보 조회 (개별 조회용)
     * 이 엔드포인트는 프로필 수정 화면 등에서 단독으로 사용자 정보만 필요할 때 유용하게 사용할 수 있습니다.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        UserResponse response = UserResponse.from(userService.findByEmail(principal.getUsername()));
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ 내가 작성한 글 목록 조회 (페이지네이션용)
     * '더 보기' 기능이나 '내가 쓴 글' 탭 화면에서 사용합니다.
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<PostSummaryResponse>> getMyPosts(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myPosts = myPageService.getMyPosts(principal.getUsername(), pageable);
        return ResponseEntity.ok(myPosts);
    }

    /**
     * ✅ 내가 스크랩한 글 목록 조회 (페이지네이션용)
     * '더 보기' 기능이나 '내가 스크랩한 글' 탭 화면에서 사용합니다.
     */
    @GetMapping("/scraps")
    public ResponseEntity<Page<PostSummaryResponse>> getMyScraps(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryResponse> myScraps = myPageService.getMyScraps(principal.getUsername(), pageable);
        return ResponseEntity.ok(myScraps);
    }

    // --- 사용자 정보 수정 및 탈퇴 API ---
    @PatchMapping("/nickname")
    public ResponseEntity<MessageResponse> updateNickname(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User principal,
            @Valid @RequestBody UpdateNicknameRequest request) {
        myPageService.updateNickname(principal.getUsername(), request.getNewNickname());
        return ResponseEntity.ok(new MessageResponse("닉네임이 성공적으로 변경되었습니다."));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<MessageResponse> withdrawUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        myPageService.withdrawUser(principal.getUsername());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }
}