package com.summer.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 有效期类型
 */
@RequiredArgsConstructor
public enum ValiDateTypeEnum {

    PERMANENT(0),

    CUSTOM(1);

    @Getter
    private final int type;
}
