package com.summer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.summer.shortlink.admin.dao.entity.GroupDO;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(GroupSaveReqDTO groupName);
}
