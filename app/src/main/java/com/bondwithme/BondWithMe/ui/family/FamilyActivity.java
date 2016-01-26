package com.bondwithme.BondWithMe.ui.family;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.ui.BaseActivity;
import com.bondwithme.BondWithMe.ui.FamilyFragment;
import com.bondwithme.BondWithMe.ui.InviteMemberActivity;
import com.bondwithme.BondWithMe.ui.add.AddMembersActivity;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.material.widget.Dialog;

/**
 * Created 10/20/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FamilyActivity extends BaseActivity {
    private static final String TAG = FamilyActivity.class.getSimpleName();
    private String content_group_id;
    private Dialog showSelectDialog;

    @Override
    protected void initBottomBar() {
    }

    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_rewards);
    }

    @Override
    protected void titleRightEvent() {
        if (showSelectDialog != null && showSelectDialog.isShowing()) {
            //return false;
        } else {
//                    popupWindow.dismissPopupWindow();

            showSelectDialog();
            // return false;
        }
    }

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(FamilyActivity.this);
        View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(FamilyActivity.this, null, selectIntention);
        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开添加成员页面
                startActivity(new Intent(FamilyActivity.this, AddMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                //打开创建群组页面
                Intent intent = new Intent(FamilyActivity.this, InviteMemberActivity.class);
                intent.putExtra("isCreateNewGroup", true);
                intent.putExtra("jumpIndex", 0);
//                startActivity(intent);
                startActivityForResult(intent, 1);
                showSelectDialog.dismiss();
            }
        });
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        showSelectDialog.show();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected Fragment getFragment() {
        Intent intent = getIntent();
        return FamilyFragment.newInstance();
    }

    @Override
    public void initView() {

    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean banBack;

    protected CommandListener commandlistener;

    public void setCommandlistener(CommandListener commandlistener) {
        this.commandlistener = commandlistener;
    }

    public interface CommandListener {
        public boolean execute(View v);
    }

}
