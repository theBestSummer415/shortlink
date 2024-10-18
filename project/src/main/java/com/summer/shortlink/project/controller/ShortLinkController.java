package com.summer.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summer.shortlink.project.common.convention.result.Result;
import com.summer.shortlink.project.common.convention.result.Results;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.summer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/short-link/project/v1/link")
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){

        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     * @param requestParam gid
     * @return
     */
    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组中的链接数量
     * @param requestParam gids
     * @return
     */
    @GetMapping("/count")
    public Result<List<ShortLinkGroupCountRespDTO>> listGroupLinkCount(@RequestParam List<String> requestParam){
        return Results.success(shortLinkService.listGroupLinkCount(requestParam));
    }
}
