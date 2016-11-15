package com.garena.design.pattern.interceptor;


/**
 * Nov 2016
 * 
 * @author Tho Q Luong
 */
public class InterceptorException extends RuntimeException{

    public InterceptorException() {
        super();
    }

    public InterceptorException(String message) {
        super(message);
    }

    public InterceptorException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterceptorException(Throwable cause) {
        super(cause);
    }
}