package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.cloudflare.CloudFlareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GitService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CloudFlareService cloudFlareService;

    @Value("${api.url.github}")
    private String apiUrl;

    @Value("${api.key.github}")
    private String apiKey;

    public GitService(CloudFlareService cloudFlareService) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.cloudFlareService = cloudFlareService;
    }

    public static List<Map<String, Object>> filterFields(Map<String, Object> data) {
        List<Map<String, Object>> filteredList = new ArrayList<>();
        List<String> fieldsToKeep = List.of("filename", "changes", "patch");

        // Check if the map contains the "files" field
        if (data.containsKey("files") && data.get("files") instanceof List) {
            List<?> files = (List<?>) data.get("files");

            for (Object file : files) {
                if (file instanceof Map) {
                    Map<String, Object> fileMap = (Map<String, Object>) file;
                    Map<String, Object> filteredMap = new HashMap<>();

                    // Keep only the required fields
                    for (String field : fieldsToKeep) {
                        if (fileMap.containsKey(field)) {
                            filteredMap.put(field, fileMap.get(field));
                        }
                    }
                    filteredList.add(filteredMap);
                }
            }
        }
        return filteredList;
    }

    public Map<String, Object> fetchGitData(GitHubNotificationDto notification) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String apiUrlCompare = apiUrl + notification.getRepository() + "/compare/" + notification.getPreviousCommit() + "..." + notification.getCommit();

        System.out.println(apiUrlCompare);

        ResponseEntity<String> response = restTemplate.getForEntity(apiUrlCompare, String.class, entity);
        try {
            // Parse response into a Map
            Map<String, Object> jsonResponseGit = objectMapper.readValue(response.getBody(), Map.class);

            // Filter the "files" field
            List<Map<String, Object>> filteredFiles = filterFields(jsonResponseGit);

            // Convert filtered files back to JSON
            String filteredJsonString = objectMapper.writeValueAsString(filteredFiles);

            System.out.println(filteredJsonString);

            // Send filtered JSON string to CloudFlare
            return cloudFlareService.sendRequestToCloudflare(filteredJsonString.replace(" ", ""));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
