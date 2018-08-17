package com.nd.android.mdm.mdm_feedback_biz;

public interface IFeedbackOperator {
    String getCmdName();
    void operate(String pContent);
}
