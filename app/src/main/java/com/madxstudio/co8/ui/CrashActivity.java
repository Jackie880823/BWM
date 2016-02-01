package com.madxstudio.co8.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.madxstudio.co8.App;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.mail.MailSender;
import com.madxstudio.co8.mail.MailSenderModel;
import com.madxstudio.co8.receiver_service.ReportIntentService;
import com.madxstudio.co8.util.AppInfoUtil;
import com.madxstudio.co8.util.FileUtil;
import com.madxstudio.co8.util.MessageUtil;
import com.madxstudio.co8.widget.MyDialog;

/**
 * Created by wing on 15/8/4.
 */
public class CrashActivity extends Activity {

    private static MyDialog errorReportDialog;
    private static final int SEND_FINISH = 1;
    private static final int SUCCESED = 2;
    private static final int FAILED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SEND_FINISH:
                        MessageUtil.showMessage(CrashActivity.this, R.string.say_thanks_for_report);
                        if (msg.arg1 == SUCCESED) {
                            FileUtil.clearCrashFiles(CrashActivity.this);
                        }
                        if (errorReportDialog != null)
                            errorReportDialog.dismiss();
                        CrashActivity.this.finish();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                        break;
                }
                return false;
            }
        });
        showFeedbackDialog();

    }

    private void showFeedbackDialog() {

        if (errorReportDialog == null) {
            errorReportDialog = new MyDialog(this, R.string.error_feedback, R.string.error_feedback_desc);
            errorReportDialog.setCanceledOnTouchOutside(false);
            errorReportDialog.setButtonCancel(R.string.text_dialog_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    CrashActivity.this.finish();
//                    System.exit(0);
                }
            });
            errorReportDialog.setButtonAccept(R.string.report, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    Intent intentService = new Intent(App.getContextInstance(), ReportIntentService.class);
                    startService(intentService);
                    //show uploading...
                    CrashActivity.this.finish();
                }
            });
        }
        if (!errorReportDialog.isShowing())
            errorReportDialog.show();
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
                        + "VersionCode:" + AppInfoUtil.getAppVersionName(CrashActivity.this) + "\r\n"
                        + "VersionCode:" + AppInfoUtil.getDeviceUUID(CrashActivity.this)
        );
        mailInfo.setAttachFiles(FileUtil.getCrashFiles(CrashActivity.this));
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

    Handler mHandler;

    @Override
    protected void onDestroy() {
        if (errorReportDialog != null)
            errorReportDialog.dismiss();
        super.onDestroy();
    }

}
