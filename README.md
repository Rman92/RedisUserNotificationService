# Redis - User Activity Notification Service (Java 11)

Spring Boot app which allows user to perform different activities on User details and get notified once the activities are successfully stored / updated in Redis Database. This example uses the [pub/sub](https://redis.io/topics/pubsub) this feature used to publish User activities as events to Notification service.

<a href="https://github.com/Rman92/redis-usernotifyservice/blob/main/Arch-Redis-User-Notification-Service.png"><img src="https://github.com/Rman92/redis-usernotifyservice/blob/main/Arch-Redis-User-Notification-Service.png" width="100%"></a>

## Overview video

Here's a short video that explains the project and how it uses Redis:

[![Watch the video on YouTube](https://github.com/redis-developer/basic-redis-chat-app-demo-dotnet/raw/main/docs/YTThumbnail.png)](https://www.youtube.com/watch?v=miK7xDkDXF0)

## Technical Stack

- Backend - _Java 11_,_Spring Boot_, _Redis Database_, _Redis PubSub_, _RedisHashOperations_

## How it works?

### Database Schema

#### User

```java
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
```

### Initialization

User details can be add/update/activate/deactivate using REST APIs. 

![How it works](docs/screenshot000.png)

Redis is used mainly as a database to keep the user and for sending messages after successful user details updates are done.

#### How the data is stored:

#### How the data is stored:
  - User data is stored as a hash using key USER, hash key user id and hash value user.
    - hash key : `userId`- unique user id;
    - hash value : `user`: hashed user details
Code to add user details in Redis DB usinhg HashOps
```java
    /**
     * Method to add/update {@link User} details to DB
     * @param user
     */
    @Override
    public void add(User user) {
        hashOperations.put(KEY, user.getUserId(), user);
    }
```
* User hash is accessed by key `USER:{userId}`. The data for it stored with `hash of user`.

* UserEvent is published to redis topic name `USERQUEUE`. 
```java
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
```
Code to pubilsh UserEvent
```java
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
```
Code to subscribe topic which contains UserEvents
```java
/**
     * MessageListener method which is listening to Redis Queue
     * and passing {@link UserEvent} to {@link NotifyService}
     *
     * @param message
     * @param pattern
     */
    @SneakyThrows
    public void onMessage(final Message message, final byte[] pattern) {
        messageList.add(message.toString());
        log.info("Message received: " + new String(message.getBody()));
        UserEvent userEvent = objectMapper.readValue(new String(message.getBody()), UserEvent.class);
        notifyService.sendUserNotification(userEvent);
    }
```
#### How the data is accessed from Redis Database:

- **Get User** Using hashops from UserRepository
```java
  /**
     * Method to fetch {@link User} details from DB using User ID
     * @param id User ID
     * @return user
     */
    @Override
    public User findUser(String id) {
        return (User) hashOperations.get(KEY, id);
    }
```

## How to run it locally?

#### Write in environment variable or Dockerfile actual connection to Redis:

```
   REDIS_ENDPOINT_URL = "Redis server URI:PORT_NUMBER"
   REDIS_PASSWORD = "Password to the server"
```

#### Run backend

Build the Redis container with the following command:
```sh
  docker-compose up -d
```

From the _Redis-UserNotiFicationService_ Directory execute:

```sh
mvn clean install
java -jar targe/redis-usernotifyservice*.jar
```

## Try it out

#### Deploy to Heroku

<p>
    <a href="https://heroku.com/deploy" target="_blank">
        <img src="https://www.herokucdn.com/deploy/button.svg" alt="Deploy to Heorku" />
    </a>
</p>
