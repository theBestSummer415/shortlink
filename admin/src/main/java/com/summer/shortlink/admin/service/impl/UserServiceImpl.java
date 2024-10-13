package com.summer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.admin.common.convention.exception.ClientException;
import com.summer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.summer.shortlink.admin.dao.entity.UserDO;
import com.summer.shortlink.admin.dao.mapper.UserMapper;
import com.summer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import static com.summer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.summer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_FAIL;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;

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

    @Override
    public Boolean hasUsername(String username) {
        return userRegisterCachePenetrationBloomFilter.contains(username);

    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(hasUsername(requestParam.getUsername())){
            throw new ClientException(USER_NAME_EXIST);
        }
        // TODO
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(requestParam, userDO);
        int insert = baseMapper.insert(userDO);
        if(insert < 1) throw new ClientException(USER_SAVE_FAIL);

        userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
    }


}
