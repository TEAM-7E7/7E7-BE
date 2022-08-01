package com.seven.marketclip.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;

    private ObjectMapper objectMapper;


    // 스케줄 lockProvider
    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }

    // 캐싱
    @Bean
    public CacheManager userCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(5L));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(20))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder) -> builder
                .withCacheConfiguration("chatMessageCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration("chatRoomCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("goodsCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(20)))
                .withCacheConfiguration("myGoodsCache",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
    }

    /**
     * redis pub/sub 메시지를 처리하는 listener 설정
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
        redisStandaloneConfiguration.setPassword(redisPassword);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    //어플리케이션에서 사용할 redisTemplate 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return redisTemplate;
    }
}