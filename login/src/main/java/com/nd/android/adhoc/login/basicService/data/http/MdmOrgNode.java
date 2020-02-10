package com.nd.android.adhoc.login.basicService.data.http;

/**
 {
 "children": [],
 "id": "0090010101",
 "levelType": "1", // (1,"country"),(2,"province"),(3,"city"),(4,"x4"),(5,"x5"),(6,"school"),(7,"grade"),(8,"class");
 "levelTypeName": "country",
 "nodeType": "group",
 "onlineStatus": false,
 "state": "closed",
 "text": "贝伊奥卢"
 }
 */

public class MdmOrgNode {
    private String id = "";
    private String levelType = "";
    private String levelTypeName = "";
    private String nodeType = "";

    private boolean onlineStatus = false;
    private String state = "";
    private String text = "";

    public MdmOrgNode() {
        super();
    }

    public String getId(){
        return id;
    }

    public void setId(String pID){
        id = pID;
    }

    public String getText(){
        return text;
    }

    public void setText(String pText){
        text = pText;
    }
}
