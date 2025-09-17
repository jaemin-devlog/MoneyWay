package com.example.moneyway.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AiPlanClient {

    private final String API_KEY;
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public AiPlanClient(@Value("${openai-api-key}") String apiKey) {
        this.API_KEY = apiKey;
    }

    public String requestPlan(String prompt) throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");
        json.put("stream", false);  // 스트리밍 해제
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content",
                        "You are a travel planner AI. Respond ONLY with valid JSON. " +
                                "Do not include explanations, comments, text, or markdown fences. " +
                                "The response MUST be a single valid JSON object starting with { and ending with }."
                ))
                .put(new JSONObject().put("role", "user").put("content", prompt))
        );
        json.put("temperature", 0.2);

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
            if (!response.isSuccessful()) {
                throw new RuntimeException("OpenAI API 호출 실패: " + response);
            }

            String raw = response.body().string();
            log.debug("Raw OpenAI response: {}", raw);

            JSONObject obj = new JSONObject(raw);
            String content = obj.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

            log.info("Final AI content: {}", content);

            int firstBrace = content.indexOf('{');
            int lastBrace  = content.lastIndexOf('}');
            if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
                content = content.substring(firstBrace, lastBrace + 1);
            }
            log.info("Cleaned AI content: {}", content);

            // JSON 검증
            new ObjectMapper().readTree(content);
            return content;
        }
    }
}
