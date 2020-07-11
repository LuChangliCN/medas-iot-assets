package com.foxconn.iot.assets.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.foxconn.iot.assets.common.api.CommonPage;
import com.foxconn.iot.assets.common.api.CommonResult;
import com.foxconn.iot.assets.model.UmsResource;
import com.foxconn.iot.assets.security.component.DynamicSecurityMetadataSource;
import com.foxconn.iot.assets.service.UmsResourceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台资源管理Controller
 */
@Controller
@Api(tags = "UmsResourceController", description = "后台资源管理")
@RequestMapping("/resource")
public class UmsResourceController {

	@Autowired
	private UmsResourceService resourceService;
	@Autowired
	private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

	@ApiOperation("添加后台资源")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public CommonResult<?> create(@RequestBody UmsResource umsResource) {
		int count = resourceService.create(umsResource);
		dynamicSecurityMetadataSource.clearDataSource();
		if (count > 0) {
			return CommonResult.success(count);
		} else {
			return CommonResult.failed();
		}
	}

	@ApiOperation("修改后台资源")
	@RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
	@ResponseBody
	public CommonResult<?> update(@PathVariable Long id, @RequestBody UmsResource umsResource) {
		int count = resourceService.update(id, umsResource);
		dynamicSecurityMetadataSource.clearDataSource();
		if (count > 0) {
			return CommonResult.success(count);
		} else {
			return CommonResult.failed();
		}
	}

	@ApiOperation("根据ID获取资源详情")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public CommonResult<UmsResource> getItem(@PathVariable Long id) {
		UmsResource umsResource = resourceService.getItem(id);
		return CommonResult.success(umsResource);
	}

	@ApiOperation("根据ID删除后台资源")
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	@ResponseBody
	public CommonResult<?> delete(@PathVariable Long id) {
		int count = resourceService.delete(id);
		dynamicSecurityMetadataSource.clearDataSource();
		if (count > 0) {
			return CommonResult.success(count);
		} else {
			return CommonResult.failed();
		}
	}

	@ApiOperation("分页模糊查询后台资源")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public CommonResult<CommonPage<UmsResource>> list(@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String nameKeyword, @RequestParam(required = false) String urlKeyword,
			@RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		List<UmsResource> resourceList = resourceService.list(categoryId, nameKeyword, urlKeyword, pageSize, pageNum);
		return CommonResult.success(CommonPage.restPage(resourceList));
	}

	@ApiOperation("查询所有后台资源")
	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	@ResponseBody
	public CommonResult<List<UmsResource>> listAll() {
		List<UmsResource> resourceList = resourceService.listAll();
		return CommonResult.success(resourceList);
	}
}