package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.mail.MailSender;
import com.bondwithme.BondWithMe.mail.MailSenderModel;
import com.bondwithme.BondWithMe.util.AppInfoUtil;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;

import java.io.File;

/**
 * Created by wing on 15/8/4.
 */
public class CrashActivity extends Activity {

    private static MyDialog errorReportDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showFeedbackDialog();
    }

    private void showFeedbackDialog() {

        if (errorReportDialog == null) {
            errorReportDialog = new MyDialog(this, R.string.error_feedback, R.string.error_feedback_desc);
            errorReportDialog.setCanceledOnTouchOutside(false);
            errorReportDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            });
            errorReportDialog.setButtonAccept(R.string.report, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorReportDialog.dismiss();
                    //send report
                    new SendEmailTask().execute();


//                    Intent i = new Intent(Intent.ACTION_SEND);
//                    i.setType("message/rfc822");
//                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@wingzhong.com"});
//                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                    i.putExtra(Intent.EXTRA_TEXT, "body of email");
//                    try {
//                        CrashActivity.this.startActivity(Intent.createChooser(i, "Send mail..."));
//                    } catch (android.content.ActivityNotFoundException ex) {
//                        Toast.makeText(CrashActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                    }
//
                }
            });
        }
        if (!errorReportDialog.isShowing())
            errorReportDialog.show();
    }


    class SendEmailTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
//			pd.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
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
            mailInfo.setAttachFileNames(getCrashFilePaths());
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
            //sms.sendHtmlMail(mailInfo);
            return true;
        }

        private String[] getCrashFilePaths(){
            File[] files = FileUtil.getCrashFiles(CrashActivity.this);
            if(files!=null&&files.length>0){
                String[] filePaths = new String[files.length];
                int length = files.length;
                for (int i=0;i<length;i++){
                    filePaths[i] = files[i].toString();
                }
                return filePaths;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
//			pd.dismiss();
            MessageUtil.showMessage(CrashActivity.this,R.string.say_thanks_for_report);
            if(result){
                FileUtil.clearCrashFiles(CrashActivity.this);
            }
//                MessageUtil.showMessage(CrashActivity.this,R.string.send_email_successed);
//            }else{
//                MessageUtil.showMessage(CrashActivity.this, R.string.send_email_failed);
//            }


            CrashActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

    }

    @Override
    protected void onDestroy() {
        if (errorReportDialog != null)
            errorReportDialog.dismiss();
        super.onDestroy();
    }
}
