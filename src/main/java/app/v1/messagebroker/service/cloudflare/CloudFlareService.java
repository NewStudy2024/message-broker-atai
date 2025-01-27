package app.v1.messagebroker.service.cloudflare;

import app.v1.messagebroker.DTO.CloudFlareDto;
import app.v1.messagebroker.service.github.discussion.GitDiscussionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CloudFlareService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final GitDiscussionService gitDiscussionService;

    @Value("${api.url.cloudflare}")
    private String apiBaseUrl;

    @Value("${api.key.cloudflare}")
    private String apiToken;

    @Value("${api.message.cloudflare}")
    private String systemMessage;

    public CloudFlareService(ObjectMapper objectMapper, GitDiscussionService gitDiscussionService) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.gitDiscussionService = gitDiscussionService;
    }

    public CloudFlareDto sendRequestToCloudflare(String data) {
        try {
            List<Map<String, Object>> messages = List.of(
                    Map.of("role", "system", "content", systemMessage),
                    Map.of("role", "user", "content", "Describe the changes made in the project. Here is the list of updates: " + data)
            );

            System.out.println(data);

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiToken);
            headers.set("Content-Type", "application/json");

            // Prepare request body
            Map<String, Object> requestBody = Map.of("messages", messages);

            // Create HTTP Entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(apiBaseUrl, requestEntity, String.class);
            CloudFlareDto apiResponse = objectMapper.readValue(response.getBody(), CloudFlareDto.class);

            // Parse the response body into a Map
            return apiResponse;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send request to Cloudflare", e);
        }
    }
}
