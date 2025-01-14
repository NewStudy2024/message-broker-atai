package app.v1.messagebroker.DTO;

import lombok.Data;
import java.util.List;

@Data
public class CloudFlareDto {
    private CloudFlareResultDto result;
    private boolean success;
    private List<String> errors;
    private List<String> messages;
}
