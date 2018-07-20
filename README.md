# Console chat
This is simple console client-server shared chat app. After connecting to server user should 
register by typing his nickname. Server will register connection and send 100 last users messages.
User can send messages and some commands, like /users and /changename. 
## Prerequisites

This application uses maven build system.
To build and run application computer must have `JRE 1.8+` and `Apache Maven` installed
## Running in IDE
To run chat in IDE you can use classes in `starter` package
## Building and Running
##### 1. Build jar files:
`mvn package`
##### 2. Run server:
`java -jar target/server.jar`
##### 3. Run client:
`java -jar target/client.jar`
##### Run some bots:
`java -jar target/bots.jar [number]`,

`[number]` is optional

Server must be alive for bots to run
## Run tests
To run tests you can use maven goal `test`
