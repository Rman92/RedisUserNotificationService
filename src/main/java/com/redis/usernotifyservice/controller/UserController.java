package com.redis.usernotifyservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.redis.usernotifyservice.model.User;
import com.redis.usernotifyservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 * Rest endpoint controller which expose APIs for user to
 * perform add/delete/update activities using {@link UserService}
 *
 * @author Rushabh Khandare
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * REST API to check health of the app
     *
     * @return status String
     */
    @GetMapping("/health")
    public String status() {
        return "User-Notify service is running";
    }

    /**
     * REST API to fetch {@link User} details using id
     *
     * @return user User
     */
    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserForId(id);
    }

    /**
     * REST API to add {@link User} details
     *
     * @return user User
     */
    @PostMapping("/add/user")
    public User addUser(@RequestBody User user) throws JsonProcessingException {
        return userService.addUser(user);
    }

    /**
     * REST API to activate {@link User}
     *
     * @return user User
     */
    @PutMapping("/activate/user/{id}")
    public User activateUser(@PathVariable String id) throws JsonProcessingException {
        return userService.updateUserStatus(id,true);
    }

    /**
     * REST API to deactivate {@link User}
     *
     * @return user User
     */
    @PutMapping("/deactivate/user/{id}")
    public User deactivateUser(@PathVariable String id) throws JsonProcessingException {
        return userService.updateUserStatus(id,false);
    }

    /**
     * REST API to delete {@link User}
     *
     * @return id String
     */
    @DeleteMapping("/delete/user/{id}")
    public String deleteUser(@PathVariable String id) throws JsonProcessingException {
        userService.deleteUser(id);
        return "User deleted with id : " + id;
    }
}
