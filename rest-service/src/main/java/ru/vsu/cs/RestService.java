package ru.vsu.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The module is responsible for downloading files from the database via a link and activating the user account
 */
@EnableJpaRepositories("ru.vsu.cs.*")
@EntityScan("ru.vsu.cs.*")
@ComponentScan("ru.vsu.cs.*")
@SpringBootApplication
public class RestService {

    public static void main(String[] args) {
        SpringApplication.run(RestService.class);
    }
}
