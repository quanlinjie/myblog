package com.study.myblog.handler.ex;

@SuppressWarnings("serial")
public class CustomApiException extends RuntimeException {

    public CustomApiException(String message) {
        super(message);
    }
}
