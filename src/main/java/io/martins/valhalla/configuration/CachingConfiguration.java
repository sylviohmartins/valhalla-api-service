package io.martins.valhalla.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfiguration {

  private static final long CACHE_EXPIRATION_HOURS = 12L;

  private static final long CACHE_MAXIMUM_SIZE = 1000L;

  @Bean
  public Caffeine<Object, Object> caffeineConfig() {
    return Caffeine.newBuilder() //
        .expireAfterWrite(CACHE_EXPIRATION_HOURS, TimeUnit.HOURS) //
        .maximumSize(CACHE_MAXIMUM_SIZE); //
  }

  @Bean
  public CaffeineCacheManager cacheManager(final Caffeine<Object, Object> caffeine) {
    final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
    cacheManager.setCaffeine(caffeine);

    return cacheManager;
  }

}
