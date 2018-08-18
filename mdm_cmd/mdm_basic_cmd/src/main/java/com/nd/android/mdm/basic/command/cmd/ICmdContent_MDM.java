package com.nd.android.mdm.basic.command.cmd;

import com.nd.android.adhoc.basic.command.cmd.ICmdContent;

/**
 * 指令内容，有些指令除了 json 之外，还会有一些其他的扩展信息
 * <p>
 * Created by HuangYK on 2018/4/29.
 */
public interface ICmdContent_MDM extends ICmdContent {//<ICmdContent_MDM> {
//
//    ICmdContent_MDM setFrom(int pFrom);
//
//    ICmdContent_MDM setTo(int pTo);
//
//    ICmdContent_MDM setCmdType(int pCmdType);

    int getFrom();

    int getTo();

    /**
     * 命令类型
     *
     * @return 命令类型
     */
    int getCmdType();

}
