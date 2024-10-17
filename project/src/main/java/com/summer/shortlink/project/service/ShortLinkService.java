package com.summer.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.summer.shortlink.project.dao.entity.ShortLinkDO;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

public interface ShortLinkService extends IService<ShortLinkDO> {
    /**
     * 创建短链接
     * @param requestParam
     * @return 短链接创建信息
     */
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);
}
