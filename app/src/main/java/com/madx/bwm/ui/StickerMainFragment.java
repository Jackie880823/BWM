package com.madx.bwm.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.madx.bwm.R;
import com.madx.bwm.adapter.MessageHorizontalListViewAdapter;
import com.madx.bwm.interfaces.StickerViewClickListener;
import com.madx.bwm.widget.HorizontalListView;

import java.util.List;

/**
 * Created by quankun on 15/5/12.
 */
public class StickerMainFragment extends Fragment {
    private StickerViewClickListener stickerViewClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View rootView;
    private List<String> stickerNameList;
    private HorizontalListView horizontalListView;
    private List<String> sticker_List_Id;
    private MessageStickerFragment fragment = null;
    private MessageHorizontalListViewAdapter horizontalListViewAdapter;

    public void setPicClickListener(StickerViewClickListener viewClickListener) {
        stickerViewClickListener = viewClickListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == inflater) {
            return null;
        }
        rootView = inflater.inflate(R.layout.fragment_sticker_main, null);
        horizontalListView = (HorizontalListView) rootView.findViewById(R.id.sticker_listView);
        stickerNameList = MainActivity.STICKER_NAME_LIST;
        sticker_List_Id = MainActivity.FIRST_STICKER_LIST;
        horizontalListViewAdapter = new MessageHorizontalListViewAdapter(sticker_List_Id, getActivity());
        horizontalListView.setAdapter(horizontalListViewAdapter);
        setTabSelection(0);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setTabSelection(position);
                horizontalListViewAdapter.setChoosePosition(position);
                horizontalListViewAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    private void setTabSelection(int index) {
        if (getActivity().isFinishing()) {
            return;
        }
        String selectStickerName = stickerNameList.get(index);
        // 开启一个Fragment事务
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragment = new MessageStickerFragment();//selectStickerName, MessageChatActivity.this, groupId);
        Bundle bundle = new Bundle();
        bundle.putString("selectStickerName", selectStickerName);
        fragment.setArguments(bundle);
        fragment.setPicClickListener(stickerViewClickListener);
        transaction.replace(R.id.message_frame, fragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
