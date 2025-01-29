package app.v1.messagebroker.service.github;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitCommitsService {

    private final WebClient webClient;
    private final String apiUrl;
    private final String apiKey;

    public GitCommitsService(@Value("${api.url.github}") String apiUrl,
                             @Value("${api.key.github}") String apiKey,
                             WebClient.Builder webClientBuilder) {
        // Build a WebClient instance (can set global headers, base URL, etc.)
        this.webClient = webClientBuilder.build();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public ResponseEntity<String> getCommits(GitHubNotificationDto notification) {
        System.out.println(" -- Git commits Service -- ");

        // Construct the GitHub Compare API URL
        String compareUrl = apiUrl + notification.getRepository()
                + "/compare/" + notification.getPreviousCommit()
                + "..." + notification.getCommit();

        System.out.println("Git compare URL: " + compareUrl);

        // Perform the GET request, returning a ResponseEntity<String>
        return webClient
                .get()
                .uri(compareUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .toEntity(String.class)
                .block();
    }
}
