package app.v1.messagebroker.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GitHubNotificationDto {
    private String repository;
    private String ref;
    private String commit;
    private String pusher;
    private String nameRepo;

    @JsonProperty("previous_commit")
    private String previousCommit;

    // Setter for repository with dynamic nameRepo update
    public void setRepository(String repository) {
        this.repository = repository;
        updateNameRepo();
    }

    // Setter for pusher with dynamic nameRepo update
    public void setPusher(String pusher) {
        this.pusher = pusher;
        updateNameRepo();
    }

    // Update nameRepo whenever repository or pusher changes
    private void updateNameRepo() {
        if (repository != null && pusher != null) {
            this.nameRepo = repository.replaceFirst(String.format("^%s/", pusher), "");
        } else {
            this.nameRepo = null;
        }
    }
}
