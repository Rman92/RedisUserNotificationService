package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.usernotifyservice.model.UserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisMessagePublisher implements MessagePublisher {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ChannelTopic topic;

    @Autowired
    private ObjectMapper objectMapper;

    public RedisMessagePublisher() {
    }

    public RedisMessagePublisher(final RedisTemplate<String, Object> redisTemplate, final ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(final UserEvent userEvent) throws JsonProcessingException {
        redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(userEvent));
    }
}
