package ru.vsu.cs.services;

import ru.vsu.cs.entities.AppUser;

public interface AppUserService {
    String registerUser(AppUser appUser);
    String setEmail(AppUser appUser, String email);
}
