package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redis.usernotifyservice.model.UserEvent;
/**
 * Interface which declares method for message publisher
 *
 * @author Rushabh Khandare
 */
public interface MessagePublisher {
    void publish(final UserEvent userEvent) throws JsonProcessingException;
}
