package com.example.moneyway.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// ... import 동일

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
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-4o-mini");
        json.put("stream", true);
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content",
                        "You are a travel planner AI. Respond ONLY with valid JSON. " +
                                "Do not include +, comments, markdown, or explanations."))
                .put(new JSONObject().put("role", "user").put("content", prompt))
        );
        json.put("temperature", 0.2);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        StringBuilder responseBuilder = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        EventSourceListener listener = new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data)) {
                    latch.countDown();
                    return;
                }
                try {
                    JSONObject delta = new JSONObject(data)
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("delta");

                    if (delta.has("content")) {
                        responseBuilder.append(delta.getString("content"));
                    }
                } catch (Exception e) {
                    System.err.println("Streaming parse error: " + data);
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                System.err.println("Streaming failed: " + t.getMessage());
                latch.countDown();
            }
        };

        EventSources.createFactory(client).newEventSource(request, listener);

        latch.await(60, TimeUnit.SECONDS);

        String raw = responseBuilder.toString().trim();
        int start = raw.indexOf("{");
        int end = raw.lastIndexOf("}");

        if (start >= 0 && end > start) {
            String cleaned = raw.substring(start, end + 1)
                    .replaceAll("[+`]", "");

            // ✅ 중괄호 / 대괄호 각각 보정
            cleaned = fixJsonBrackets(cleaned);

            // 최종 유효성 검증
            new ObjectMapper().readTree(cleaned);

            return cleaned;
        }
        throw new IllegalStateException("AI 응답이 JSON 형식이 아님. raw=" + raw);
    }

    /**
     * ✅ JSON 괄호 보정 유틸
     */
    private String fixJsonBrackets(String json) {
        String fixed = json;

        long openBraces = fixed.chars().filter(ch -> ch == '{').count();
        long closeBraces = fixed.chars().filter(ch -> ch == '}').count();
        while (closeBraces < openBraces) {
            fixed += "}";
            closeBraces++;
        }

        long openBrackets = fixed.chars().filter(ch -> ch == '[').count();
        long closeBrackets = fixed.chars().filter(ch -> ch == ']').count();
        while (closeBrackets < openBrackets) {
            fixed += "]";
            closeBrackets++;
        }

        return fixed;
    }
}

