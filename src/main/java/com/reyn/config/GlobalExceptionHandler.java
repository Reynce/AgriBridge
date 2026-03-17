package com.reyn.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.util.SaResult;
import com.reyn.utils.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * 优先匹配写在上面的异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    public SaResult handleNotLoginException(NotLoginException e){
        SaResult response = new SaResult();
        response.setCode(401);
        response.setMsg("登录凭证过期或无效");
        return response;
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SaResult handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMsg = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error ->
                errorMsg.append(((FieldError) error).getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; "));
        return SaResult.error("参数校验失败：" + errorMsg.toString());
    }

    @ExceptionHandler(BusinessException.class)
    public SaResult handleBusinessException(BusinessException e){
        return SaResult.error(e.getMessage());
    }

    // 拦截: 其他所有异常
    @ExceptionHandler(Exception.class)
    public SaResult handerException(Exception e){
        e.printStackTrace();
        return SaResult.error(e.getMessage());
    }
}
