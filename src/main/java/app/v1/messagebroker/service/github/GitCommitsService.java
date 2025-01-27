package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitCommitsService {
    private final RestTemplate restTemplate;

    @Value("${api.url.github}")
    private String apiUrl;

    @Value("${api.key.github}")
    private String apiKey;

    public GitCommitsService() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> getCommits(GitHubNotificationDto notification) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Forming the URL for the GitHub Compare API
        String apiUrlCompare = apiUrl + notification.getRepository() +
                "/compare/" + notification.getPreviousCommit() +
                "..." + notification.getCommit();

        System.out.println("Git compare URL: " + apiUrlCompare);

        // Making a request to GitHub and returning ResponseEntity<String> as is
        return restTemplate.exchange(
                apiUrlCompare,
                HttpMethod.GET,
                entity,
                String.class
        );
    }
}
