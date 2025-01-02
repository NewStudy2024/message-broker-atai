package app.v1.messagebroker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


import java.util.Map;


@Service
public class GitService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    public GitService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> fetchGitData() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        System.out.println(apiUrl);

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class, entity);
        try {
            Map<String, Object> jsonResponse = objectMapper.readValue(response.getBody(), Map.class);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonResponse));
            return jsonResponse;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }

}
