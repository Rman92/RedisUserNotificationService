package com.redis.usernotifyservice.config;

import com.redis.usernotifyservice.pubsup.RedisMessageSubscriber;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
/**
 * Configuration class which initializes redis database and Redis pubSub
 * connections using redis configs from {@literal application.properties}
 * and env for password
 *
 * @author Rushabh Khandare
 */
@Log4j2
@Configuration
public class RedisAppConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAppConfig.class);
    @Value("${REDIS_URL}")
    private String redisUrl;

    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private String redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Value("${REDIS_DB}")
    private String redisDB;

    /**
     * Bean to instantiate {@link RedisTemplate}
     *
     * @return redisTemplate RedisTemplate
     */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        template.setEnableTransactionSupport(true);
        return template;
    }

    /**
     * Bean to instantiate {@link RedisMessageSubscriber}
     *
     * @return messageListenerAdapter MessageListenerAdapter
     */
    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    /**
     * Bean to instantiate {@link RedisMessageListenerContainer}
     *
     * @return container RedisMessageListenerContainer
     */
    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    /**
     * Bean to instantiate {@link ChannelTopic} using topic name USEREVENTS
     *
     * @return container ChannelTopic
     */
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("USEREVENTS");
    }

    /**
     * Bean to instantiate {@link RedisConnectionFactory}
     *
     * @return lettuceConnectionFactory LettuceConnectionFactory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Read environment variables
        String endpointUrl = redisUrl;
        if (endpointUrl == null) {
            endpointUrl = "127.0.0.1:6379";
        }

        String[] urlParts = endpointUrl.split(":");

        String host = urlParts[0];
        String port = "6379";

        if (urlParts.length > 1) {
            port = urlParts[1];
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, Integer.parseInt(port));

        if(redisPassword == null || redisPassword.isEmpty()) {
            redisPassword = System.getenv("REDIS_PASSWORD");
        }
        log.info("Connecting to %s:%s with password: %s%n", host, port, redisPassword);

        if (redisPassword != null) {
            config.setPassword(redisPassword);
        } else {
            log.error("Incorrect redis password.");
        }
        return new LettuceConnectionFactory(config);
    }
}
