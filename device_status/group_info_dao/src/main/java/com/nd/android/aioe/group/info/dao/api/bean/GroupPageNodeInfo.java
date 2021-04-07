package com.nd.android.aioe.group.info.dao.api.bean;

/**
 * Created by Administrator on 2019/10/18 0018.
 * "name": "321321",
 "groupCode": "000Y",
 "property": 0,
 "hasSon": 0,
 "mdTime": 1571369517298,
 "type": 2,
 "crTime": 1571369517298
 */

public class GroupPageNodeInfo {
    private String name;
    private String groupCode;
    private int property;
    private int hasSon;
    private long mdTime;
    private int type;
    private long crTime;

    public GroupPageNodeInfo(){
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String pGroupCode) {
        groupCode = pGroupCode;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int pProperty) {
        property = pProperty;
    }

    public int getHasSon() {
        return hasSon;
    }

    public void setHasSon(int pHasSon) {
        hasSon = pHasSon;
    }

    public long getMdTime() {
        return mdTime;
    }

    public void setMdTime(long pMdTime) {
        mdTime = pMdTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int pType) {
        type = pType;
    }

    public long getCrTime() {
        return crTime;
    }

    public void setCrTime(long pCrTime) {
        crTime = pCrTime;
    }
}
