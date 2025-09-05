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
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS) // 응답 대기 시간
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o-mini");
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content", "You are a helpful travel planner."))
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
            return resJson.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
