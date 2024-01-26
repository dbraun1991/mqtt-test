package de.application.domain.exception;

public abstract class DomainException extends Exception {
    private final String message;

    public DomainException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
