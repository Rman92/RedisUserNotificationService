package com.redis.usernotifyservice.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {
    private String userId;
    private EventType eventType;
}
