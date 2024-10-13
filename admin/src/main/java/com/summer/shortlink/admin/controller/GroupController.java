package com.summer.shortlink.admin.controller;

import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.dto.resp.GroupRespDTO;
import com.summer.shortlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组服务层
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/short-link/admin/v1/group")
public class GroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     * @param requestParam 短链接分组名
     * @return
     */
    @PostMapping
    public Result<Void> saveGroup(@RequestBody GroupSaveReqDTO requestParam){
        groupService.saveGroup(requestParam);
        return Results.success();
    }

    @GetMapping
    public Result<List<GroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }
}
