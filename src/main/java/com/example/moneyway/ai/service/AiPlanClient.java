package com.example.moneyway.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
        json.put("model", "gpt-4o");
        json.put("stream", true);  // 스트리밍
        json.put("messages", new org.json.JSONArray()
                .put(new JSONObject().put("role", "system").put("content",
                        "You are a travel planner AI. Respond ONLY with valid JSON."
                                + " Do not include explanations, text, or markdown fences." ))
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

        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data)) {
                    latch.countDown();
                } else {
                    try {
                        JSONObject obj = new JSONObject(data);
                        String delta = obj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("delta")
                                .optString("content");
//                        log.debug("Delta chunk: {}", delta);
                        sb.append(delta);
                    } catch (Exception e) {
                        log.error("Chunk parse error: {}", data, e);
                    }
                }
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.error("Stream failed", t);
                latch.countDown();
            }
        });

        latch.await(120, TimeUnit.SECONDS);

        String content = sb.toString().trim();
        log.info("Final AI content: {}", content);

        int firstBrace = content.indexOf('{');
        int lastBrace  = content.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            content = content.substring(firstBrace, lastBrace + 1);
        }
        log.info("Cleaned AI content: {}", content);

        // JSON 검증
        try {
            new ObjectMapper().readTree(content);
        } catch (Exception e) {
            log.error("AI 응답 JSON 파싱 실패. 원문: {}", content, e);
            throw new RuntimeException("AI 응답이 올바른 JSON이 아님");
        }
        return content;
        // JSON 검증
    }

}


