package com.madx.bwm.ui;


import android.os.AsyncTask;
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
import com.madx.bwm.util.FileUtil;
import com.madx.bwm.widget.HorizontalListView;

import java.io.File;
import java.util.ArrayList;
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
    private HorizontalListView horizontalListView;
    private MessageStickerFragment fragment = null;
    private MessageHorizontalListViewAdapter horizontalListViewAdapter;
    static List<String> STICKER_NAME_LIST = new ArrayList<>();
    static List<String> FIRST_STICKER_LIST = new ArrayList<>();

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
        if (STICKER_NAME_LIST.size() == 0) {
            addStickerList();
        }
        if (FIRST_STICKER_LIST.size() == 0) {
            addImageList();
        }
        horizontalListViewAdapter = new MessageHorizontalListViewAdapter(FIRST_STICKER_LIST, getActivity());
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

    private void addStickerList() {
        try {
            List<String> pathList = FileUtil.getAllFilePathsFromAssets(getActivity(), MessageChatActivity.STICKERS_NAME);
            if (null != pathList) {
                for (String string : pathList) {
                    STICKER_NAME_LIST.add(MessageChatActivity.STICKERS_NAME + File.separator + string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageList() {
        if (STICKER_NAME_LIST != null && STICKER_NAME_LIST.size() > 0) {
            for (String string : STICKER_NAME_LIST) {
                List<String> stickerAllNameList = FileUtil.getAllFilePathsFromAssets(getActivity(), string);
                if (null != stickerAllNameList) {
                    String iconPath = string + File.separator + stickerAllNameList.get(0);
                    FIRST_STICKER_LIST.add(iconPath);
                }
            }
        }
    }

    private void setTabSelection(int index) {
        if (getActivity().isFinishing()) {
            return;
        }
        String selectStickerName = STICKER_NAME_LIST.get(index);
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
