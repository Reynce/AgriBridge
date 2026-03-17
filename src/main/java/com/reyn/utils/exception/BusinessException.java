package com.reyn.utils.exception;

/**
 * 简单的自定义业务异常类
 * 支持手动传入错误信息
 */
public class BusinessException extends RuntimeException {

    private int code;
    private String message;

    // 默认构造函数
    public BusinessException() {
        super();
    }

    // 只传入错误信息
    public BusinessException(String message) {
        super(message);
        this.message = message;
        this.code = 500; // 默认错误码
    }

    // 传入错误码和错误信息
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    // 传入错误信息和原因异常
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = 500;
    }

    // 传入错误码、错误信息和原因异常
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    // getter方法
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

