package pl.serweryminecraft24.sm24VoterJava.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.bukkit.entity.Player;
import pl.serweryminecraft24.sm24VoterJava.api.ApiResponse;
import pl.serweryminecraft24.sm24VoterJava.config.PluginConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class VoteApiService {
    private final String apiToken;
    private final Gson gson = new Gson();
    private final String customUserAgent = "Sm24VoterJava/1.0.0";


    public VoteApiService(PluginConfig config) {
        this.apiToken = config.getApiToken();
    }

    public CompletableFuture<ApiResponse> fetchVoteLink() {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://serweryminecraft24.pl/api/verify-vote-plugin/link-glosujacy";

            java.util.Map<String, String> data = new java.util.HashMap<>();
            data.put("serverToken", this.apiToken);

            String jsonBody = gson.toJson(data);

            return executePostRequest(url, jsonBody);
        });
    }

    public CompletableFuture<ApiResponse> verifyVote(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            String url = "https://serweryminecraft24.pl/api/verify-vote-plugin"; // Zmieniony URL na poprawny z API

            java.util.Map<String, String> data = new java.util.HashMap<>();
            data.put("serverToken", this.apiToken);
            data.put("nickname", player.getName());

            String jsonBody = gson.toJson(data);

            return executePostRequest(url, jsonBody);
        });
    }

    private ApiResponse executePostRequest(String requestUrl, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(requestUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");

            conn.setRequestProperty("User-Agent", this.customUserAgent);

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setDoOutput(true);


            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            InputStream inputStream = (responseCode >= 200 && responseCode <= 299)
                    ? conn.getInputStream() : conn.getErrorStream();

            if (inputStream == null) {
                return new ApiResponse(false, "API zwróciło błąd HTTP " + responseCode + ", ale bez dodatkowych informacji.");
            }

            String responseText = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            if (responseCode >= 200 && responseCode <= 299) {
                return gson.fromJson(responseText, ApiResponse.class);
            } else {
                try {
                    ApiResponse errorResponse = gson.fromJson(responseText, ApiResponse.class);
                    if (errorResponse != null && errorResponse.getMessage() != null && !errorResponse.getMessage().trim().isEmpty()) {
                        return new ApiResponse(false, errorResponse.getMessage());
                    }
                } catch (JsonSyntaxException e) {
                    return new ApiResponse(false, responseText);
                }
                return new ApiResponse(false, "API zwróciło nieoczekiwany błąd (HTTP " + responseCode + ").");
            }
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas komunikacji z API: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}