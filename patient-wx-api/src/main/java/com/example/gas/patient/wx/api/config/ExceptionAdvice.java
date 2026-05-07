package com.example.gas.patient.wx.api.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.felord.payment.PayException;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.exception.HospitalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public R exceptionHandler(Exception e) {
        if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException exception = (HttpMessageNotReadableException) e;
            log.error("error", exception);
            return R.error("请求未提交数据或者数据有误");
        } else if (e instanceof MissingServletRequestPartException) {
            MissingServletRequestPartException exception = (MissingServletRequestPartException) e;
            log.error("error", exception);
            return R.error("请求提交数据错误");
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) e;
            log.error("error", exception);
            return R.error("HTTP请求方法类型错误");
        }
        //处理后端验证失败产生的异常
        else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            return R.error(exception.getBindingResult().getFieldError().getDefaultMessage());
        }
        //处理业务异常
        else if (e instanceof HospitalException) {
            log.error("执行异常", e);
            HospitalException exception = (HospitalException) e;
            return R.error(exception.getMsg());
        } else if (e instanceof PayException) {
            PayException exception = (PayException) e;
            log.error("微信支付异常", exception);
            return R.error("微信支付异常");
        }
        //处理其余的异常
        else {
            log.error("执行异常", e);
            return R.error("执行异常");
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public R unLoginHandler(Exception e) {
        return R.error(e.getMessage());
    }

}