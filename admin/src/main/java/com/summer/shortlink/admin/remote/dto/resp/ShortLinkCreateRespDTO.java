package com.summer.shortlink.admin.remote.dto.resp;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接创建请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkCreateRespDTO {

    /**
     * 分组gid
     */
    private String gid;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     *短链接
     */
    private String fullShortUrl;



}
