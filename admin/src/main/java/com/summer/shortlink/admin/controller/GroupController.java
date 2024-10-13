package com.summer.shortlink.admin.controller;

import com.summer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接分组服务层
 */

@RestController
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
}
