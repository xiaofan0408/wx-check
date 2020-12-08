package com.xiaofan0408.wxcheck.common.exception;

import lombok.Getter;

@Getter
public final class ServiceException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    public ServiceException(ServiceExceptionEnum serviceExceptionEnum) {
        // 使用父类的 message 字段
        super(serviceExceptionEnum.getMessage());
        // 设置错误码
        this.code = serviceExceptionEnum.getCode();
    }

    public ServiceException( Integer code,String message) {
        super(message);
        this.code = code;
    }
}