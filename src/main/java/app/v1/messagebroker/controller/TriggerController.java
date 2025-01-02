package app.v1.messagebroker.controller;

import app.v1.messagebroker.service.GitService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


@Controller
@RequestMapping("/trigger")
public class TriggerController {

    private final GitService gitService;

    public TriggerController(GitService gitService) {
        this.gitService = gitService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> triggerGitFetch(){
        return ResponseEntity.status(200).body(gitService.fetchGitData());
    }
}
