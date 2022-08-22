package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redis.usernotifyservice.model.UserEvent;

public interface MessagePublisher {
    void publish(final UserEvent userEvent) throws JsonProcessingException;
}
