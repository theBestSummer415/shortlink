package com.summer.shortlink.admin.common.enums;

import com.summer.shortlink.admin.common.convention.errorcode.IErrorCode;

public enum UserErrorCodeEnum implements IErrorCode {
    USER_TOKEN_FAIL("A000200", "用户Token验证失效"),

    USER_NOT_EXIST("B000200", "用户记录不存在"),
    USER_EXIST("B000201", "用户记录已存在"),
    USER_NAME_EXIST("B000202", "用户名已存在"),
    USER_SAVE_FAIL("B000203", "用户记录新增失败"),
    USER_ALREADY_LOGIN("B000204", "用户已登录"),
    USER_NOT_LOGIN("B000205", "用户未登录"),
    USER_SAVE_KEY_DUPLICATE("B000206", "用户插入键值重复");;


    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
