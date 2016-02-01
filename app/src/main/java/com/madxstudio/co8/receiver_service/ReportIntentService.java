package com.madxstudio.co8.receiver_service;

import android.app.IntentService;
import android.content.Intent;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.mail.MailSender;
import com.madxstudio.co8.mail.MailSenderModel;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.util.FileUtil;

/**
 * 异常报告服务
 * Created by wing on 15/9/7.
 */

public class ReportIntentService extends IntentService {

    //没有这个空的构造函数,启动会报错
    public ReportIntentService()
    {
        super("reportService");
    }
    @Override
    public void onCreate() {
        // TODO 自动生成的方法存根
        super.onCreate();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        doSendMail();
    }

    @Override
    public void onDestroy() {
        //所有任务执行完后,自动关闭
        super.onDestroy();
        FileUtil.clearCrashFiles(this);
    }

    private void doSendMail() {
        MailSenderModel mailInfo = new MailSenderModel();
        mailInfo.setMailServerHost(Constant.REPORT_EMAIL_SERVER_HOST);
        mailInfo.setMailServerPort(Constant.REPORT_EMAIL_SERVER_PORT);
        mailInfo.setValidate(true);
        mailInfo.setUserName(Constant.REPORT_EMAIL_USERNAME);
        mailInfo.setPassword(Constant.REPORT_EMAIL_PASSWORD);
        mailInfo.setFromAddress(Constant.REPORT_EMAIL_FROM_ADDRESS);
        mailInfo.setToAddress(Constant.REPORT_EMAIL_TO_ADDRESS);
        mailInfo.setSubject("BWM 异常反馈!");
        mailInfo.setContent("反馈客户设备信息:\r\n"
                        + "VersionCode:" + AppInfoUtil.getAppVersionName(this) + "\r\n"
                        + "VersionCode:" + AppInfoUtil.getDeviceUUID(this)
        );
        mailInfo.setAttachFiles(FileUtil.getCrashFiles(this));
//
//		     StringBuffer content = new StringBuffer();
//		     content.append(params[1]);
//		     content.append("<br>");
//		     content.append(getString(R.string.send_email_binded_info)+"<br>");
//		     DeviceSQLiteDatabase deviceDB = DeviceSQLiteDatabase.getInstance(SendEmailActivity.this);
//		     List<Module> moduleList = deviceDB.getData(MyApplication.getInstance().getProType());
//			if(moduleList!=null){
//				content.append("<table>");
//				content.append("<tr bgcolor=\"#C0C0C0\"><td>DeviceName</td><td>WiFi Name</td><td>ApiKey</td><td>FeedId</td></tr> ");
//				for(Module m : moduleList){
//					content.append("<tr><td>"+m.getDeviceName()+"</td><td>"+m.getModuleID()+"</td><td>"+m.getApiKey()+"</td><td>"+m.getFeedId()+"</td></tr> ");
//					//content.append(m.getDeviceName()).append("#").append(m.getModuleID()).append("#").append(m.getApiKey()).append("#").append(m.getFeedId());
//					//content.append("<br>");
//				}
//				content.append("</table>");
//			}
//			content.append(getString(R.string.send_email_user_info)+"<br>").append(params[0]);
//		     mailInfo.setContent(content.toString());

        MailSender sms = new MailSender();
        sms.sendTextMail(mailInfo);
//            sms.sendHtmlMail(mailInfo);
    }
}
