package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.usernotifyservice.model.UserEvent;
import com.redis.usernotifyservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
/**
 * Implementation of the class MessagePublisher
 * which is used by {@link UserService}to publish {@link UserEvent}
 *
 * @author Rushabh Khandare
 */
@Component
public class RedisMessagePublisher implements MessagePublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Default constructor
     */
    public RedisMessagePublisher() {
    }

    /**
     * All arg constructor
     *
     * @param redisTemplate
     * @param topic
     */
    public RedisMessagePublisher(final RedisTemplate<String, Object> redisTemplate, final ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    /**
     * Method to publish {@link UserEvent} to redis pubSub message queue
     *
     * @param userEvent UserEvent
     * @throws JsonProcessingException
     */
    public void publish(final UserEvent userEvent) throws JsonProcessingException {
        redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(userEvent));
    }
}
