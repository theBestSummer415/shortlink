package com.summer.shortlink.project.dao.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName("t_link_goto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkGotoDO {

    private Long id;

    private String gid;

    private String fullShortUrl;
}
