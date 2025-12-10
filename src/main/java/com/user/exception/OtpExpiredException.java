package com.user.exception;

public class OtpExpiredException extends RuntimeException {

    public OtpExpiredException() {
        super("OTP has expired.");
    }

    public OtpExpiredException(String message) {
        super(message);
    }
}
