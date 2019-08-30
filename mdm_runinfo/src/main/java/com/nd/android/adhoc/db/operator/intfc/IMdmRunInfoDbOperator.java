package com.nd.android.adhoc.db.operator.intfc;


import com.nd.android.adhoc.db.entity.intfc.IMdmRunInfoEntity;

import java.util.List;

/**
 * Created by linsj on 2018/4/11 0011.
 */

public interface IMdmRunInfoDbOperator {

    List<IMdmRunInfoEntity> getCurDayRunInfo();

    List<IMdmRunInfoEntity> getToReportRunInfo();

    boolean deleteUnUseableRunInfo();

    boolean deleteRunInfo(List<IMdmRunInfoEntity> listEntity);

    boolean saveOrUpdateRunInfo(List<IMdmRunInfoEntity> listEntity);
}
