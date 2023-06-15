package ru.skobaro.exceptions;

public class HTTPRequestException extends RuntimeException {
    public HTTPRequestException() {
    }

    public HTTPRequestException(String message) {
        super(message);
    }

    public HTTPRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HTTPRequestException(Throwable cause) {
        super(cause);
    }

    public HTTPRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
