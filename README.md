# Redis - User Activity Notification Service (Java 11)

Showcases how to implement chat app with ASP.NET Core, SignalR and Redis. This example uses the [pub/sub](https://redis.io/topics/pubsub) feature combined with Websockets from [SignalR](https://docs.microsoft.com/en-us/aspnet/core/fundamentals/websockets?view=aspnetcore-5.0#signalr) for implementing the message communication between client and server.

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

### Registration

![How it works](docs/screenshot000.png)

Redis is used mainly as a database to keep the user and for sending messages after successful user details updates are done.

#### How the data is stored:

#### How the data is stored:
  - User data is stored as a hash using key USER, hash key user id and hash value user.
    - hash key : `userId`- unique user id;
    - hash value : `user`: hashed user details
Code as below
'''Java 
    /**
     * Method to add/update {@link User} details to DB
     * @param user
     */
    @Override
    public void add(User user) {
        hashOperations.put(KEY, user.getUserId(), user);
    }
'''
* User hash set is accessed by key `user:{userId}`. The data for it stored with `HSET key field data`. User id is calculated by incrementing the `total_users`.

  - E.g `INCR total_users`

* Username is stored as a separate key (`username:{username}`) which returns the userId for quicker access.
  - E.g `SET username:Alex 4`

#### How the data is accessed:

- **Get User** `HGETALL user:{id}`

  - E.g `HGETALL user:2`, where we get data for the user with id: 2.

- **Online users:** will return ids of users which are online
  - E.g `SMEMBERS online_users`

#### Code Example: Prepare User Data in Redis HashSet

```csharp
var usernameKey = $"username:{username}";
// Yeah, bcrypt generally ins't used in .NET, this one is mainly added to be compatible with Node and Python demo servers.
var hashedPassword = BCrypt.Net.BCrypt.HashPassword(password);
var nextId = await redisDatabase.StringIncrementAsync("total_users");
var userKey = $"user:{nextId}";
await redisDatabase.StringSetAsync(usernameKey, userKey);
await redisDatabase.HashSetAsync(userKey, new HashEntry[] {
    new HashEntry("username", username),
    new HashEntry("password", hashedPassword)
});
```

### Rooms

![How it works](docs/screenshot001.png)

#### How the data is stored:

Each user has a set of rooms associated with them.

**Rooms** are sorted sets which contains messages where score is the timestamp for each message. Each room has a name associated with it.

- Rooms which user belongs too are stored at `user:{userId}:rooms` as a set of room ids.

  - E.g `SADD user:Alex:rooms 1`

- Set room name: `SET room:{roomId}:name {name}`
  - E.g `SET room:1:name General`

#### How the data is accessed:

- **Get room name** `GET room:{roomId}:name`.

  - E. g `GET room:0:name`. This should return "General"

- **Get room ids of a user:** `SMEMBERS user:{id}:rooms`.
  - E. g `SMEMBERS user:2:rooms`. This will return IDs of rooms for user with ID: 2

#### Code Example: Get all My Rooms

```csharp
//fetch all my rooms
var roomIds = await _database.SetMembersAsync($"user:{userId}:rooms");
var rooms = new List<ChatRoom>();
foreach (var roomIdRedisValue in roomIds)
{
    var roomId = roomIdRedisValue.ToString();
    //fetch all users in the rooms
    var name = await _database.StringGetAsync($"room:{roomId}:name");
    if (name.IsNullOrEmpty)
    {
        // It's a room without a name, likey the one with private messages
        var roomExists = await _database.KeyExistsAsync($"room:{roomId}");
        if (!roomExists)
        {
            continue;
        }

        var userIds = roomId.Split(':');
        if (userIds.Length != 2)
        {
            throw new Exception("You don't have access to this room");
        }

        rooms.Add(new ChatRoom()
        {
            Id = roomId,
            Names = new List<string>() {
                (await _database.HashGetAsync($"user:{userIds[0]}", "username")).ToString(),
                (await _database.HashGetAsync($"user:{userIds[1]}", "username")).ToString(),
            }
        });
    }
    else
    {
        rooms.Add(new ChatRoom()
        {
            Id = roomId,
            Names = new List<string>() {
                name.ToString()
            }
        });
    }
}
return rooms;
```

### Messages

#### Pub/sub

After initialization, a pub/sub subscription is created: `SUBSCRIBE MESSAGES`. At the same time, each server instance will run a listener on a message on this channel to receive real-time updates.

Again, for simplicity, each message is serialized to **_JSON_**, which we parse and then handle in the same manner, as WebSocket messages.

Pub/sub allows connecting multiple servers written in different platforms without taking into consideration the implementation detail of each server.

#### How the data is stored:

- Messages are stored at `room:{roomId}` key in a sorted set (as mentioned above). They are added with `ZADD room:{roomId} {timestamp} {message}` command. Message is serialized to an app-specific JSON string.
  - E.g `ZADD room:0 1617197047 { "From": "2", "Date": 1617197047, "Message": "Hello", "RoomId": "1:2" }`

#### How the data is accessed:

- **Get list of messages** `ZREVRANGE room:{roomId} {offset_start} {offset_end}`.
  - E.g `ZREVRANGE room:1:2 0 50` will return 50 messages with 0 offsets for the private room between users with IDs 1 and 2.

#### Code Example: Send Message

```csharp
public async Task SendMessage(UserDto user, ChatRoomMessage message)
{
  await _database.SetAddAsync("online_users", message.From);
  var roomKey = $"room:{message.RoomId}";
  await _database.SortedSetAddAsync(roomKey, JsonConvert.SerializeObject(message), (double)message.Date);
  await PublishMessage("message", message);
}
```

**Message**. A user sends a message, and it needs to be broadcasted to the other clients. The pub/sub allows us also to broadcast this message to all server instances which are connected to this Redis:

`PUBLISH message "{'serverId': 4132, 'type':'message', 'data': {'from': 1, 'date': 1615480369, 'message': 'Hello', 'roomId': '1:2'}}"`

Note we send additional data related to the type of the message and the server id. Server id is used to discard the messages by the server instance which sends them since it is connected to the same `MESSAGES` channel.

`type` field of the serialized JSON corresponds to the real-time method we use for real-time communication (connect/disconnect/message).

`data` is method-specific information. In the example above it's related to the new message.

#### How the data is stored / accessed:

The session data is stored in Redis by utilizing the [**StackExchange.Redis**](https://github.com/StackExchange/StackExchange.Redis) client.

```csharp
services
    .AddDataProtection()
    .PersistKeysToStackExchangeRedis(redis, "DataProtectionKeys");

services.AddStackExchangeRedisCache(option =>
{
    option.Configuration = redisConnectionUrl;
    option.InstanceName = "RedisInstance";
});

services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.Name = "AppTest";
});
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
