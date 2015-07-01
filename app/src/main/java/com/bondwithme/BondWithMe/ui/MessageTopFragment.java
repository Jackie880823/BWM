package com.bondwithme.BondWithMe.ui;

import android.content.Intent;
import android.widget.GridView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MessageMemberAdapter;
import com.bondwithme.BondWithMe.entity.UserEntity;

import java.util.ArrayList;

/**
 * Created by christepherzhang on 15/1/22.
 */
public class MessageTopFragment extends BaseFragment implements MessageMemberAdapter.MemberClickListener {

    private ArrayList<UserEntity> userEntityList;
    private UserEntity userEntity = new UserEntity();

    public MessageTopFragment()
    {
        super();
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_top_message;
    }

    @Override
    public void initView() {

        this.userEntityList = (ArrayList<UserEntity>) getArguments().getSerializable("data");

        if (this.userEntityList == null)
        {
            this.userEntityList = new ArrayList<>();
        }

        GridView mGridView = (GridView)getViewById(R.id.people_gridView);
        MessageMemberAdapter messageMemberAdapter = new MessageMemberAdapter(getParentActivity(),R.layout.message_gridview_item, userEntityList);
        mGridView.setAdapter(messageMemberAdapter);
        messageMemberAdapter.notifyDataSetChanged();
        messageMemberAdapter.setMemberClickListener(this);


        mGridView.setEnabled(false);
        mGridView.setFocusable(false);
        mGridView.setClickable(false);

//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(), ChatActivity.class);
//                intent.putExtra("type", 0);
//                intent.putExtra("userEntity", userEntityList.get(position));
//                view.findViewById(R.id.tv_num).setVisibility(View.GONE);
//                startActivity(intent);
//                return;
//            }
//        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void memberClick(UserEntity entity) {
        Intent intent = new Intent(getActivity(), MessageChatActivity.class);
        intent.putExtra("type", 0);
        //intent.putExtra("userEntity", entity);
        intent.putExtra("groupId", entity.getGroup_id());
        intent.putExtra("titleName", entity.getUser_given_name());
//        view.findViewById(R.id.tv_num).setVisibility(View.GONE);
        startActivity(intent);
    }
}