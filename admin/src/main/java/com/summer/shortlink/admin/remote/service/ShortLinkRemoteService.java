package com.summer.shortlink.admin.remote.service;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.summer.shortlink.admin.common.convention.result.Result;
import com.summer.shortlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.admin.remote.dto.req.ShortLinkUpdateReqDTO;
import com.summer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.summer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ShortLinkRemoteService {

    default Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/project/v1/link/page", requestMap);

        return JSON.parseObject(resultPageStr, new TypeReference<Result<IPage<ShortLinkPageRespDTO>>>() {
        });
    }

    /**
     * 按分组查询短链接量
     *
     * @param requestParam gids
     * @return (gid, count)
     */
    default Map<String, Integer> listGroupLinkCount(List<String> requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("requestParam", requestParam);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/short-link/project/v1/link/count", requestMap);

        List<ShortLinkGroupCountRespDTO> respData = JSON.parseObject(resultPageStr, new TypeReference<Result<List<ShortLinkGroupCountRespDTO>>>() {
        }).getData();
        return respData.stream().collect(Collectors.toMap(
                each -> each.getGid(),
                each -> each.getShortLinkCount(),
                (existing, replacement) -> existing
        ));

    }

    default void updateShortLink(ShortLinkUpdateReqDTO requestParam) {

        HttpUtil.post("http://127.0.0.1:8001/api/short-link/project/v1/link/update", JSON.toJSONString(requestParam));


    }
}
