package com.summer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.project.common.convention.exception.ServiceException;
import com.summer.shortlink.project.dao.entity.ShortLinkDO;
import com.summer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.summer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.summer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.summer.shortlink.project.service.ShortLinkService;
import com.summer.shortlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import static com.summer.shortlink.project.common.constant.ShortLinkConstant.SHORT_LINK_ENABLE_TRUE;
import static com.summer.shortlink.project.common.constant.ShortLinkConstant.SHORT_LINK_MAX_GENERATE_TIMES;


@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortUriCreateCacheBloomFilter;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUri(shortLinkSuffix);
        shortLinkDO.setEnableStatus(SHORT_LINK_ENABLE_TRUE);
        String fullShortUrl = requestParam.getDomain() + "/" + shortLinkSuffix;
        shortLinkDO.setFullShortUrl(fullShortUrl);
        try {
            baseMapper.insert(shortLinkDO);
        }catch (DuplicateKeyException ex){
            // TODO: 感觉这一部分逻辑是多余的，这一部分的逻辑和抛出DuplicateKeyException的逻辑是一样的
            LambdaQueryWrapper<ShortLinkDO> eq = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl);
            ShortLinkDO selectOne = baseMapper.selectOne(eq);
            if(selectOne != null){
                log.warn("短链接{}重复入库", fullShortUrl);
                throw new ServiceException("短链接生成重复");
            }

        }

        shortUriCreateCacheBloomFilter.add(fullShortUrl);

        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int generateCount = 0;
        String shortUri;
        while (true){
            if (generateCount > SHORT_LINK_MAX_GENERATE_TIMES) {
                throw new ServiceException("短链接生成频繁，请稍后再试");
            }
            // TODO: 用系统时间加盐做hash
            shortUri = HashUtil.hashToBase62(requestParam.getOriginUrl() + System.currentTimeMillis());
            if(!shortUriCreateCacheBloomFilter.contains(requestParam.getDomain() + "/" + shortUri)){
                break;
            }
            generateCount++;
        }

        return shortUri;
    }
}
