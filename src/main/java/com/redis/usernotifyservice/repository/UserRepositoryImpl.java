package com.redis.usernotifyservice.repository;

import com.redis.usernotifyservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Implementation of {@link UserRepository}
 * to connect to Redis DB using {@link RedisTemplate} and {@link HashOperations}
 *
 * @author Rushabh Khandare
 */
@Repository
public class UserRepositoryImpl implements UserRepository{

    private static final String KEY = "User";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    /**
     * Constructor to instantiate UserRepositoryImpl
     *
     * @param redisTemplate
     */
    @Autowired
    public UserRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    /**
     * Init method to instantiate hashOperations
     * using {@link RedisTemplate}
     */
    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    /**
     * Method to fetch all existing users from Redis DB
     * using KEY
     * @return  map of users Map<Object, Object>
     */
    @Override
    public Map<Object, Object> findAllUsers() {
        return hashOperations.entries(KEY);
    }

    /**
     * Method to add/update {@link User} details to DB
     * @param user
     */
    @Override
    public void add(User user) {
        hashOperations.put(KEY, user.getUserId(), user);
    }

    /**
     * Method to delete {@link User} details from DB
     * @param id User ID
     */
    @Override
    public void delete(String id) {
        hashOperations.delete(KEY, id);
    }

    /**
     * Method to fetch {@link User} details from DB using User ID
     * @param id User ID
     * @return user
     */
    @Override
    public User findUser(String id) {
        return (User) hashOperations.get(KEY, id);
    }
}
