package com.summer.shortlink.admin.controller;


import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    // 配合lombok的RequiredArgsConstructor构造器注入注入进来
    private final UserService userService;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public UserRespDTO getUserByUsername(@PathVariable("username") String username){
        return userService.getUserByUsername(username);
    }
}
