package com.redis.usernotifyservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("User")
@ToString
@Data
public class User implements Serializable {
    private String userId;
    private String userName;
    private String userEmail;
    private String userContactNumber;
    private String userAddress;
    private boolean isActive;
    private Gender gender;
    public enum Gender {
        MALE, FEMALE
    }

}
