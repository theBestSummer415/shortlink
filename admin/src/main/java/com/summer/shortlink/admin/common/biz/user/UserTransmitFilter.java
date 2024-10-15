package com.summer.shortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.summer.shortlink.admin.common.constant.UserConstant;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import static com.summer.shortlink.admin.common.constant.RedisCacheConstant.LOGIN_PREFIX;
import static java.nio.charset.StandardCharsets.UTF_8;


@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    // TODO: 写死了需要忽略的URI，很丑陋
    private static final List<String> IGNORE_URI = Lists.newArrayList(
            "/api/short-link/admin/v1/user/login",
            "/api/short-link/admin/v1/user/has-username"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();

        if(!IGNORE_URI.contains(requestURI)) {
            String userId = httpServletRequest.getHeader(UserConstant.USER_ID_KEY);
            String userName = httpServletRequest.getHeader(UserConstant.USER_NAME_KEY);
            String realName = httpServletRequest.getHeader(UserConstant.REAL_NAME_KEY);
            if (StringUtils.hasText(userName)) {
                userName = URLDecoder.decode(userName, UTF_8);
            }
            if (StringUtils.hasText(realName)) {
                realName = URLDecoder.decode(realName, UTF_8);
            }
            String token = httpServletRequest.getHeader(UserConstant.USER_TOKEN_KEY);

            // 登录的serviceImpl里会把用户信息存入redis，下一次服务用户带token访问就可以获取到该用户的信息
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(LOGIN_PREFIX + userName, token);
            if (userInfoJsonStr != null) {
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }


            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                // TODO: 每次请求过来，会先执行service部分，再执行这个removeUser的操作
                UserContext.removeUser();
            }
        }
    }
}