package com.redis.usernotifyservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.usernotifyservice.model.EventType;
import com.redis.usernotifyservice.model.User;
import com.redis.usernotifyservice.model.UserEvent;
import com.redis.usernotifyservice.pubsup.RedisMessagePublisher;
import com.redis.usernotifyservice.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

/**
 * Service class which performs db operations on user {@link User}
 * request and publish the {@link UserEvent} to Redis pubSub USEREVENTS queue
 *
 * @author Rushabh Khandare
 */
@Service
@Log4j2
@NoArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisMessagePublisher redisMessagePublisher;

    ObjectMapper objectMapper = new ObjectMapper();
    private UserEvent userEvent;

    /**
     * Service Method to fetch user details
     *
     * @param id User ID
     * @return User
     */
    //@Cacheable(value = "userCache")
    public User getUserForId(String id) {
        log.info("Fetching user details by id {}",id);
        User user =  userRepository.findUser(id);
        return user;
    }

    /**
     * Service Method to add user details and publish CREATE {@link EventType}
     *
     * @param user User
     * @return User
     */
    public User addUser(User user) throws JsonProcessingException {
        log.info("Adding new user details to db : {}",user.toString());
        publishUserEvent(user.getUserId(),EventType.CREATE);
        userRepository.add(user);
        return user;
    }

    /**
     * Service Method to update user details and publish Activate/Deactivate {@link EventType}
     *
     * @param id User ID
     * @param activate true/false value to activate/deactivate user
     * @return User updated user details
     */
    public User updateUserStatus(String id, boolean activate) throws JsonProcessingException {
        User user = getUserForId(id);
        user.setActive(activate);
        log.info("Updating status of user details for is: {}",id);
        userRepository.add(user);
        if(activate){
            publishUserEvent(user.getUserId(),EventType.ACTIVATE);
        }else {
            publishUserEvent(user.getUserId(),EventType.INACTIVATE);
        }
        return user;
    }

    /**
     * Service Method to delete user details and publish Delete {@link EventType}
     *
     * @param id User ID
     * @return User updated user details
     */
    public void deleteUser(String id) throws JsonProcessingException {
      userRepository.delete(id);
        log.info("Deleting new user details to db : {}",id);
        publishUserEvent(id,EventType.DELETE);
    }

    /**
     * Helper method to publish {@link UserEvent}
     *
     * @param id User ID
     * @param eventType User activity event type
     * @throws JsonProcessingException
     */
    private void publishUserEvent(String id,EventType eventType) throws JsonProcessingException {
        userEvent = new UserEvent(id, eventType);
        redisMessagePublisher.publish(userEvent);
        log.info("Published user event {}",userEvent.toString());
    }
}
