package app.v1.messagebroker.service.github.discussion;
import app.v1.messagebroker.DTO.GitHubNotificationDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GitDiscussionPropertiesService {
    private final WebClient webClient;
    private final String githubToken;
    private final String githubGraphqlEndpoint;
    private final ObjectMapper objectMapper;

    public GitDiscussionPropertiesService(WebClient webClient, ObjectMapper objectMapper,
                                          @Value("${api.key.github}") String githubToken,
                                          @Value("${api.url.github.graphql}") String githubGraphqlEndpoint) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.githubToken = githubToken;
        this.githubGraphqlEndpoint = githubGraphqlEndpoint;
    }


    public String getRepositoryId(GitHubNotificationDto notification) {
        String repositoryIdQuery = String.format(
                "{ \"query\": \"query { repository(owner: \\\"%s\\\", name: \\\"%s\\\") { id } }\" }",
                notification.getPusher(), notification.getNameRepo());

        try {
            String response = webClient.post()
                    .uri(githubGraphqlEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + githubToken)
                    .bodyValue(repositoryIdQuery)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Error fetching repository ID: " + errorBody))))
                    .bodyToMono(String.class)
                    .block();

            JsonNode repositoryJsonNode = objectMapper.readTree(response);
            String repositoryId = repositoryJsonNode.path("data").path("repository").path("id").asText();

            if (repositoryId == null || repositoryId.isEmpty()) {
                throw new RuntimeException("Repository ID not found");
            }

            return repositoryId;

        } catch (Exception e) {
            System.err.println("Error during request to fetch repository ID");
            e.printStackTrace();
            throw new RuntimeException("Request Error", e);
        }
    }


    public String getGeneralCategory(GitHubNotificationDto notification) {
        String discussionCategoriesQuery = String.format(
                "{ \"query\": \"query { repository(owner: \\\"%s\\\", name: \\\"%s\\\") { discussionCategories(first: 10) { edges { node { id name } } } } }\" }",
                notification.getPusher(), notification.getNameRepo());



        System.out.println(discussionCategoriesQuery);
        System.out.println(notification.getNameRepo());
        System.out.println(notification.getPusher());

        try {
            String response = webClient.post()
                    .uri(githubGraphqlEndpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + githubToken)
                    .bodyValue(discussionCategoriesQuery)
                    .retrieve()
                    .onStatus(status -> !status.is2xxSuccessful(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                                            "Error fetching discussion categories: " + errorBody))))
                    .bodyToMono(String.class)
                    .block();

            JsonNode discussionJsonNode = objectMapper.readTree(response);
            JsonNode edges = discussionJsonNode.path("data").path("repository").path("discussionCategories").path("edges");

            for (JsonNode edge : edges) {
                String categoryName = edge.path("node").path("name").asText();
                if ("General".equalsIgnoreCase(categoryName)) {
                    return edge.path("node").path("id").asText(); // Return the ID of the node
                }
            }

            throw new RuntimeException("General category not found");

        } catch (Exception e) {
            //If error means discussions not activated
            System.err.println("Error during request to fetch discussion categories");
            e.printStackTrace();
            throw new RuntimeException("Request Error", e);
        }
    }
}
