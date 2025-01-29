package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GeminiResponseDto;
import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.gemini.GeminiService;
import app.v1.messagebroker.service.github.discussion.GitDiscussionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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


    public String fetchGitData(GitHubNotificationDto notification) {
        try {
            ResponseEntity<String> response = gitCommitsService.getCommits(notification);

            // Parse response into a Map
            Map<String, Object> jsonResponseGit = objectMapper.readValue(response.getBody(), Map.class);

            // Filter the "files" field
            List<Map<String, Object>> filteredFiles = gitMapperService.filterFields(jsonResponseGit);

            // Convert filtered files back to JSON
            return objectMapper.writeValueAsString(filteredFiles).replace(" ", "");
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void createDiscussionWithRetry(String gitCompareResponse, GitHubNotificationDto notification, int attemptsLeft){
        if (attemptsLeft <= 0) {
            throw new RuntimeException("Failed to create discussion after multiple attempts");
        }

        try {
            // Requesting Gemini Ai Service
            GeminiResponseDto responseGemini = geminiService.sendRequestGemini(gitCompareResponse);

            // Creating Discussions
            gitDiscussionService.createDiscussion(notification, responseGemini.getTitle(), responseGemini.getBody());

        } catch (RuntimeException e) {
            System.err.println("Attempt failed: " + e.getMessage() + ". Retries left: " + (attemptsLeft - 1));
            createDiscussionWithRetry(gitCompareResponse, notification, attemptsLeft - 1);
        }
    }

    private void createDiscussion(String gitCompareResponse, GitHubNotificationDto notification){
        createDiscussionWithRetry(gitCompareResponse, notification, 2);
    }

    public void onTrigger(GitHubNotificationDto notification){
        String gitCompareResponse = fetchGitData(notification);
        createDiscussion(gitCompareResponse, notification);
    }
}
