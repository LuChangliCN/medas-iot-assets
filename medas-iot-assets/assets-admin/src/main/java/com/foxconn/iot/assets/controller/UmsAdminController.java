package com.foxconn.iot.assets.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foxconn.iot.assets.common.api.CommonPage;
import com.foxconn.iot.assets.common.api.CommonResult;
import com.foxconn.iot.assets.dto.UmsAdminDto;
import com.foxconn.iot.assets.dto.UmsAdminParam;
import com.foxconn.iot.assets.model.UmsAdmin;
import com.foxconn.iot.assets.model.UmsRole;
import com.foxconn.iot.assets.service.UmsAdminService;
import com.foxconn.iot.assets.service.UmsRoleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 后台用户管理
 */
@RestController
@Api(tags = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
	
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsRoleService roleService;

    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public CommonResult<UmsAdmin> register(@RequestBody UmsAdminParam umsAdminParam, BindingResult result) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            CommonResult.failed();
        }
        return CommonResult.success(umsAdmin);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<?> getAdminInfo(Principal principal) {
        if(principal==null){
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("roles", new String[]{"TEST"});
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        return CommonResult.success(data);
    }

    @ApiOperation("根据用户名或姓名分页获取用户列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
    												@RequestParam(value = "companyId", required = false) Long companyId,
                                                   @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsAdmin> adminList = adminService.list(keyword, companyId, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CommonResult<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getItem(id);
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定用户信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public CommonResult<?> update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        int count = adminService.update(id, admin);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("删除指定用户信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public CommonResult<?> delete(@PathVariable Long id) {
        int count = adminService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改帐号状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    public CommonResult<?> updateStatus(@PathVariable Long id,@RequestParam(value = "status") Integer status) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        int count = adminService.update(id,umsAdmin);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给用户分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    public CommonResult<?> updateRole(@RequestParam("adminId") Long adminId,
                                   @RequestParam("roleIds") List<Long> roleIds) {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定用户的角色")
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.GET)
    public CommonResult<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        if (roleList.size() > 0 && roleList.get(0) != null) {
        	return CommonResult.success(roleList);
        } else {
        	return CommonResult.success(null);
        }
    }
    
    @ApiOperation(value = "創建用戶")
    @PostMapping(value = "/")
    public CommonResult<?> create(@Valid @RequestBody UmsAdminDto admin, BindingResult result) {
    	int count = adminService.create(admin);
    	if (count > 0) {
    		return CommonResult.success(null);
    	} else {
    		return CommonResult.failed();
    	}
    }
    
    @ApiOperation(value = "修改用戶")
    @PutMapping(value = "/{id:\\d+}")
    public CommonResult<?> update(@PathVariable(value = "id") Long id, @RequestBody UmsAdminDto admin) {
    	int count = adminService.update(id, admin);
    	if (count > 0) {
    		return CommonResult.success(null);
    	} else {
    		return CommonResult.failed();
    	}
    }
    
    @ApiOperation(value = "獲取用戶所在部門的層級關係")
    @GetMapping(value = "/company/relation/{id:\\d+}")
    public CommonResult<?> queryCompanyRelation(@PathVariable(value = "id") Long userid) {
    	List<Long> companyIds = adminService.queryCompanyRelation(userid);
    	List<String> companyIds_ = new ArrayList<>();
    	for (Long id : companyIds) {
    		companyIds_.add(id + "");
    	}
    	return CommonResult.success(companyIds_);
    }
    
    @ApiOperation(value = "啟用、禁用用戶")
    @PutMapping(value = "/disable/{id:\\d+}/{status:^[01]$}")
    public CommonResult<?> disable(@PathVariable(value = "id") Long id, @PathVariable(value = "status") Integer status) {
    	int count = adminService.disable(id, status);
    	if (count > 0) {
    		return CommonResult.success(null);
    	} else {
    		return CommonResult.failed();
    	}
    }
    
    @ApiOperation(value = "重置密碼")
    @PutMapping(value = "/reset/password/{id:\\d+}")
    public CommonResult<?> resetPassword(@PathVariable(value = "id") Long userid) {
    	int count = adminService.resetPassword(userid);
    	if (count > 0) {
    		return CommonResult.success(null);
    	} else {
    		return CommonResult.failed();
    	}
    }
    
    @ApiOperation(value = "查詢部門所有用戶")
    @GetMapping(value = "/company/{id:\\d+}")
    public CommonResult<?> queryCompanyUsers(@PathVariable(value = "id") Long companyId) {
    	List<UmsAdmin> users = adminService.queryByCompany(companyId);
    	return CommonResult.success(users);
    }
}
