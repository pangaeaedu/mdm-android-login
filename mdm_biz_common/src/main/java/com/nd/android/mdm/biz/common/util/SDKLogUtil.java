package com.nd.android.mdm.biz.common.util;

import android.Manifest;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.flattener.Flattener;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.elvishew.xlog.printer.file.naming.FileNameGenerator;
import com.nd.android.adhoc.basic.common.AdhocBasicConfig;
import com.nd.android.adhoc.basic.util.permission.AdhocRxPermissions;
import com.nd.android.adhoc.basic.util.thread.rx.AdhocActionSubscriber;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

/**
 * 为了在 Log 打印规范还未统一的情况下，确保其他模块的正常使用，先从 module_mdm_basic 中移过来，但是已经标注为废弃
 *
 * Created by yaoyue1019 on 10-31.
 */
@Deprecated
public class SDKLogUtil {
    static final int MAX_LOG_SIZE = 500;
    static Logger logger;
    static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    static List<String> lastLogs = new ArrayList<String>();

    static {
        AdhocRxPermissions.getInstance(AdhocBasicConfig.getInstance().getAppContext())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new AdhocActionSubscriber<>(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        XLog.init();
                        Printer loggerPrinter = new FilePrinter.Builder(new File("/sdcard", AdhocBasicConfig.getInstance().getAppContext().getPackageName()).getPath() + "/business")
                                .fileNameGenerator(new DateFileNameGenerator())
                                .logFlattener(new Flattener() {
                                    @Override
                                    public CharSequence flatten(int logLevel, String tag, String message) {
                                        return String.format("%s\t%s\t%s", format.format(new Date()), tag, message);
                                    }
                                })
                                .build();
                        logger = new Logger.Builder()
                                .printers(new AndroidPrinter(), loggerPrinter)
                                .build();
                    }
                }));
    }

    public static List<String> getHistory() {
        return new ArrayList<>(lastLogs);
    }

    private static void addHistoryLog(String log) {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd    HH:mm:ss");
        String date = sDateFormat.format(new Date());

        String reslut = date + "\n" + log;

        lastLogs.add(reslut);
        synchronized (SDKLogUtil.class) {
            if (lastLogs.size() > MAX_LOG_SIZE) {
                lastLogs.remove(0);
            }
        }
    }

    private static void addHistoryLog(String str, Object... args) {
        String log = String.format(str, args);
        addHistoryLog(log);
    }

    public static void v(String log) {
        if (logger != null) {
            logger.v(log);

        }
        addHistoryLog(log);
    }

    public static void v(String str, Object... args) {
        if (logger != null) {
            logger.v(str, args);
        }
        addHistoryLog(str, args);
    }

    public static void d(String log) {
        if (logger != null) {
            logger.d(log);
        }
        addHistoryLog(log);
    }

    public static void d(String str, Object... args) {
        if (logger != null) {
            logger.d(str, args);
        }
        addHistoryLog(str, args);
    }

    public static void i(String log) {
        if (logger != null) {
            logger.i(log);
        }
        addHistoryLog(log);
    }

    public static void i(String str, Object... args) {
        if (logger != null) {
            logger.i(str, args);
        }
        addHistoryLog(str, args);
    }

    public static void w(String log) {
        if (logger != null) {
            logger.w(log);
        }
        addHistoryLog(log);
    }

    public static void w(String str, Object... args) {
        if (logger != null) {
            logger.w(str, args);
        }
        addHistoryLog(str, args);
    }

    public static void e(String log) {
        if (logger != null) {
            logger.e(log);
        }
        addHistoryLog(log);
    }

    public static void e(String str, Object... args) {
        if (logger != null) {
            logger.e(str, args);
        }
        addHistoryLog(str, args);
    }

    @Deprecated
    public static void setLogPath(String path, final String fileName) {
        Printer loggerPrinter = new FilePrinter.Builder(path)
                .fileNameGenerator(new FileNameGenerator() {
                    @Override
                    public boolean isFileNameChangeable() {
                        return true;
                    }

                    @Override
                    public String generateFileName(int logLevel, long timestamp) {
                        return fileName;
                    }
                })
                .build();
        logger = new Logger.Builder()
                .printers(new AndroidPrinter(), loggerPrinter)
                .build();
    }

//    public static class LocalLogPrinter implements Printer {
//
//        @Override
//        public void println(int logLevel, String tag, String msg) {
//            new LogEvent(logLevel, tag, msg).post();
//        }
//    }
}
