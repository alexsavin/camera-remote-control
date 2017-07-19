package com.fuckolympus.arc.error;

/**
 * Created by alex on 5.6.17.
 */
public class CommunicationException extends RuntimeException {

    private int statusCode = -1;

    public CommunicationException(String message) {
        super(message);
    }

    public CommunicationException(int statuCode, String message) {
        super(message);
        this.statusCode = statuCode;
    }

    public CommunicationException(int statuCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statuCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
