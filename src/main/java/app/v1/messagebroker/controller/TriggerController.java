package app.v1.messagebroker.controller;

import app.v1.messagebroker.DTO.GitHubNotificationDto;
import app.v1.messagebroker.service.github.GitService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/fetch")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Void> triggerGitFetch(
            @Parameter(description = "Payload containing GitHub repository and commit details", required = true)
            @RequestBody GitHubNotificationDto gitHubNotificationDto) {
        gitService.onTrigger(gitHubNotificationDto);
        return ResponseEntity.ok().build();
    }
}
