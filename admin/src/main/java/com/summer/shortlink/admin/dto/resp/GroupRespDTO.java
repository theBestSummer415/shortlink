package com.summer.shortlink.admin.dto.resp;

import lombok.Data;

@Data
public class GroupRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 下短链接数量
     */
    private Integer shortLinkCount;
}
