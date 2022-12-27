package com.study.myblog.handler.ex;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
