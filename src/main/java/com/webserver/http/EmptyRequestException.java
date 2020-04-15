package com.webserver.http;

/**
 * ClassName: EmptyRequestException
 * Function:  空请求异常
 * 当实例化HttpRequest时若为空请求会抛出该异常
 * Date:      2019/11/4 16:04
 * @author     Kenny
 * version    V1.0
 */
public class EmptyRequestException extends Exception{

    public EmptyRequestException() {
        super();
    }

    public EmptyRequestException(String message) {
        super(message);
    }

    public EmptyRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyRequestException(Throwable cause) {
        super(cause);
    }

    public EmptyRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
