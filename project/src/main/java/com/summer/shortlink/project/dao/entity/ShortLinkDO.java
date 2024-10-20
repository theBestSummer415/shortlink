package com.summer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@TableName("t_link")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkDO extends BaseDO{


    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 网站图标
     */
    private String favicon;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private int enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private int createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private int validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    // TODO: 这个字段是mysql关键字！！！
    @TableField("`describe`")
    private String describe;

    /**
     * 历史pv
     */
    private Integer totalPv;

    /**
     * 历史uv
     */
    private Integer totalUv;

    /**
     * 历史uip
     */
    private Integer totalUip;


    /**
     * 统计数量
     */
    @TableField(value = "count(*)", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER, select = false)
    private Integer shortLinkCount;
}
