package com.summer.shortlink.admin.controller;


import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    // 配合lombok的RequiredArgsConstructor构造器注入注入进来
    private final UserService userService;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable("username") String username){
        UserRespDTO result = userService.getUserByUsername(username);
        return Results.success(result);
    }

    /**
     * 查询用户名是否存在（这样所有请求会直接打在数据库上）
     * @param username 查询的用户名
     * @return 存在返回false，不存在返回false
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUsername(@RequestParam("username")String username){
        return Results.success(userService.hasUsername(username));
    }
}
