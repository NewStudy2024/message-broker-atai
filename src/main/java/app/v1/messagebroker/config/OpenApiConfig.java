package app.v1.messagebroker.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI messageBrokerOpenAPI() {
        return new OpenAPI()
                .addServersItem(new Server()
                        .url("https://atai-mamytov.click/nest24/message-broker")
                        .description("Production Server"))

                .info(new Info()
                        .title("Message broker API")
                        .description("Message broker endpoints to trigger the bot")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Atai Mamytov")
                                .email("atai.mamytov@.com"))
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project GitHub Repository")
                        .url("https://github.com/NewStudy2024/message-broker-atai"));
    }
}