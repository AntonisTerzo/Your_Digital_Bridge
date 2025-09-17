package dev.antonis.your_digital_bridge.security.exceptions;

public class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String msg) { super(msg); }
}
