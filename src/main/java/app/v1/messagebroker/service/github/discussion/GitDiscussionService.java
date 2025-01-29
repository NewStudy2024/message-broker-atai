package app.v1.messagebroker.service.github.discussion;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GitDiscussionService {

    private final WebClient webClient;
    private final String githubToken;
    private final GitGraphQlFormater graphQlFormater;
    private final GitDiscussionPropertiesService gitDiscussionPropertiesService;
    private final String githubGraphqlEndpoint;

    public GitDiscussionService(GitGraphQlFormater graphQlFormater,
                                GitDiscussionPropertiesService gitDiscussionPropertiesService,
                                @Value("${api.key.github}") String githubToken,
                                @Value("${api.url.github.graphql}") String githubGraphqlEndpoint,
                                WebClient.Builder webClientBuilder) {
        this.graphQlFormater = graphQlFormater;
        this.gitDiscussionPropertiesService = gitDiscussionPropertiesService;
        this.githubToken = githubToken;
        this.githubGraphqlEndpoint = githubGraphqlEndpoint;
        this.webClient = webClientBuilder.build();
    }

    public void createDiscussion(GitHubNotificationDto notification, String title, String body) {
        // Prepare the GraphQL mutation payload
        String payload = graphQlFormater.formatCreateDiscussionMutation(
                gitDiscussionPropertiesService.getRepositoryId(notification),
                gitDiscussionPropertiesService.getGeneralCategory(notification),
                title,
                body
        );

        // Execute the request *synchronously* by using block()
        String response = webClient.post()
                .uri(githubGraphqlEndpoint)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.USER_AGENT, "MySpringApp/1.0")
                .bodyValue(payload)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException("Failed to create discussion: " + errorBody))
                )
                .bodyToMono(String.class)
                .block(); // <-- Blocking here, so no Mono is returned outside

        if (response == null || response.isBlank()) {
            throw new RuntimeException("Failed to create discussion: empty response");
        }

        System.out.println("Discussion created successfully:\n" + response);
    }
}
