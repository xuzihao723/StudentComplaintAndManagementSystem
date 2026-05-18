package edu.demo.scfs.service;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class ResendEmailApiClient implements EmailApiClient {
    private final RestClient restClient;
    private final String apiKey;

    public ResendEmailApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${app.email-api.resend.endpoint:https://api.resend.com/emails}") String endpoint,
            @Value("${app.email-api.resend.api-key:}") String apiKey
    ) {
        this.restClient = restClientBuilder.baseUrl(endpoint).build();
        this.apiKey = apiKey;
    }

    @Override
    public void send(String to, String subject, String text, String from) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Resend API key is not configured");
        }
        try {
            restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(Map.of(
                            "from", from,
                            "to", List.of(to),
                            "subject", subject,
                            "text", text == null ? "" : text
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException ex) {
            throw new IllegalStateException("Email API failed: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString(), ex);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Email API connection failed: " + ex.getMessage(), ex);
        }
    }
}
