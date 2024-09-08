package ru.vsu.cs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The module is responsible for downloading files from the database via the link
 */
@SpringBootApplication
public class RestService {
    public static void main(String[] args) {
        SpringApplication.run(RestService.class);
    }
}
