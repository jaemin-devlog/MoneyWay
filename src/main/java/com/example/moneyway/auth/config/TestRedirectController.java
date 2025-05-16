package com.example.moneyway.auth.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRedirectController {
    @GetMapping("/login/success")
    public String success() {
        return "로그인 성공!";
    }
}
