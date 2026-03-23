package com.qlx.oa.config;

import com.qlx.oa.common.BusinessException;
import com.qlx.oa.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务拦截: 状态码[{}], 提示信息[{}]", e.getCode(), e.getMsg());

        return Result.error(e.getCode(), e.getMsg());
    }


    @ExceptionHandler(Exception.class)
    public Result<?> handleSystemException(Exception e) {

        log.error("系统内部发生未知异常: ", e);

        return Result.error(500, "系统开小差了，请稍后再试");
    }
    //专门拦截 @Validated 参数校验失败的异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();


        String defaultMessage = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";

        log.warn("参数校验拦截: {}", defaultMessage);


        return Result.error(400, defaultMessage);
    }
}
