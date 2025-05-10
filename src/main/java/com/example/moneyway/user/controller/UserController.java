package com.example.moneyway.user.controller;

import com.example.moneyway.user.dto.FirebaseSignupRequest;
import com.example.moneyway.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 🔐 Firebase 인증 기반 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@AuthenticationPrincipal String firebaseUid,
                                       @RequestBody FirebaseSignupRequest request) {
        System.out.println("📥 /signup 진입 (principal 기반)");
        System.out.println("🔥 Firebase UID: " + firebaseUid);

        userService.registerUser(firebaseUid, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 👤 현재 로그인한 사용자 프로필 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal String firebaseUid) {
        System.out.println("👤 /me 진입 (principal 기반)");
        System.out.println("🔥 Firebase UID: " + firebaseUid);

        return ResponseEntity.ok(userService.getMyProfile(firebaseUid));
    }
}
