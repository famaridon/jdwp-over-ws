package com.famaridon.tcpoverws.exceptions;

public class SessionManagerException extends Exception {
    public SessionManagerException() {
    }

    public SessionManagerException(String message) {
        super(message);
    }

    public SessionManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionManagerException(Throwable cause) {
        super(cause);
    }

    public SessionManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
