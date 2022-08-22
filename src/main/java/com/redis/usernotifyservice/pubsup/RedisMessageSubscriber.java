package com.redis.usernotifyservice.pubsup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.usernotifyservice.model.UserEvent;
import com.redis.usernotifyservice.service.NotifyService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    public NotifyService notifyService = new NotifyService();

    private ObjectMapper objectMapper = new ObjectMapper();

    public static List<String> messageList = new ArrayList<String>();

    @SneakyThrows
    public void onMessage(final Message message, final byte[] pattern) {
        messageList.add(message.toString());
        System.out.println("Message received: " + new String(message.getBody()));
        UserEvent userEvent = objectMapper.readValue(new String(message.getBody()), UserEvent.class);
        notifyService.sendUserNotification(userEvent);
    }
}
