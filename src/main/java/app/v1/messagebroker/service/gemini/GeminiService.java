package app.v1.messagebroker.service.gemini;

import app.v1.messagebroker.DTO.GeminiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class GeminiService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private final String apiBaseUrl;

    public GeminiService(WebClient webClient,
                         ObjectMapper objectMapper,
                         @Value("${api.url.gemini}") String apiBaseUrl) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.apiBaseUrl = apiBaseUrl;
    }

    public GeminiResponseDto sendRequestGemini(String data) {
        Map<String, Object> requestBody = Map.of("data", data);

        try {
            String response = webClient.post()
                    .uri(apiBaseUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Error request to Gemini API: " + errorBody))))
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(response, GeminiResponseDto.class);
        } catch (Exception e) {
            System.err.println("Error during request to Gemini");
            e.printStackTrace();
            throw new RuntimeException("Request Error", e);
        }
    }
}
