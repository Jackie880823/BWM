package com.bondwithme.BondWithMe.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.widget.MyDialog;

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

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@wingzhong.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                    i.putExtra(Intent.EXTRA_TEXT, "body of email");
                    try {
                        CrashActivity.this.startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(CrashActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
//
                }
            });
        }
        if (!errorReportDialog.isShowing())
            errorReportDialog.show();
    }

    @Override
    protected void onDestroy() {
        if (errorReportDialog != null)
            errorReportDialog.dismiss();
        super.onDestroy();
    }
}
