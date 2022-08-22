package com.redis.usernotifyservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redis.usernotifyservice.model.User;
import com.redis.usernotifyservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public String status() {
        return "User-Notify service is running";
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserForId(id);
    }

    @PostMapping("/add/user")
    public User addUser(@RequestBody User user) throws JsonProcessingException {
        return userService.addUser(user);
    }

    @PutMapping("/activate/user")
    public User activateUser(@PathVariable String id) throws JsonProcessingException {
        return userService.updateUserStatus(id,true);
    }

    @PutMapping("/deactivate/user")
    public User deactivateUser(@PathVariable String id) throws JsonProcessingException {
        return userService.updateUserStatus(id,false);
    }

    @DeleteMapping("/delete/user")
    public String deleteUser(@PathVariable String id) throws JsonProcessingException {
        userService.deleteUser(id);
        return "User deleted with id : " + id;
    }
}
