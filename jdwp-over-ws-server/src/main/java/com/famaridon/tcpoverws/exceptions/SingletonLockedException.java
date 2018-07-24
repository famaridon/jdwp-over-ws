package com.famaridon.tcpoverws.exceptions;

public class SingletonLockedException extends SessionManagerException {
    public SingletonLockedException() {
    }

    public SingletonLockedException(String message) {
        super(message);
    }

    public SingletonLockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SingletonLockedException(Throwable cause) {
        super(cause);
    }

    public SingletonLockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
