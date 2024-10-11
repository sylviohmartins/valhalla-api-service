package io.martins.valhalla.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessagesConfiguration {

  @Bean
  MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:messages");
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
    messageSource.setDefaultLocale(new Locale("pt", "BR"));
    messageSource.setFallbackToSystemLocale(false);

    return messageSource;
  }

}
