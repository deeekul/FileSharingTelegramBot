package ru.vsu.cs.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.cs.services.UserActivationService;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class ActivationController {

    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var result = userActivationService.activation(id);
        if (result) {
            return ResponseEntity.ok().body("Регистрация прошла успешно!");
        }
        return ResponseEntity.internalServerError().build();
    }
}
