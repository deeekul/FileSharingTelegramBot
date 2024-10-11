# File Sharing Bot 

File Sharing Bot - Telegram bot that is simple file sharing service written in Java. The application was created as a 
training project to explore the possibilities of Telegram bots.

## 💻 Tech Stack
- Java 11
- Spring Boot 2.7.5
- PostgreSQL
- Docker
- RabbitMQ
- Maven

## Opportunities
- User registration
- Storing downloaded files
- Generating a unique link for each photo or document for later download or transfer

## Application architecture
### Dispathcer microservice 
It is used to check incoming data and distribute messages to the appropriate queues in the broker.

### Node microservice
It is designed for processing messages from the broker (most of the business logic, converting the file to binary and uploading to the database, 
as well as generating responses for users and sending them to the broker).

### Rest microservice
It is used to process incoming http requests to download a file and confirm registration.

### Mail microservice
It is designed for sending emails containing a link to confirm registration.

