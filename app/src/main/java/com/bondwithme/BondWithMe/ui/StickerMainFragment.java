package com.bondwithme.BondWithMe.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.bondwithme.BondWithMe.dao.LocalStickerInfoDao;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.MessageHorizontalListViewAdapter;
import com.bondwithme.BondWithMe.interfaces.StickerViewClickListener;
import com.bondwithme.BondWithMe.ui.more.sticker.StickerStoreActivity;
import com.bondwithme.BondWithMe.util.FileUtil;
import com.bondwithme.BondWithMe.widget.HorizontalListView;

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
    private ProgressBarCircularIndeterminate progress;
    private MessageHorizontalListViewAdapter horizontalListViewAdapter;
    private LinearLayout layout;
    List<String> STICKER_NAME_LIST = new ArrayList<>();
    List<String> FIRST_STICKER_LIST = new ArrayList<>();

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
        progress = (ProgressBarCircularIndeterminate) rootView.findViewById(R.id.progress_bar);
        layout = (LinearLayout) rootView.findViewById(R.id.sticker_setting_linear);

        STICKER_NAME_LIST = LocalStickerInfoDao.getInstance(getActivity()).queryAllSticker();
        for (String entry : STICKER_NAME_LIST) {
            String path = MainActivity.STICKERS_NAME + File.separator + entry;
            File file = new File(path);
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                for (File file1 : files) {
                    String filePath = file1.getAbsolutePath();
                    if (filePath.substring(filePath.lastIndexOf(File.separator) + 1).contains("B")) {
                        FIRST_STICKER_LIST.add(filePath);
                        break;
                    }
                }

            }
        }
//        if (STICKER_NAME_LIST.size() == 0) {
//            progress.setVisibility(View.VISIBLE);
//            horizontalListViewAdapter = new MessageHorizontalListViewAdapter(new ArrayList<String>(), getActivity());
//            new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected Void doInBackground(Void... params) {
//                    addStickerList();
//                    addImageList();
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    progress.setVisibility(View.GONE);
//                    horizontalListViewAdapter = new MessageHorizontalListViewAdapter(FIRST_STICKER_LIST, getActivity());
//                    horizontalListView.setAdapter(horizontalListViewAdapter);
//                    setTabSelection(0);
//                }
//
//            }.execute();
//        } else {
        progress.setVisibility(View.GONE);
        horizontalListViewAdapter = new MessageHorizontalListViewAdapter(FIRST_STICKER_LIST, getActivity());
//        }

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
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), StickerStoreActivity.class));
            }
        });
        return rootView;
    }

    private void addStickerList() {
        try {
            List<String> pathList = FileUtil.getAllFilePathsFromAssets(getActivity(), MainActivity.STICKERS_NAME);
            if (null != pathList) {
                for (String string : pathList) {
                    STICKER_NAME_LIST.add(MainActivity.STICKERS_NAME + File.separator + string);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addImageList() {
        if (STICKER_NAME_LIST != null && STICKER_NAME_LIST.size() > 0) {
            for (String string : STICKER_NAME_LIST) {
                if (null == string) {
                    continue;
                }
                List<String> stickerAllNameList = FileUtil.getAllFilePathsFromAssets(getActivity(), string);
                if (null != stickerAllNameList && stickerAllNameList.size() > 0) {
                    String iconPath = string + File.separator + stickerAllNameList.get(0);
                    FIRST_STICKER_LIST.add(iconPath);
                }
            }
        }
    }

    private void setTabSelection(int index) {
        if (getActivity() == null || getActivity().isFinishing() || horizontalListViewAdapter.getCount() == 0) {
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
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }
}
