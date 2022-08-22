package com.redis.usernotifyservice.model;

import lombok.*;

import java.io.Serializable;
/**
 * UserEvent class which represents User Event to be published to
 * notification service
 *
 * @author Rushabh Khandare
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {
    private String userId;
    private EventType eventType;
}
