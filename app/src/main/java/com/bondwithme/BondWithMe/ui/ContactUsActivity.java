package com.bondwithme.BondWithMe.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.MyTextUtil;
import com.bondwithme.BondWithMe.widget.MyDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * 普通Activity,包含了头部和底部，只需定义中间Fragment内容(通过重写getFragment() {)
 */
public class ContactUsActivity extends BaseActivity {


    private TextView mail_subject;
    private EditText mail_from;
    private EditText mail_content;
    private MyDialog showSelectDialog;

    private String subject_text;
    String mail_from_text;
    String mail_content_text;

    public int getLayout() {
        return R.layout.activity_contact_us;
    }

    @Override
    protected void initBottomBar() {

    }

    @Override
    protected void setTitle() {

        tvTitle.setText(R.string.text_contact_us);
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        titleBar.setBackgroundColor(getResources().getColor(R.color.tab_color_press4));
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {

        mail_subject = getViewById(R.id.mail_subject);
        mail_from = getViewById(R.id.mail_from);
        mail_content = getViewById(R.id.mail_content);

        getViewById(R.id.subject_spinner).setOnClickListener(this);
        getViewById(R.id.btn_send_mail).setOnClickListener(this);


    }

    private void showSelectDialog() {

        if(showSelectDialog==null) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View selectIntention = factory.inflate(R.layout.dialog_mail_select_subject, null);
            showSelectDialog = new MyDialog(this, R.string.hint_subject, selectIntention);
            showSelectDialog.setCanceledOnTouchOutside(false);
            showSelectDialog.setButtonCancel(R.string.cancel,new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSelectDialog.dismiss();
                }
            });
            selectIntention.findViewById(R.id.subject_1).setOnClickListener(this);
            selectIntention.findViewById(R.id.subject_2).setOnClickListener(this);
            selectIntention.findViewById(R.id.subject_3).setOnClickListener(this);
        }
        if(!showSelectDialog.isShowing())
            showSelectDialog.show();
    }

    @Override
    protected void onDestroy() {
        if(showSelectDialog!=null){
            showSelectDialog.dismiss();
            showSelectDialog=null;
        }
        super.onDestroy();
    }


    private boolean validateForm() {
        if (TextUtils.isEmpty(subject_text)) {
            return false;
        }
        mail_from_text = mail_from.getText().toString();
        if (TextUtils.isEmpty(mail_from_text) && MyTextUtil.isInvalidText(mail_from_text)) {
            return false;
        }

        mail_content_text = mail_content.getText().toString();
        if (TextUtils.isEmpty(mail_content_text) && MyTextUtil.isInvalidText(mail_content_text)) {
            return false;
        }

        return true;
    }

    ProgressDialog mProgressDialog;
    private void sendMail(){
        if(validateForm()) {

            mProgressDialog = new ProgressDialog(this,getString(R.string.text_sending));
            mProgressDialog.show();

            Map<String, String> params = new HashMap<>();

            params.put("user_id", MainActivity.getUser().getUser_id());
            params.put("user_login_id", MainActivity.getUser().getUser_login_id());
            params.put("subject", subject_text);
            params.put("body", mail_content_text);
            params.put("email", mail_from_text);
            new HttpTools(this).post(Constant.API_CONTACT_US, params,this, new HttpCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                    mProgressDialog.dismiss();
                }

                @Override
                public void onResult(String string) {
                    MessageUtil.showMessage(ContactUsActivity.this,R.string.msg_action_successed);
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }else{
            MessageUtil.showMessage(this, R.string.alert_text_mail_info_not_complete);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subject_1:
                subject_text = getString(R.string.mail_subject_1);
                mail_subject.setText(subject_text);
                showSelectDialog.dismiss();
                break;
            case R.id.subject_2:
                subject_text = getString(R.string.mail_subject_2);
                mail_subject.setText(subject_text);
                showSelectDialog.dismiss();
                break;
            case R.id.subject_3:
                subject_text = getString(R.string.mail_subject_3);
                mail_subject.setText(subject_text);
                showSelectDialog.dismiss();
                break;
            case R.id.subject_spinner:
                showSelectDialog();
                break;
            case R.id.btn_send_mail:
                sendMail();
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
