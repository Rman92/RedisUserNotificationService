package com.redis.usernotifyservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class NotifyEvent implements Serializable {
    private String userEmail;
    private String message;
}
