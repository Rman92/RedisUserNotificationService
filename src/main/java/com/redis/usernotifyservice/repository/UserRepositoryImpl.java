package com.redis.usernotifyservice.repository;

import com.redis.usernotifyservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private static final String KEY = "User";

    private RedisTemplate<String, Object> redisTemplate;
    private HashOperations hashOperations;

    @Autowired
    public UserRepositoryImpl(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public Map<Object, Object> findAllUsers() {
        return hashOperations.entries(KEY);
    }

    @Override
    public void add(User user) {
        hashOperations.put(KEY, user.getUserId(), user);
    }

    @Override
    public void delete(String id) {
        hashOperations.delete(KEY, id);
    }

    @Override
    public User findUser(String id) {
        return (User) hashOperations.get(KEY, id);
    }
}
