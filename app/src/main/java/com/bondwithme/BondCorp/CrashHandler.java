package com.madxstudio.co8;


import android.content.Context;
import android.os.Looper;

import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.util.PreferencesUtil;
import com.madxstudio.co8.util.SystemUtil;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * Created by wing on 15/6/3.
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";


    //系统默认的UncaughtException处理类
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();
    //程序的Context对象
//    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
//        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
//            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            Intent intent = new Intent(App.getContextInstance(), MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            App.getContextInstance().startActivity(intent);
//            App.getContextInstance().exit();

            //保存发生异常标识，以备下次启动提示反馈
            PreferencesUtil.saveValue(App.getContextInstance(), Constant.APP_CRASH, true);
            //退出程序
            App.getContextInstance().exit();


        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                MessageUtil.showMessage(App.getContextInstance(), R.string.app_crash_message);
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        infos = SystemUtil.collectDeviceInfo(App.getContextInstance());
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }


    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private void saveCrashInfo2File(Throwable ex) {
        //收集设备参数信息
        infos = SystemUtil.collectDeviceInfo(App.getContextInstance());
        //添加用户相关信息
        infos.put("bondwithme_id", MainActivity.getUser().getBondwithme_id());
        infos.put("user_name", MainActivity.getUser().getUser_given_name());

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String filePath = FileUtil.getSaveCrashPath(App.getContextInstance());
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            LogUtil.e(TAG, "an error occured while writing file...");
        }
    }


}
