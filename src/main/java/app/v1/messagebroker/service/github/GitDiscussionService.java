package app.v1.messagebroker.service.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitDiscussionService {
    @Value("${api.key.github}")
    private String githubToken;

    private final String githubGraphqlEndpoint = "https://api.github.com/graphql";

    public String createDiscussion(String repositoryId, String categoryId, String title, String body) {
        RestTemplate restTemplate = new RestTemplate();

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubToken);
        headers.set("Content-Type", "application/json");

        // GraphQL query
        String query = String.format(
                "{ \"query\": \"mutation { createDiscussion(input: { repositoryId: \\\"%s\\\", categoryId: \\\"%s\\\", title: \\\"%s\\\", body: \\\"%s\\\" }) { discussion { id title url } } }\" }",
                repositoryId, categoryId, title, body
        );

        // HTTP request
        HttpEntity<String> entity = new HttpEntity<>(query, headers);

        // Execute the request
        ResponseEntity<String> response = restTemplate.postForEntity(githubGraphqlEndpoint, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody(); // Return the response body
        } else {
            throw new RuntimeException("Failed to create discussion: " + response.getBody());
        }
    }

}
