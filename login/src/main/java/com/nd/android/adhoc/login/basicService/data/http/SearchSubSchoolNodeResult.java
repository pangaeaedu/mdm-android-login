package com.nd.android.adhoc.login.basicService.data.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchSubSchoolNodeResult {
    @SerializedName("count_item")
    private int count_item = 0;

    public void setCount_item(int count_item) {
        this.count_item = count_item;
    }

    @SerializedName("list")
    private List<SearchSubSchoolNode> list;

    public int getCount_item() {
        return count_item;
    }

    public List<SearchSubSchoolNode> getList() {
        return list;
    }

    public void setList(List<SearchSubSchoolNode> list) {
        this.list = list;
    }

    public static class SearchSubSchoolNode{
        @SerializedName("groupcode")
        private String groupcode = "";

        @SerializedName("groupname")
        private String groupname = "";

        @SerializedName("district")
        private String district = null;

        @SerializedName("governorate")
        private String governorate = null;

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setGovernorate(String governorate) {
            this.governorate = governorate;
        }

        public String getDistrict(){
            return district;
        }

        public String getGovernorate(){
            return governorate;
        }

        public String getGroupcode() {
            return groupcode;
        }

        public void setGroupcode(String groupcode) {
            this.groupcode = groupcode;
        }

        public String getGroupname() {
            return groupname;
        }

        public void setGroupname(String groupname) {
            this.groupname = groupname;
        }
    }

}
