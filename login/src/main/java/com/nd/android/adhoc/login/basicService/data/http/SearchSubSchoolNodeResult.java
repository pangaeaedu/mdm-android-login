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
        @SerializedName("schoolid")
        private String schoolid = "";

        @SerializedName("district")
        private String district = null;

        @SerializedName("governorate")
        private String governorate = null;


        public void setSchoolid(String schoolid) {
            this.schoolid = schoolid;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public void setGovernorate(String governorate) {
            this.governorate = governorate;
        }

        public String getSchoolID(){
            return schoolid;
        }

        public String getDistrict(){
            return district;
        }

        public String getGovernorate(){
            return governorate;
        }
    }

}
