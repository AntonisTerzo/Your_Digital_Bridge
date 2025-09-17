package dev.antonis.your_digital_bridge.security.exceptions;

public class InvalidTransactionException extends RuntimeException{
    public InvalidTransactionException(String msg) { super(msg); }
}
