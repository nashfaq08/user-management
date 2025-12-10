package com.user.exception;

public class OtpNotFoundException extends RuntimeException {

    public OtpNotFoundException() {
        super("OTP record not found.");
    }

    public OtpNotFoundException(String message) {
        super(message);
    }
}
