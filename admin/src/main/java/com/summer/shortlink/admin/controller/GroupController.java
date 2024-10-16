package com.summer.shortlink.admin.controller;

import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.dto.req.GroupSortReqDTO;
import com.summer.shortlink.admin.dto.req.GroupUpdateReqDTO;
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

    /**
     * 查询短链接分组集合

     */
    @GetMapping
    public Result<List<GroupRespDTO>> listGroup(){
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组名称
     * @param requestParam：gid和新名称
     */
    @PutMapping
    public Result<Boolean> updateGroup(@RequestBody GroupUpdateReqDTO requestParam){
        return Results.success(groupService.updateGroup(requestParam));
    }

    /**
     * 删除短链接分组
     */
    @DeleteMapping
    public Result<Void> updateGroup(@RequestParam String gid){
        groupService.deleteGroup(gid);
        return Results.success();
    }

    /**
     * 短链接分组排序
     */
    @PostMapping("/sort")
    public Result<Void> sortGroup(@RequestBody List<GroupSortReqDTO> requestParam){
        groupService.sortGroup(requestParam);
        return Results.success();
    }
}
