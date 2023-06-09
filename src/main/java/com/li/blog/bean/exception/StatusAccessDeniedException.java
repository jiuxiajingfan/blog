package com.li.blog.bean.exception;

/**
 * @ClassName StatusAccessDeniedException
 * @Description TODO
 * @Author Nine
 * @Date 2022/10/20 22:06
 * @Version 1.0
 */
public class StatusAccessDeniedException extends Exception {

    public StatusAccessDeniedException() {
    }

    public StatusAccessDeniedException(String message) {
        super(message);
    }

    public StatusAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusAccessDeniedException(Throwable cause) {
        super(cause);
    }

    public StatusAccessDeniedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
