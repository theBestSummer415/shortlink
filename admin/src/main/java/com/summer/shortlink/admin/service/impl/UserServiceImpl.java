package com.summer.shortlink.admin.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.admin.common.biz.user.UserContext;
import com.summer.shortlink.admin.common.convention.exception.ClientException;
import com.summer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.summer.shortlink.admin.dao.entity.UserDO;
import com.summer.shortlink.admin.dao.mapper.UserMapper;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.summer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.summer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.summer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.summer.shortlink.admin.dto.resp.UserRespDTO;
import com.summer.shortlink.admin.service.GroupService;
import com.summer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.summer.shortlink.admin.common.constant.GroupStateConstant.GROUP_DEFAULT_NAME;
import static com.summer.shortlink.admin.common.constant.RedisCacheConstant.*;
import static com.summer.shortlink.admin.common.constant.UserConstant.USER_DELETE_FLAG_FALSE;
import static com.summer.shortlink.admin.common.enums.UserErrorCodeEnum.*;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    private final GroupService groupService;

    @Override
    public UserRespDTO getUserByUsername(String username) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class).eq(UserDO::getUsername, username);
        UserDO userDO = baseMapper.selectOne(eq);
        UserRespDTO result = new UserRespDTO();

        if (userDO == null)
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
        if (hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());

        try {
            if (lock.tryLock()) {
                UserDO userDO = new UserDO();
                BeanUtils.copyProperties(requestParam, userDO);

                try {
                    int insert = baseMapper.insert(userDO);
                    if (insert < 1) throw new ClientException(USER_SAVE_FAIL);
                }catch (DuplicateKeyException e){
                    throw new ClientException(USER_EXIST);
                }

                groupService.saveGroup(new GroupSaveReqDTO(GROUP_DEFAULT_NAME, requestParam.getUsername()));
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_NAME_EXIST);
        } finally {
            // TODO: tryLock失败好像也会走这里？
            lock.unlock();
        }


    }

    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // TODO: 验证当前用户名为已登录用户（不能修改别人的信息）
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, UserContext.getUsername());
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(requestParam, userDO);

        baseMapper.update(userDO, eq);
    }

    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        LambdaQueryWrapper<UserDO> eq = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, USER_DELETE_FLAG_FALSE);

        UserDO userDO = baseMapper.selectOne(eq);
        if (userDO == null) {
            throw new ClientException(USER_NOT_EXIST);
        }

        // TODO: 这种校验无法实现自动登录，自动登录应该再传入一个token，可以实现多设备登录
        Boolean haslogin = stringRedisTemplate.hasKey(LOGIN_PREFIX + requestParam.getUsername());
        if (haslogin != null && haslogin) {
            throw new ClientException(USER_ALREADY_LOGIN);
        }
        /**
         * Hash
         * Key: login_用户名
         * Value：
         *      key: token
         *      val:json
         */
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(LOGIN_PREFIX + requestParam.getUsername(), uuid, JSON.toJSONString(userDO));
        stringRedisTemplate.expire(LOGIN_PREFIX + requestParam.getUsername(), TIME_30, TimeUnit.MINUTES);
        return new UserLoginRespDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(LOGIN_PREFIX + username, token) != null;
    }

    @Override
    public void logout(String username, String token) {
        if(checkLogin(username, token))
            // TODO: 可以配合login方法实现多设备登录
            stringRedisTemplate.delete(LOGIN_PREFIX + username);
        else throw new ClientException(USER_NOT_LOGIN);
    }


}
