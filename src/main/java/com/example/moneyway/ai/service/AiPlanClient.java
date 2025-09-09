package com.example.moneyway.ai.service;

import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AiPlanClient {

    private final String API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public AiPlanClient(@Value("${openai-api-key}") String apiKey) {
        this.API_KEY = apiKey;
    }

    public String requestPlan(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o-mini");
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content",
                        "You are a travel planner AI. Respond ONLY with valid JSON. " +
                                "Do not include +, comments, markdown, or explanations."))
                .put(new JSONObject().put("role", "user").put("content", prompt))
        );
        json.put("temperature", 0.7);

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("API Error: " + response);

            String responseBody = response.body().string();
            JSONObject resJson = new JSONObject(responseBody);
            String raw = resJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            // JSON 부분만 추출
            int start = raw.indexOf("{");
            int end = raw.lastIndexOf("}");
            if (start >= 0 && end > start) {
                String cleaned = raw.substring(start, end + 1);

                // 불필요한 기호 제거 (+, backtick 등)
                cleaned = cleaned.replaceAll("[+`]", "");

                return cleaned;
            }
            throw new IllegalStateException("AI 응답이 JSON 형식이 아님: " + raw);
        }
    }

}
