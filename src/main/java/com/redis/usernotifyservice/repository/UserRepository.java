package com.redis.usernotifyservice.repository;

import com.redis.usernotifyservice.model.User;

import java.util.Map;

/**
 * Interface which defines methods to perform operations on database
 *
 * @author Rushabh Khandare
 */
public interface UserRepository{
    /**
     * Return all users
     */
    Map<Object, Object> findAllUsers();

    /**
     * Add key-value pair to Redis.
     */
    void add(User user);

    /**
     * Delete a key-value pair in Redis.
     */
    void delete(String id);

    /**
     * find a user
     */
    User findUser(String id);
}
