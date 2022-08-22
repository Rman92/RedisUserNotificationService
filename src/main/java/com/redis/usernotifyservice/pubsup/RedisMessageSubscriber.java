package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.usernotifyservice.model.UserEvent;
import com.redis.usernotifyservice.service.NotifyService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
/**
 * Implementation of the class MessageListener which listens to the Redis Queue for {@link UserEvent}
 *
 * @author Rushabh Khandare
 */
@Component
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
public class RedisMessageSubscriber implements MessageListener {

    public NotifyService notifyService = new NotifyService();

    private ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> messageList = new ArrayList<String>();

    /**
     * MessageListener method which is listening to Redis Queue
     * and passing {@link UserEvent} to {@link NotifyService}
     *
     * @param message
     * @param pattern
     */
    @SneakyThrows
    public void onMessage(final Message message, final byte[] pattern) {
        messageList.add(message.toString());
        log.info("Message received: " + new String(message.getBody()));
        UserEvent userEvent = objectMapper.readValue(new String(message.getBody()), UserEvent.class);
        notifyService.sendUserNotification(userEvent);
    }
}
