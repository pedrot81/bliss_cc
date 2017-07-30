package com.pdt.blissrecruitment.exception;

import com.pdt.blissrecruitment.entities.Error;

public class ConnectorException extends Exception {
    private Error error;

    public ConnectorException(String message, Error error, Throwable throwable) {
        super(message, throwable);
        this.error = error;
    }

    public ConnectorException(String errorMessage, Error error) {
        super(errorMessage);
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ConnectorException{" +
                "error=" + error +
                '}';
    }
}
