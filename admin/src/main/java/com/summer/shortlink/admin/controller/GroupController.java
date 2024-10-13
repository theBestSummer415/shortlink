package com.summer.shortlink.admin.controller;

import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接分组服务层
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/group")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public Result<Void> saveGroup(@RequestBody GroupSaveReqDTO requestParam){

        groupService.saveGroup(requestParam);
        return Results.success();
    }
}
