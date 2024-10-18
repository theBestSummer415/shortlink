package com.summer.shortlink.admin.remote.dto.resp;


import lombok.Data;

@Data
public class ShortLinkGroupCountRespDTO {

    private String gid;

    private Integer shortLinkCount;
}
