package com.summer.shortlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.summer.shortlink.project.dao.entity.ShortLinkDO;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     * @param requestParam
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);


    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    List<ShortLinkGroupCountRespDTO> listGroupLinkCount(List<String> requestParam);

    void updateLink(ShortLinkUpdateReqDTO requestParam);

    void redirectUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException;
}
