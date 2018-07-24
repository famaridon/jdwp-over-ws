package com.famaridon.tcpoverws.exceptions;

public class DebugSocketException extends SessionManagerException {
    public DebugSocketException() {
    }

    public DebugSocketException(String message) {
        super(message);
    }

    public DebugSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public DebugSocketException(Throwable cause) {
        super(cause);
    }

    public DebugSocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
