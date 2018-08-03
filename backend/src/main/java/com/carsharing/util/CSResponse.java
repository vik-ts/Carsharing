package com.carsharing.util;

import org.springframework.lang.Nullable;

public class CSResponse<T> {
    private String message;
    private T body;

    public CSResponse(final String message, @Nullable T body) {
        this.message = message;
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }
}
