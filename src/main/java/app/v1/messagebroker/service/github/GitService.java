package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.cloudflare.CloudFlareService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class GitService {
    private final GitCommitsService gitCommitsService;
    private final ObjectMapper objectMapper;
    private final CloudFlareService cloudFlareService;
    private final GitMapperService gitMapperService;
    private final GitDiscussionService gitDiscussionService;

    public GitService(GitCommitsService gitCommitsService,
                      GitMapperService gitMapperService,
                      GitDiscussionService gitDiscussionService,
                      CloudFlareService cloudFlareService) {
        this.objectMapper = new ObjectMapper();
        this.cloudFlareService = cloudFlareService;
        this.gitCommitsService = gitCommitsService;
        this.gitMapperService = gitMapperService;
        this.gitDiscussionService = gitDiscussionService;
    }


    public Map<String, Object> fetchGitData(GitHubNotificationDto notification) {
        ResponseEntity<String> response = gitCommitsService.getCommits(notification);

        try {
            // Parse response into a Map
            Map<String, Object> jsonResponseGit = objectMapper.readValue(response.getBody(), Map.class);

            // Filter the "files" field
            List<Map<String, Object>> filteredFiles = gitMapperService.filterFields(jsonResponseGit);

            // Convert filtered files back to JSON
            String filteredJsonString = objectMapper.writeValueAsString(filteredFiles);

            System.out.println(filteredJsonString);

            Map<String, Object> responseCloudFlare = cloudFlareService.sendRequestToCloudflare(filteredJsonString.replace(" ", ""));

            // Send filtered JSON string to CloudFlare
            return responseCloudFlare;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
