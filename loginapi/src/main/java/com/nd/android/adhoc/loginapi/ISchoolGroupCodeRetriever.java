package com.nd.android.adhoc.loginapi;

public interface ISchoolGroupCodeRetriever {
    String retrieveGroupCode(String pRootCode) throws Exception;

    String onGroupNotFound(String pRootCode) throws Exception;
}
