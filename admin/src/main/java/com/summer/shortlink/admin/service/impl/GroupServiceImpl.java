package com.summer.shortlink.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.admin.dao.entity.GroupDO;
import com.summer.shortlink.admin.dao.mapper.GroupMapper;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.service.GroupService;
import com.summer.shortlink.admin.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.summer.shortlink.admin.common.constant.GroupStateConstant.GROUP_DELETE_FLAG_FALSE;

/**
 * 短链接分组服务层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        String gid = RandomGenerator.generateRandom6();
        while (hasGid(gid)){
            gid = RandomGenerator.generateRandom6();
        }

        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .gid(gid)
                .build();
        baseMapper.insert(groupDO);
    }

    private boolean hasGid(String gid){
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE)
                .eq(GroupDO::getGid, gid)
                // TODO: 传入用户名
                .eq(GroupDO::getUsername, null);

        return baseMapper.selectOne(eq) != null;
    }
}
