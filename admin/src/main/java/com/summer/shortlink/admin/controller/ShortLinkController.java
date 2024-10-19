package com.summer.shortlink.admin.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.common.convention.result.Results;
import com.summer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.summer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.summer.shortlink.admin.remote.service.ShortLinkRemoteService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/short-link/admin/v1/link")
public class ShortLinkController {
    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @GetMapping("/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(@RequestBody ShortLinkPageReqDTO requestParam){

        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    @PostMapping("/update")
    public Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam){
        shortLinkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }
}
