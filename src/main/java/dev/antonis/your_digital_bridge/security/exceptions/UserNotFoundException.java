package dev.antonis.your_digital_bridge.security.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String msg) { super (msg); }
}
