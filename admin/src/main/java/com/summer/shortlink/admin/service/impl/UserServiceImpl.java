package com.summer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.admin.common.convention.exception.ClientException;
import com.summer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.summer.shortlink.admin.dao.entity.UserDO;
import com.summer.shortlink.admin.dao.mapper.UserMapper;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(eq);
        UserRespDTO result = new UserRespDTO();

        if(userDO == null)
            throw new ClientException(UserErrorCodeEnum.USER_NOT_EXIST);

        BeanUtils.copyProperties(userDO, result);
        return result;
    }


}
