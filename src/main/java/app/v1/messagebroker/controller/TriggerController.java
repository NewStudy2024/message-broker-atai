package app.v1.messagebroker.controller;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.github.GitService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/api/trigger")
@Tag(name = "Broker trigger", description = "Trigger endpoint")
public class TriggerController {

    private final GitService gitService;

    public TriggerController(GitService gitService) {
        this.gitService = gitService;
    }

    @Operation(
            summary = "Trigger Git data fetch",
            description = "Fetches Git data based on the provided GitHubNotificationDto."
    )

    @PostMapping("/test-event")
    public ResponseEntity<String> triggerTestEvent() {
        // Можно эмулировать GitHubNotificationDto с фиктивными данными
        GitHubNotificationDto mockDto = new GitHubNotificationDto();
        mockDto.setRepository("example/repo");
        mockDto.setCommitId("123abc");
        mockDto.setPusher("TestUser");

        try {
            gitService.onTrigger(mockDto);
            return ResponseEntity.ok("Test event processed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Test event failed: " + e.getMessage());
        }
    }

    @PostMapping("/fetch")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> triggerGitFetch(
            @Parameter(description = "Payload containing GitHub repository and commit details", required = true)
            @RequestBody GitHubNotificationDto gitHubNotificationDto) {
        try {
            gitService.onTrigger(gitHubNotificationDto);
            return ResponseEntity.ok("Discussion created successfully");
        } catch (RuntimeException ex) {
            // Log the error if necessary
            System.err.println("Error creating discussion: " + ex.getMessage());
            // Return a custom HTTP status with a message instead of propagating the error
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to create discussion: " + ex.getMessage());
        }
    }
}
