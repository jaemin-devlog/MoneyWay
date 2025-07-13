package com.example.moneyway.user.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AvatarService {

    private static final String[] COLORS = {
            // 머티리얼 & 플랫 UI 색상
            "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", // 선홍색, 자홍색, 보라, 라벤더, 인디고
            "#2196F3", "#03A9F4", "#00BCD4", "#009688", "#4CAF50", // 파랑, 하늘, 시안, 청록, 초록
            "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", // 연두, 라임, 노랑, 주황노랑, 오렌지
            "#FF5722", "#795548", "#9E9E9E", "#607D8B", "#000000", // 붉은 주황, 갈색, 회색, 청회색, 검정

            // 파스텔 색상
            "#FFB3BA", "#FFDFBA", "#FFFFBA", "#BAFFC9", "#BAE1FF", // 연핑크, 살구, 연노랑, 민트, 하늘
            "#D5AAFF", "#A3E4DB", "#FFD3E0", "#C5E1A5", "#FFF59D", // 연보라, 옥색, 장미빛, 연연두, 밝은노랑

            // 시원한 톤의 색상
            "#6EC6FF", "#81D4FA", "#4DD0E1", "#80CBC4", "#A5D6A7", // 시원한 하늘, 연하늘, 연시안, 연청록, 연초록
            "#C5E1A5", "#E6EE9C", "#FFF59D", "#FFCC80", "#FFAB91", // 연연두, 레몬, 노랑, 살구주황, 복숭아빛

            // 짙고 선명한 톤의 색상
            "#D32F2F", "#C2185B", "#7B1FA2", "#512DA8", "#303F9F", // 진빨강, 자홍, 짙은보라, 어두운보라, 남보라
            "#1976D2", "#0288D1", "#0097A7", "#00796B", "#388E3C", // 짙은파랑, 청록, 어두운청록, 딥민트, 딥그린
            "#689F38", "#AFB42B", "#FBC02D", "#FFA000", "#F57C00", // 머스타드그린, 황토빛, 금색, 짙은주황, 호박색
            "#E64A19", "#5D4037", "#616161", "#455A64"            // 붉은갈색, 진갈색, 어두운회색, 청회색
    };


    /**
     * ✅ 이메일을 기반으로 고유한 SVG 프로필 이미지를 생성합니다.
     * @param email 사용자의 이메일
     * @return "data:image/svg+xml,..." 형태의 문자열
     */
    public String generateAvatar(String email) {
        // 1. 이메일에서 첫 글자(이니셜) 추출
        String initial = getInitial(email);

        // 2. 이메일 해시코드를 기반으로 일관된 배경색 선택
        String color = selectColor(email);

        String svg = """
            <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" viewBox="0 0 100 100">
                <rect width="100" height="100" fill="%s"/>
                <text x="50" y="50" font-family="'Arial', sans-serif" font-size="50" fill="#FFFFFF" text-anchor="middle" dy=".3em">%s</text>
            </svg>
            """.formatted(color, initial);

        String encodedSvg = URLEncoder.encode(svg, StandardCharsets.UTF_8);
        String dataUri = "data:image/svg+xml," + encodedSvg.replace("+", "%20");

        return dataUri;
    }

    /**
     *  이메일에서 첫 글자를 추출합니다.
     */
    private String getInitial(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "?";
        }
        return email.trim().substring(0, 1).toUpperCase();
    }

    /**
     * 이메일을 기반으로 색상을 선택합니다.
     */
    private String selectColor(String email) {
        if (email == null || email.isEmpty()) {
            return COLORS[0];
        }
        // 이메일의 해시코드를 사용하여 항상 동일한 색상을 반환하도록 함
        int hashCode = Math.abs(email.hashCode());
        return COLORS[hashCode % COLORS.length];
    }
}