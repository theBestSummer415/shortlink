package com.summer.shortlink.admin.controller;


import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.summer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.summer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.summer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/user")
public class UserController {

    // 配合lombok的RequiredArgsConstructor构造器注入注入进来
    private final UserService userService;

    @GetMapping("/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 查询用户名是否存在（这样所有请求会直接打在数据库上）
     * @param username 查询的用户名
     * @return 存在返回true，不存在返回false
     */
    @GetMapping("/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username")String username){
        return Results.success(userService.hasUsername(username));
    }

    @PostMapping
    public Result<Void> register(@RequestBody UserRegisterReqDTO requestParam){
        userService.register(requestParam);
        return Results.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam){
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     * @return 用户登录返回参数，包含token
     */
    @PostMapping("/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam){
        return Results.success(userService.login(requestParam));
    }

    @GetMapping("/check-login")
    public Result<Boolean> checkLogin(@RequestParam String username, @RequestParam String token){
        return Results.success(userService.checkLogin(username, token));
    }

    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestParam String username, @RequestParam String token){
        userService.logout(username, token);
        return Results.success();
    }
}
