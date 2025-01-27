package app.v1.messagebroker.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeminiResponseDto {
    private String title;
    private String body;
}
