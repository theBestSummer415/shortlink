package com.summer.shortlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summer.shortlink.project.common.convention.result.Result;
import com.summer.shortlink.project.common.convention.result.Results;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.summer.shortlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    @PostMapping("/api/short-link/project/v1/link/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){

        return Results.success(shortLinkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     * @param requestParam gid
     * @return
     */
    @GetMapping("/api/short-link/project/v1/link/page")
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam){
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组中的链接数量
     * @param requestParam gids
     * @return
     */
    @GetMapping("/api/short-link/project/v1/link/count")
    public Result<List<ShortLinkGroupCountRespDTO>> listGroupLinkCount(@RequestParam List<String> requestParam){
        return Results.success(shortLinkService.listGroupLinkCount(requestParam));
    }

    @PostMapping("/api/short-link/project/v1/link/update")
    public Result<Void> updateLink(@RequestBody ShortLinkUpdateReqDTO requestParam){

        shortLinkService.updateLink(requestParam);
        return Results.success();
    }

    @GetMapping("/{short-uri}")
    public void redirectUrl(@PathVariable("short-uri")String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.redirectUrl(shortUri, request, response);

    }
}
