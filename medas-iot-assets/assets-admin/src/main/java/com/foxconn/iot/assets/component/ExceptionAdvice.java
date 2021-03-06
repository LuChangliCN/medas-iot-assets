package com.foxconn.iot.assets.component;

import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foxconn.iot.assets.common.api.CommonResult;

@ControllerAdvice
@Order(100)
public class ExceptionAdvice {

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public CommonResult<?> handleException(Exception e) {
		return CommonResult.failed(e.getMessage());
	}
}
