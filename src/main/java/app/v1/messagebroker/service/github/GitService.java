package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GeminiResponseDto;
import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.gemini.GeminiService;
import app.v1.messagebroker.service.github.discussion.GitDiscussionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class GitService {
    private final GitCommitsService gitCommitsService;
    private final ObjectMapper objectMapper;
    private final GitMapperService gitMapperService;
    private final GitDiscussionService gitDiscussionService;
    private final GeminiService geminiService;

    public GitService(GitCommitsService gitCommitsService,
                      GitMapperService gitMapperService,
                      GitDiscussionService gitDiscussionService,
                      GeminiService geminiService) {
        this.objectMapper = new ObjectMapper();
        this.gitCommitsService = gitCommitsService;
        this.gitMapperService = gitMapperService;
        this.gitDiscussionService = gitDiscussionService;
        this.geminiService = geminiService;
    }

    public void fetchGitData(GitHubNotificationDto notification) {
        ResponseEntity<String> response = gitCommitsService.getCommits(notification);

        try {
            // Parse response into a Map
            Map<String, Object> jsonResponseGit = objectMapper.readValue(response.getBody(), Map.class);

            // Filter the "files" field
            List<Map<String, Object>> filteredFiles = gitMapperService.filterFields(jsonResponseGit);

            // Convert filtered files back to JSON
            String filteredJsonString = objectMapper.writeValueAsString(filteredFiles).replace(" ", "");

            // Requesting Gemini Ai Service
            GeminiResponseDto responseGemini = geminiService.sendRequestGemini(filteredJsonString);

            // Creating Discussions
            gitDiscussionService.createDiscussion(notification, responseGemini.getTitle(), responseGemini.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
