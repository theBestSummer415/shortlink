package com.summer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.summer.shortlink.admin.dao.entity.GroupDO;
import com.summer.shortlink.admin.dto.req.GroupSaveReqDTO;
import com.summer.shortlink.admin.dto.resp.GroupRespDTO;

import java.util.List;

public interface GroupService extends IService<GroupDO> {
    void saveGroup(GroupSaveReqDTO groupName);

    List<GroupRespDTO> listGroup();
}
