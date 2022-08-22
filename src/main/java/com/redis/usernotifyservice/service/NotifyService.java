package com.redis.usernotifyservice.service;

import com.redis.usernotifyservice.model.EventType;
import com.redis.usernotifyservice.model.NotifyEvent;
import com.redis.usernotifyservice.model.UserEvent;
import com.redis.usernotifyservice.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class which verifies user {@link UserEvent} and
 * performs notification process on it
 *
 * @author Rushabh Khandare
 */
@Log4j2
@Service
@NoArgsConstructor
public class NotifyService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Method sends User activity notification
     * {@link NotifyEvent} over email to respective user
     *
     * @param userEvent
     */
    public void sendUserNotification(UserEvent userEvent) {
        log.info("Fetching user details from redis db : {}",userEvent);
        NotifyEvent notifyEvent = new NotifyEvent();
        notifyEvent.setUserEmail(userEvent.getUserId());
        EventType eventType = userEvent.getEventType();
        String message = null;
        switch (eventType){
            case CREATE:
                message = "User registered.";
                break;
            case DELETE:
                message = "User deleted.";
                break;
            case ACTIVATE:
                message = "User activated.";
                break;
            case INACTIVATE:
                message = "User inactivated.";
                break;
            default:
                message = "";
        }
        notifyEvent.setMessage(message);
        log.info("Notification sent {}",notifyEvent.toString());
    }
}
