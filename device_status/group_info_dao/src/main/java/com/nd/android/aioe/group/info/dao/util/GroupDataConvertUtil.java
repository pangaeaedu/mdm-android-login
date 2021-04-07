package com.nd.android.aioe.group.info.dao.util;

import com.nd.android.aioe.group.info.dao.api.bean.GroupNodeInfo;
import com.nd.android.aioe.group.info.dao.api.bean.GroupPageNodeInfo;

public class GroupDataConvertUtil {

    public static GroupNodeInfo convertFrom(GroupPageNodeInfo pNode) {
        GroupNodeInfo result = new GroupNodeInfo();
        result.setId(pNode.getGroupCode());
        result.setText(pNode.getName());
        result.setHasSon(pNode.getHasSon());
        return result;
    }

}
