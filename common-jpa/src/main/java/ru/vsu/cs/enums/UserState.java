package ru.vsu.cs.enums;

public enum UserState {

    /** User can enter the allowed commands or send a file*/
    BASIC_STATE,

    /** User can enter email or a command to cancel the registration process*/
    WAIT_FOR_EMAIL_STATE
}
