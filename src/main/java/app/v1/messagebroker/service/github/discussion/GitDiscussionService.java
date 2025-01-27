package app.v1.messagebroker.service.github.discussion;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitDiscussionService {
    private final String githubToken;
    private final GitGraphQlFormater graphQlFormater;
    private final GitDiscussionPropertiesService gitDiscussionPropertiesService;
    private final String githubGraphqlEndpoint;

    public GitDiscussionService(GitGraphQlFormater graphQlFormater,
                                GitDiscussionPropertiesService gitDiscussionPropertiesService,
                                @Value("${api.key.github}") String githubToken,
                                @Value("${api.url.github.graphql}") String githubGraphqlEndpoint) {
        this.graphQlFormater = graphQlFormater;
        this.gitDiscussionPropertiesService = gitDiscussionPropertiesService;
        this.githubToken = githubToken;
        this.githubGraphqlEndpoint = githubGraphqlEndpoint;
    }



    public String createDiscussion(GitHubNotificationDto notification, String title, String body) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        headers.set("Content-Type", "application/json");
        headers.set("User-Agent", "MySpringApp/1.0");

        // GraphQL query
        String payload = graphQlFormater.formatCreateDiscussionMutation(
                gitDiscussionPropertiesService.getRepositoryId(notification),
                gitDiscussionPropertiesService.getGeneralCategory(notification),
                title,
                body
        );

        // HTTP request
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // Execute the request
        ResponseEntity<String> response = restTemplate.postForEntity(githubGraphqlEndpoint, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println(response.getBody());
            return response.getBody(); // Return the response body
        } else {
            throw new RuntimeException("Failed to create discussion: " + response.getBody());
        }
    }

}
