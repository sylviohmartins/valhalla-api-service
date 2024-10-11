package io.martins.valhalla.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

  @Bean
  OpenAPI openAPI() {
    Contact contact = new Contact() //
        .name("Sylvio H. Martins") //
        .email("sylviohmartins@gmail.com") //
        .url("https://github.com/sylviohmartins/valhalla-api-service");

    Info info = new Info() //
        .title("Valhalla API Service") //
        .version("1.0.0") //
        .contact(contact) //
        .description("Inventário de serviços disponibilizados pelo time de arquitetura MARTINS.");

    return new OpenAPI().info(info);
  }

}
