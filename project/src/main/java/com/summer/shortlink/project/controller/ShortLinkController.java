package com.summer.shortlink.project.controller;

import com.summer.shortlink.project.common.convention.result.Result;
import com.summer.shortlink.project.common.convention.result.Results;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/short-link/project/v1/link")
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;

    @PostMapping("/create")
    public Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam){

        return Results.success(shortLinkService.createShortLink(requestParam));
    }
}
