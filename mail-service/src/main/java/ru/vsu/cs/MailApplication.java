package ru.vsu.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *A microservice for sending emails with a link to confirm registration.
 * When clicking on the link, the user will send a GET request to the rest service,
 * which contains the logic for activating the user account
 */
@ComponentScan("ru.vsu.cs.*")
@SpringBootApplication
public class MailApplication {

    public static void main(String[] args) {
        SpringApplication.run(MailApplication.class);
    }
}
