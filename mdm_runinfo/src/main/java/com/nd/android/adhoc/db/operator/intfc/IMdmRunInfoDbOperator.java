package com.nd.android.adhoc.db.operator.intfc;


import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;

import java.util.List;

/**
 * Created by linsj on 2018/4/11 0011.
 */

public interface IMdmRunInfoDbOperator {

    List<IMdmRunInfoEntity> getCurDayRunInfo();

    /**
     * 获取需要上报的列表，会过滤不需要上报的，比如小于3分钟运行时长的
     * @return
     */
    List<IMdmRunInfoEntity> getToReportRunInfo();

    boolean deleteUnUseableRunInfo();

    boolean deleteRunInfo(List<IMdmRunInfoEntity> listEntity);

    boolean saveOrUpdateRunInfo(List<IMdmRunInfoEntity> listEntity);

    boolean deleteAllData();

    boolean dropTable();
}
