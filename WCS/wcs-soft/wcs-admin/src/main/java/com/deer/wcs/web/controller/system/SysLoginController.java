package com.deer.wcs.web.controller.system;

import java.util.List;
import java.util.Set;

import com.deer.wcs.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.deer.wcs.common.constant.Constants;
import com.deer.wcs.common.core.domain.Result;
import com.deer.wcs.common.core.domain.model.SysMenu;
import com.deer.wcs.common.core.domain.model.SysUser;
import com.deer.wcs.common.core.domain.model.LoginBody;
import com.deer.wcs.common.utils.SecurityUtils;
import com.deer.wcs.framework.web.service.SysLoginService;
import com.deer.wcs.framework.web.service.SysPermissionService;
import com.deer.wcs.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginBody loginBody)
    {
        Result ajax = Result.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
       // redisCache.setCacheObject("userName",loginBody.getUsername());
//        boolean pwdExpire = loginService.isPwdExpire(loginBody.getUsername());
//        if (pwdExpire) {
//            ajax.put("res_code", 1001);
//        }
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public Result getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        Result ajax = Result.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Result getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.success(menuService.buildMenus(menus));
    }
}
