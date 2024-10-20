package com.summer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.project.common.convention.exception.ClientException;
import com.summer.shortlink.project.common.convention.exception.ServiceException;
import com.summer.shortlink.project.common.enums.ValiDateTypeEnum;
import com.summer.shortlink.project.dao.entity.ShortLinkDO;
import com.summer.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.summer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.summer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.summer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.summer.shortlink.project.service.ShortLinkService;
import com.summer.shortlink.project.util.HashUtil;
import com.summer.shortlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.summer.shortlink.project.common.constant.RedisKeyConstant.*;
import static com.summer.shortlink.project.common.constant.ShortLinkConstant.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCacheBloomFilter;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(SHORT_LINK_ENABLE_TRUE);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;
        shortLinkDO.setFullShortUrl(fullShortUrl);

        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
            // TODO: 感觉这一部分逻辑是多余的，这一部分的逻辑和抛出DuplicateKeyException的逻辑是一样的
            LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO selectOne = baseMapper.selectOne(eq);
            if (selectOne != null) {
                log.warn("短链接{}重复入库", fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }

        }

        // TODO-Done: 缓存预热
        redisTemplate.opsForValue().set(
                StrUtil.format(GOTO_SHORT_LINK_KEY_FORMAT, fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidDate(requestParam.getValidDate()),
                TimeUnit.MILLISECONDS
        );
        shortUriCreateCacheBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(SHORT_LINK_HTTP_PROTOCOL_STR + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, SHORT_LINK_DEL_FALSE)
                .eq(ShortLinkDO::getEnableStatus, SHORT_LINK_ENABLE_TRUE);
        IPage<ShortLinkDO> selectPage = baseMapper.selectPage(requestParam, eq);
        return selectPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain(SHORT_LINK_HTTP_PROTOCOL_STR + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> listGroupLinkCount(List<String> requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .select(ShortLinkDO::getGid, ShortLinkDO::getShortLinkCount)
                .in(ShortLinkDO::getGid, requestParam)
                .groupBy(ShortLinkDO::getGid);
        List<ShortLinkDO> shortLinkDOS = baseMapper.selectList(queryWrapper);


        return BeanUtil.copyToList(shortLinkDOS, ShortLinkGroupCountRespDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLink(ShortLinkUpdateReqDTO requestParam) {

        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, SHORT_LINK_DEL_FALSE)
                .eq(ShortLinkDO::getEnableStatus, SHORT_LINK_ENABLE_TRUE);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(hasShortLinkDO.getDomain())
                .shortUri(hasShortLinkDO.getShortUri())
                .clickNum(hasShortLinkDO.getClickNum())
                .favicon(hasShortLinkDO.getFavicon())
                .createdType(hasShortLinkDO.getCreatedType())
                .gid(requestParam.getGid())
                .originUrl(requestParam.getOriginUrl())
                .describe(requestParam.getDescribe())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .build();
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, SHORT_LINK_DEL_FALSE)
                    .eq(ShortLinkDO::getEnableStatus, SHORT_LINK_ENABLE_TRUE)
                    .set(Objects.equals(requestParam.getValidDateType(), ValiDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);

            baseMapper.update(shortLinkDO, updateWrapper);
        } else {

            LambdaUpdateWrapper<ShortLinkDO> linkUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);

            baseMapper.delete(linkUpdateWrapper);
            shortLinkDO.setGid(requestParam.getGid());
            baseMapper.insert(shortLinkDO);

        }

    }

    @Override
    public void redirectUrl(String shortUri, ServletRequest request, ServletResponse response) throws IOException {
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUri;
        String originalLink = redisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY_FORMAT, fullShortUrl));

        // TODO-Done: 布隆过滤器
        if( ! shortUriCreateCacheBloomFilter.contains(fullShortUrl)){
            ((HttpServletResponse) response).sendRedirect(NOT_FOUND_REDIRECT);
            return;
        }
        // TODO-Done：空值查询
        String nullValue = redisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_NULL_VALUE_KEY_FORMAT, fullShortUrl));
        if (StrUtil.isNotBlank(nullValue)){
            ((HttpServletResponse) response).sendRedirect(NOT_FOUND_REDIRECT);
            return;
        }


        if(StrUtil.isBlank(originalLink)){
            // TODO-Done: 分布式锁，防止key失效，大量请求同时打到数据库，这里用分布式锁
            RLock lock = redissonClient.getLock(String.format(GOTO_SHORT_LINK_LOCK_KEY_FORMAT, fullShortUrl));
            lock.lock();

            try {
                originalLink = redisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY_FORMAT, fullShortUrl));
                if (StrUtil.isNotBlank(originalLink)){
                    ((HttpServletResponse) response).sendRedirect(originalLink);
                    return;
                }


                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper1 = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper1);
                if(shortLinkGotoDO == null){
                    //TODO: 此处需要进行风控，防止攻击者一直请求不存在的shortUrl
                    redisTemplate.opsForValue().set(
                            String.format(GOTO_SHORT_LINK_NULL_VALUE_KEY_FORMAT, fullShortUrl),
                            GOTO_SHORT_LINK_NULL_VALUE,
                            NULL_VALUE_EXPIRE,
                            NULL_VALUE_EXPIRE_UNIT
                    );
                    ((HttpServletResponse) response).sendRedirect(NOT_FOUND_REDIRECT);
                    return;
                }

                LambdaQueryWrapper<ShortLinkDO> queryWrapper2 = Wrappers.lambdaQuery(ShortLinkDO.class)
                        .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                        .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                        .eq(ShortLinkDO::getDelFlag, SHORT_LINK_DEL_FALSE)
                        .eq(ShortLinkDO::getEnableStatus, SHORT_LINK_ENABLE_TRUE);
                ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper2);

                if (shortLinkDO != null){
                    // TODO: 有效期已经再当前时间以前，缓存置空，但数据库里的数据还没有处理
                    if(shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date())){
                        redisTemplate.opsForValue().set(
                                String.format(GOTO_SHORT_LINK_NULL_VALUE_KEY_FORMAT, fullShortUrl),
                                GOTO_SHORT_LINK_NULL_VALUE,
                                NULL_VALUE_EXPIRE,
                                NULL_VALUE_EXPIRE_UNIT
                        );
                        ((HttpServletResponse) response).sendRedirect(NOT_FOUND_REDIRECT);
                    }

                    redisTemplate.opsForValue().set(
                            StrUtil.format(GOTO_SHORT_LINK_KEY_FORMAT, fullShortUrl),
                            shortLinkDO.getOriginUrl(),
                            LinkUtil.getLinkCacheValidDate(shortLinkDO.getValidDate()),
                            TimeUnit.MILLISECONDS
                    );
                    ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
                }
            }finally {
                lock.unlock();
            }

        }else{
            ((HttpServletResponse) response).sendRedirect(originalLink);
        }




    }


    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int generateCount = 0;
        String shortUri;
        while (true) {
            if (generateCount > SHORT_LINK_MAX_GENERATE_TIMES) {
                throw new ServiceException("短链接生成频繁，请稍后再试");
            }
            // TODO: 用系统时间加盐做hash
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl() + System.currentTimeMillis());
            if (!shortUriCreateCacheBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)) {
                break;
            }
            generateCount++;
        }

        return shortUri;
    }
}
