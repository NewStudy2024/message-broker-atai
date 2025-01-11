package app.v1.messagebroker.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubNotificationDto {
    private String repository;
    private String ref;
    private String commit;
    private String pusher;

    @JsonProperty("previous_commit")
    private String previousCommit;
}
