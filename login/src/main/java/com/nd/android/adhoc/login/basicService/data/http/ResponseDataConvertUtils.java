package com.nd.android.adhoc.login.basicService.data.http;

public class ResponseDataConvertUtils {
    public static MdmOrgNode convertFrom(GroupPageNode pNode){
        MdmOrgNode result = new MdmOrgNode();
        result.setId(pNode.getGroupCode());
        result.setText(pNode.getName());
        result.setHasSon(pNode.getHasSon());
        return result;
    }
}
