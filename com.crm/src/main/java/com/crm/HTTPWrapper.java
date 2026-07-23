package com.crm;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HTTPWrapper {

    private final HttpClient client;
    private final ObjectMapper mapper;

    public HTTPWrapper() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5)) // Prevents infinite hanging connections
                .build();
        this.mapper = new ObjectMapper();
    }

    public <T> T get(String url, Class<T> type) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("[HTTPWrapper] GET failed with status: " + response.statusCode());
                return null;
            }

            if (response.body() == null || response.body().isBlank()) {
                return null;
            }

            return mapper.readValue(response.body(), type);
        } catch (Exception e) {
            System.out.println("[HTTPWrapper] GET request error: " + e.getMessage());
            return null;
        }
    }

    public <T> T post(String url, Object body, Class<T> type) {
        try {
            String jsonBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                System.out.println("[HTTPWrapper] POST failed with status: " + response.statusCode());
                return null;
            }

            if (response.body() == null || response.body().isBlank()) {
                return null;
            }

            return mapper.readValue(response.body(), type);
        } catch (Exception e) {
            System.out.println("[HTTPWrapper] POST request error: " + e.getMessage());
            return null;
        }
    }
}
