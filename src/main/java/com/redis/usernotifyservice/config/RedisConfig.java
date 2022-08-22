//package com.redis.usernotifyservice.config;
//
//import com.redis.usernotifyservice.pubsup.MessagePublisher;
//import com.redis.usernotifyservice.pubsup.RedisMessagePublisher;
//import com.redis.usernotifyservice.pubsup.RedisMessageSubscriber;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisPassword;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
//import org.springframework.data.redis.serializer.GenericToStringSerializer;
//
//import java.time.Duration;
//
////@Configuration
////@EnableRedisRepositories
//public class RedisConfig {
//    @Value("${REDIS_URL}")
//    private String redisUrl;
//
//    @Value("${REDIS_HOST}")
//    private String redisHost;
//
//    @Value("${REDIS_PORT}")
//    private String redisPort;
//
//    @Value("${REDIS_PASSWORD}")
//    private String redisPassword;
//
//    @Value("${REDIS_DB}")
//    private String redisDB;
//
//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(redisHost);
//        redisStandaloneConfiguration.setPort(Integer.valueOf(redisPort));
//        redisStandaloneConfiguration.setUsername("default");
//        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
//        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
//        //jedisClientConfiguration.connectTimeout(Duration.ofMillis(2000));
//        jedisClientConfiguration.usePooling();
//        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        final RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(jedisConnectionFactory());
//        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
//        template.setEnableTransactionSupport(true);
//        return template;
//    }
//
//    @Bean
//    MessageListenerAdapter messageListener() {
//        return new MessageListenerAdapter(new RedisMessageSubscriber());
//    }
//
//    @Bean
//    RedisMessageListenerContainer redisContainer() {
//        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(jedisConnectionFactory());
//        container.addMessageListener(messageListener(), topic());
//        return container;
//    }
//
//    @Bean
//    MessagePublisher redisPublisher() {
//        return new RedisMessagePublisher(redisTemplate(), topic());
//    }
//
//    @Bean
//    ChannelTopic topic() {
//        return new ChannelTopic("pubsub:queue");
//    }
//}
