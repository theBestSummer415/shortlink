package com.summer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.summer.shortlink.admin.common.biz.user.UserContext;
import com.summer.shortlink.admin.dao.entity.GroupDO;
import com.summer.shortlink.admin.dao.mapper.GroupMapper;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.dto.req.GroupSortReqDTO;
import com.summer.shortlink.admin.dto.req.GroupUpdateReqDTO;
import com.summer.shortlink.admin.dto.resp.GroupRespDTO;
import com.summer.shortlink.admin.remote.service.ShortLinkRemoteService;
import com.summer.shortlink.admin.service.GroupService;
import com.summer.shortlink.admin.util.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.summer.shortlink.admin.common.constant.GroupStateConstant.*;

/**
 * 短链接分组服务层
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {
    private final ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService() {
    };

    @Override
    public void saveGroup(GroupSaveReqDTO requestParam) {
        String gid = RandomGenerator.generateRandom6();
        while (hasGid(gid)) {
            gid = RandomGenerator.generateRandom6();
        }

        GroupDO groupDO = GroupDO.builder()
                .name(requestParam.getName())
                .gid(gid)
                .username(UserContext.getUsername())
                .sortOrder(GROUP_SORT_ORDER_DEFAULT)
                .build();
        baseMapper.insert(groupDO);
    }

    @Override
    public List<GroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDO> wrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE)
                // 登录过后，带着token能够访问到用户信息（存在UserContext中）
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder)
                .orderByDesc(GroupDO::getUpdateTime);
        List<GroupDO> groupDOList = baseMapper.selectList(wrapper);
        Map<String, Integer> groupLinkCountMap = shortLinkRemoteService.listGroupLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        List<GroupRespDTO> groupRespDTOS = BeanUtil.copyToList(groupDOList, GroupRespDTO.class);
        groupRespDTOS.forEach(each->{each.setShortLinkCount(groupLinkCountMap.get(each.getGid()));});
        return groupRespDTOS;
    }


    // TODO: group最开始分片键是name，导致这里更新name一直不成功
    @Override
    public boolean updateGroup(GroupUpdateReqDTO requestParam) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE);
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        groupDO.setUsername(UserContext.getUsername());

        return baseMapper.update(groupDO, updateWrapper) > 0;
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE)
                .set(GroupDO::getDelFlag, GROUP_DELETE_FLAG_TRUE);
        baseMapper.update(null, updateWrapper);
    }

    @Override
    public void sortGroup(List<GroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE)
                    .set(GroupDO::getSortOrder, each.getSortOrder());
            baseMapper.update(null, updateWrapper);
        });
    }

    private boolean hasGid(String gid) {
        LambdaQueryWrapper<GroupDO> eq = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, GROUP_DELETE_FLAG_FALSE)
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getUsername, UserContext.getUsername());

        return baseMapper.selectOne(eq) != null;
    }
}
