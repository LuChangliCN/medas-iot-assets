package com.foxconn.iot.assets.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.foxconn.iot.assets.common.api.CommonResult;
import com.foxconn.iot.assets.common.api.VerificationCode;
import com.foxconn.iot.assets.dto.UmsAdminLoginParam;
import com.foxconn.iot.assets.service.UmsAdminService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = { "登錄模塊" }, value = "LoginController")
public class LoginController {

	@Value("${jwt.tokenHeader}")
	private String tokenHeader;

	@Value("${jwt.tokenHead}")
	private String tokenHead;

	@Autowired
	private UmsAdminService adminService;

	@ApiOperation(value = "獲取驗證碼")
	@GetMapping("/vcode")
	public void verifyCode(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username", required = true) String username) throws IOException {		
		String text = adminService.getVerifyCode(username);
		if (StringUtils.isEmpty(text)) {
			text = VerificationCode.getCode(4);
			adminService.setVerifyCode(username, text);
		}
		BufferedImage image = VerificationCode.getImage(text);
		VerificationCode.output(image, response.getOutputStream());
	}

	@ApiOperation(value = "登錄")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public CommonResult<?> login(@RequestBody UmsAdminLoginParam umsAdminLoginParam, BindingResult result) {
		if (!adminService.checkVerifyCode(umsAdminLoginParam)) {
			return CommonResult.verifyCodeError();
		}
		String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
		if (token == null) {
			return CommonResult.validateFailed("用戶名或密碼錯誤");
		}
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("token", token);
		tokenMap.put("tokenHead", tokenHead);
		return CommonResult.success(tokenMap);
	}

	@ApiOperation(value = "刷新token")
	@RequestMapping(value = "/refreshToken", method = RequestMethod.GET)
	public CommonResult<?> refreshToken(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		String refreshToken = adminService.refreshToken(token);
		if (refreshToken == null) {
			return CommonResult.failed("token已经过期！");
		}
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("token", refreshToken);
		tokenMap.put("tokenHead", tokenHead);
		return CommonResult.success(tokenMap);
	}

	@ApiOperation(value = "登出")
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public CommonResult<?> logout() {
		return CommonResult.success(null);
	}
}