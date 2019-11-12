package com.nd.android.adhoc.db.operator;

import com.nd.android.adhoc.db.constant.MdmRunInfoDbConstant;
import com.nd.android.adhoc.db.operator.intfc.IMdmRunInfoDbOperator;

/**
 * Created by linsj on 2019/8/23.
 */

public class MdmRunInfoDbOperatorFactory {

    private volatile static MdmRunInfoDbOperatorFactory sInstance = null;

    private IMdmRunInfoDbOperator mRunInfoDbOperator;

    private final byte[] mH5Lock = new byte[0];


    public static MdmRunInfoDbOperatorFactory getInstance() {
        if (sInstance == null) {
            synchronized (MdmRunInfoDbOperatorFactory.class) {
                if (sInstance == null) {
                    sInstance = new MdmRunInfoDbOperatorFactory();
                }
            }
        }
        return sInstance;
    }

    public IMdmRunInfoDbOperator getRunInfoDbOperator() {
        if (mRunInfoDbOperator == null) {
            synchronized (mH5Lock) {
                if (mRunInfoDbOperator == null) {
                    mRunInfoDbOperator = new MdmRunInfoDbOperator(MdmRunInfoDbConstant.MDM_RUNINFO_DB_NAME);
                }
            }
        }
        return mRunInfoDbOperator;
    }
}
