package com.nd.android.mdm.event_define;

/**
 * Created by yaoyue1019 on 2-25.
 */
@Deprecated
public class LogEvent extends Event {
    //    public static final int ACTION_ADD_LOG = 1;
//    public static final int ACTION_CLEAR = 2;
//    public static final int ACTION_SHOW = 3;
//    public static final int ACTION_HIDE = 4;
//
//    public static final String TYPE_D = "#0000F0";
//    public static final String TYPE_I = "#00F000";
//    public static final String TYPE_W = "#FFF000";
//    public static final String TYPE_E = "#F00000";
    public String content;
    public int level;
    public String tag;
    /**
     * 消息类型 1接收 0回复
     */
    public int msgType;
    /**
     * 会话ID
     */
    public String sessionId;
    /**
     * 指令名称
     */
    public String cmdName;
    /**
     * 指令执行状态
     */
    public int errorCode;

    public int from;



    public LogEvent(int level, String tag, String content, int msgType, String sessionId, String cmdName, int errorCode, int type){
        this.level=level;
        this.tag=tag;
        this.content=content;
        this.msgType=msgType;
        this.sessionId=sessionId;
        this.cmdName=cmdName;
        this.errorCode=errorCode;
        this.from=type;
    }


    @Override
    public String toString() {
        return content;
    }

    @Override
    public void post() {
    }
}
