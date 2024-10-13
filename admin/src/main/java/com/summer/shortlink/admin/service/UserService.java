package com.summer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.summer.shortlink.admin.dao.entity.UserDO;
import com.summer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.summer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.summer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.summer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;

public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 判断用户名是否存在
     * @param username 需要查询的用户名
     * @return 存在返回true，不存在返回false
     */
    Boolean hasUsername(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 更新用户信息
     * @param requestParam 更新用户信息参数
     */
    void update(UserUpdateReqDTO requestParam);

    /**
     * 用户登录
     * @param requestParam 用户登录参数，包含用户名和密码
     * @return token
     */
    UserLoginRespDTO login(UserLoginReqDTO requestParam);

    /**
     * 检查用户是否登录
     * @param username 用户名
     * @param token 用户token
     * @return true表示已经登陆，false表示未登录
     */
    Boolean checkLogin(String username, String token);

    void logout(String username, String token);
}
