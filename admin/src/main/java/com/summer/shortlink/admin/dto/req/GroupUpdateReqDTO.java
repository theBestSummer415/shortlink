package com.summer.shortlink.admin.dto.req;


import lombok.Data;

@Data
public class GroupUpdateReqDTO {

    /**
     * 分组标识
     */
    private String gid;
    private String name;
}
