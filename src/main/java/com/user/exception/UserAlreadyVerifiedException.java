package com.user.exception;

public class UserAlreadyVerifiedException extends RuntimeException {

    public UserAlreadyVerifiedException() {
        super("User already verified. Please log in instead.");
    }

    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}
