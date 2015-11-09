package com.bondwithme.BondWithMe.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.FamilyGroupAdapter;
import com.bondwithme.BondWithMe.adapter.MyFamilyAdapter;
import com.bondwithme.BondWithMe.entity.FamilyGroupEntity;
import com.bondwithme.BondWithMe.entity.FamilyMemberEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.UrlUtil;
import com.bondwithme.BondWithMe.ui.family.FamilyTreeActivity;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MessageUtil;
import com.bondwithme.BondWithMe.util.NetworkUtil;
import com.bondwithme.BondWithMe.util.PinYin4JUtil;
import com.bondwithme.BondWithMe.util.PreferencesUtil;
import com.bondwithme.BondWithMe.widget.InteractivePopupWindow;
import com.bondwithme.BondWithMe.widget.MyDialog;
import com.bondwithme.BondWithMe.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.material.widget.Dialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by quankun on 15/5/12.
 */
public class FamilyFragment extends BaseFragment<MainActivity> implements View.OnClickListener {
    private static final String Tag = FamilyFragment.class.getSimpleName();
    private EditText etSearch;
    private ViewPager pager;
    private TextView message_member_tv;
    private TextView message_group_tv;
//    private Dialog showSelectDialog;
    private Context mContext;
    private boolean isMemberRefresh, isGroupRefresh;
    /**
     * 只有成员，不包括亲人
     */
    private List<FamilyMemberEntity> memberEntityList;//只有成员，不包括亲人
//    private List<FamilyMemberEntity> empmemberEntityList = new LinkedList<>();
    /**
     * 没有展开
     */
    private List<FamilyMemberEntity> memberList;//没有展开
    /**
     * 不包括family_tree
     */
    private List<FamilyMemberEntity> moreMemberList;//不包括family_tree
    private List<FamilyGroupEntity> groupEntityList;
    private MySwipeRefreshLayout groupRefreshLayout, memberRefreshLayout;
    //    private ProgressDialog mProgressDialog;
    private static final int GET_DATA = 0x11;
    private static final int GET_OP = 0x19;
    private static final int GET_DELAY_RIGHT = 0x28;
    private static final int GET_DELAY_ADD_PHOTO = 0x30;
    private MyFamilyAdapter memberAdapter;
    private FamilyGroupAdapter groupAdapter;
    public static String FAMILY_TREE = "family_treely_tree\";\n" +
            "    public static final String FAMILY_PARENT ";
    public static final String FAMILY_PARENT = "parent";
    public static final String FAMILY_CHILDREN = "children";
    public static final String FAMILY_SIBLING = "sibling";
    public static final String FAMILY_SPOUSE = "spouse";
    public static String FAMILY_MORE_MEMBER = "Everyone";
    public static String FAMILY_HIDE_MEMBER = "MyFamily";

    private RelativeLayout emptyGroupLinear;
    private RelativeLayout emptyMemberLinear;
    private ImageView emptyGroupIv;
    private ImageView emptyMemberIv;
    private View emptyGroupTv;
    private View emptyMemberTv;
    private View vProgress;
    private GridView groupListView;
    private Dialog showSelectDialog;
    private String MemeberSearch;
    private String GroupSearch;
    private boolean isup;
    private boolean isopen;
    private boolean firstOpPop;
    public InteractivePopupWindow popupWindow,popupWindowAddPhoto;
    private String popTestSt;

    List<FamilyMemberEntity> opendate = new LinkedList<>();

    public static FamilyFragment newInstance(String... params) {
        return createInstance(new FamilyFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_my_family;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_DATA:
                    Map<String, List> map = (Map<String, List>) msg.obj;
                    if (memberEntityList != null) {
                        memberEntityList.clear();
                    }
                    if (memberList != null) {
                        memberList.clear();
                    }
                    if (moreMemberList != null) {
                        moreMemberList.clear();
                    }
                    //所有的成员包括亲人、好友、等待回复
                    memberEntityList = map.get("private");
//                    Log.i("member_size===", memberEntityList.size() + "");
                    if (memberEntityList != null && memberEntityList.size() > 0) {
                        FamilyMemberEntity member = new FamilyMemberEntity();//family_tree
                        member.setUser_given_name(FAMILY_TREE);
                        member.setUser_id(FAMILY_TREE);
                        memberList.add(member);
                        moreMemberList.add(member);
//                        opendate.add(0,member);
                        for (FamilyMemberEntity memberEntity : memberEntityList) {
                            //成员的关系
                            String tree_type = memberEntity.getTree_type();
//                            Log.i("tree_type===", memberEntity.getTree_type());
                            if (FAMILY_PARENT.equals(tree_type) || FAMILY_CHILDREN.equals(tree_type)
                                    || FAMILY_SIBLING.equals(tree_type) || FAMILY_SPOUSE.equals(tree_type)) {
                                memberList.add(memberEntity);
                            }
                            moreMemberList.add(memberEntity);
                        }
                        FamilyMemberEntity familyMemberEntity = new FamilyMemberEntity();
                        familyMemberEntity.setUser_given_name(FAMILY_MORE_MEMBER);
                        familyMemberEntity.setUser_id(FAMILY_MORE_MEMBER);
                        memberList.add(familyMemberEntity);
                        FamilyMemberEntity familyMember = new FamilyMemberEntity();
                        familyMember.setUser_given_name(FAMILY_HIDE_MEMBER);
                        familyMember.setUser_id(FAMILY_HIDE_MEMBER);
                        moreMemberList.add(familyMember);
//                        opendate.add(familyMember);
                    }
                    memberAdapter.addNewData(memberList);

                    groupEntityList = map.get("group");
                    groupAdapter.addData(groupEntityList);
                    break;
                case GET_OP:
                    Map<String, List> opmap = (Map<String, List>) msg.obj;
                    if (memberEntityList != null) {
                        memberEntityList.clear();
                    }
                    if (memberList != null) {
                        memberList.clear();
                    }
                    if (moreMemberList != null) {
                        moreMemberList.clear();
                    }

                    memberEntityList = opmap.get("private");
                    if (memberEntityList != null && memberEntityList.size() > 0) {
                        FamilyMemberEntity member = new FamilyMemberEntity();
                        member.setUser_given_name(FAMILY_TREE);
                        member.setUser_id(FAMILY_TREE);
                        memberList.add(member);
                        moreMemberList.add(member);
//                        opendate.add(0,member);
                        for (FamilyMemberEntity memberEntity : memberEntityList) {
                            String tree_type = memberEntity.getTree_type();
                            if (FAMILY_PARENT.equals(tree_type) || FAMILY_CHILDREN.equals(tree_type)
                                    || FAMILY_SIBLING.equals(tree_type) || FAMILY_SPOUSE.equals(tree_type)) {
                                memberList.add(memberEntity);
                            }
                            moreMemberList.add(memberEntity);
                        }
                        FamilyMemberEntity familyMemberEntity = new FamilyMemberEntity();
                        familyMemberEntity.setUser_given_name(FAMILY_MORE_MEMBER);
                        familyMemberEntity.setUser_id(FAMILY_MORE_MEMBER);
                        memberList.add(familyMemberEntity);
                        FamilyMemberEntity familyMember = new FamilyMemberEntity();
                        familyMember.setUser_given_name(FAMILY_HIDE_MEMBER);
                        familyMember.setUser_id(FAMILY_HIDE_MEMBER);
                        moreMemberList.add(familyMember);
//                        opendate.add(familyMember);
                    }
                    memberAdapter.addNewData(moreMemberList);

                    groupEntityList = opmap.get("group");
                    groupAdapter.addData(groupEntityList);
                    break;
                case GET_DELAY_ADD_PHOTO:
                    if(MainActivity.interactivePopupWindowMap.containsKey(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO)){
                        popupWindowAddPhoto = MainActivity.interactivePopupWindowMap.get(InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO);
                        popupWindowAddPhoto.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                            @Override
                            public void popDismiss() {
                                PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO,true);
                            }
                        });
                        popupWindowAddPhoto.showPopupWindowUp();
                    }
                    break;
                case GET_DELAY_RIGHT:
                     popupWindow = new InteractivePopupWindow(getParentActivity(),getParentActivity().rightButton,popTestSt,0);
                     popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                         @Override
                         public void popDismiss() {
                             LogUtil.i("==============1","onDismiss");
                             PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_MEMBER, true);
                             newPopAddPhoto();
                         }
                     });
                    popupWindow.showPopupWindow(true);
                    break;

            }

        }
    };

    @Override
    public void initView() {
        isup = true;
        mContext = getActivity();
        FAMILY_MORE_MEMBER = mContext.getString(R.string.text_new_everyone);
        FAMILY_HIDE_MEMBER = mContext.getString(R.string.text_new_family);
        FAMILY_TREE = mContext.getString(R.string.text_new_family_tree);
        pager = getViewById(R.id.family_list_viewpager);
        message_member_tv = getViewById(R.id.message_member_tv);
        message_group_tv = getViewById(R.id.message_group_tv);
        etSearch = getViewById(R.id.et_search);
        memberEntityList = new ArrayList<>();
        groupEntityList = new ArrayList<>();
        memberList = new ArrayList<>();
        moreMemberList = new ArrayList<>();
//        mProgressDialog = new ProgressDialog(getActivity(), getString(R.string.text_loading));
//        mProgressDialog.show();
        vProgress = getViewById(R.id.rl_progress);
        vProgress.setVisibility(View.VISIBLE);

        memberAdapter = new MyFamilyAdapter(mContext, memberEntityList);
        groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
        //绑定自定义适配器
        pager.setAdapter(new FamilyPagerAdapter(initPagerView()));
        pager.setOnPageChangeListener(new MyOnPageChanger());
        getParentActivity().setFamilyCommandListener(new BaseFragmentActivity.CommandListener() {
            @Override
            public boolean execute(View v) {

                if (showSelectDialog != null && showSelectDialog.isShowing()) {
                    return false;
                } else {
//                    popupWindow.dismissPopupWindow();

                    showSelectDialog();
                    return false;
                }

            }
        });
        message_member_tv.setOnClickListener(this);
        message_group_tv.setOnClickListener(this);
        //搜索框监听器
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String etImport = etSearch.getText().toString();
                if (pager.getCurrentItem() == 0) {
                    MemeberSearch = etImport;
                } else {
                    GroupSearch = etImport;
                }
                setSearchData(etImport);

            }
        });

        popTestSt  = mContext.getResources().getString(R.string.text_tip_add_member);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void newPopAddPhoto(){
        InteractivePopupWindow popupWindowAddPhoto = new InteractivePopupWindow(getParentActivity(),getParentActivity().bottom,getParentActivity().getResources().getString(R.string.text_tip_add_photo),1) ;
        popupWindowAddPhoto.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
            @Override
            public void popDismiss() {
                PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO,true);

            }
        });
        popupWindowAddPhoto.showPopupWindowUp();

    }
    //搜索
    private void setSearchData(String searchData) {
        String etImport = PinYin4JUtil.getPinyinWithMark(searchData);
        if (pager.getCurrentItem() == 0) {
            List<FamilyMemberEntity> familyMemberEntityList;
            //如果已经展开而且搜索框不为空
            if (isopen && !TextUtils.isEmpty(etImport)) {
//                familyMemberEntityList = searchMemberList(etImport, moreMemberList);
//                memberAdapter.addNewData(familyMemberEntityList);
                memberAdapter.setSerach(moreMemberList);
                Filter filter = memberAdapter.getFilter();
                filter.filter(etImport);
            } else {
                if (isopen) {
                    familyMemberEntityList = searchMemberList(etImport, moreMemberList);
                    memberAdapter.addNewData(familyMemberEntityList);
                } else {
                    if (TextUtils.isEmpty(etImport)) {
                        familyMemberEntityList = searchMemberList(etImport, memberList);
                        memberAdapter.addNewData(familyMemberEntityList);
                    } else {
//                    familyMemberEntityList = searchMemberList(etImport, memberList);
                        memberAdapter.setSerach(moreMemberList);
                        Filter filter = memberAdapter.getFilter();
                        filter.filter(etImport);
                    }
                }

            }

        } else {
            List<FamilyGroupEntity> familyGroupEntityList;
            if (TextUtils.isEmpty(etImport)) {
                familyGroupEntityList = groupEntityList;
                groupAdapter.addData(familyGroupEntityList);
            } else {
//                familyGroupEntityList = searchGroupList(etImport, groupEntityList);
                Filter filter = groupAdapter.getFilter();
                filter.filter(etImport);
            }

        }

    }

    private List<FamilyMemberEntity> searchMemberList(String name, List<FamilyMemberEntity> list) {
        if (TextUtils.isEmpty(name)) {
            return list;
        }
        List<FamilyMemberEntity> results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (FamilyMemberEntity memberEntity : list) {
            String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getUser_given_name());
            Matcher matcher = pattern.matcher(userName);
            if (matcher.find()) {
                results.add(memberEntity);
            }
        }
        return results;
    }

    private List<FamilyGroupEntity> searchGroupList(String name, List<FamilyGroupEntity> list) {
        List<FamilyGroupEntity> results = new ArrayList();
        Pattern pattern = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
        for (FamilyGroupEntity memberEntity : list) {
            String userName = PinYin4JUtil.getPinyinWithMark(memberEntity.getGroup_name()).toLowerCase();
            Matcher matcher = pattern.matcher(userName);
            if (matcher.find()) {
                results.add(memberEntity);
            }
        }
        return results;
    }

    private void showNoFriendDialog(final FamilyMemberEntity familyMemberEntity) {

        LayoutInflater factory = LayoutInflater.from(mContext);
        View selectIntention = factory.inflate(R.layout.dialog_bond_alert_member, null);
        showSelectDialog = new MyDialog(mContext, null, selectIntention);

        showSelectDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog.dismiss();
            }
        });
        selectIntention.findViewById(R.id.subject_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onceAdd(familyMemberEntity.getUser_id());
                showSelectDialog.dismiss();
            }
        });

        selectIntention.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awaitingRemove(familyMemberEntity.getUser_id());
                showSelectDialog.dismiss();
            }
        });

        selectIntention.findViewById(R.id.subject_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awaitingRemove(familyMemberEntity.getUser_id());
                showSelectDialog.dismiss();
            }
        });

        showSelectDialog.show();


    }

    public void onceAdd(String ActionUserId){
        vProgress.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AddMemberWorkFlow.class);
        intent.putExtra("from", MainActivity.getUser().getUser_id());
        intent.putExtra("to", ActionUserId);
        startActivityForResult(intent, ADD_MEMBER);
    }

    private void awaitingRemove(final String memberId) {

        vProgress.setVisibility(View.VISIBLE);
        RequestInfo requestInfo = new RequestInfo();
        requestInfo.url = Constant.API_BONDALERT_MEMEBER_REMOVE + MainActivity.getUser().getUser_id();
        Map<String, String> params = new HashMap<>();
        params.put("member_id", memberId);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        new HttpTools(getActivity()).put(requestInfo, null, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onResult(String string) {

                vProgress.setVisibility(View.GONE);
                MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                if (groupEntityList != null) {
                    groupAdapter.clearBitmap(groupEntityList);
                    groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
                    groupListView.setAdapter(groupAdapter);
                    getData();
                }
            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
       if(isVisibleToUser){
           if(MainActivity.IS_INTERACTIVE_USE && !PreferencesUtil.getValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_PHOTO,false)){
               //相当于Fragment的onResume
               if(MainActivity.IS_INTERACTIVE_USE && InteractivePopupWindow.firstOpPop){
                   popupWindow = new InteractivePopupWindow(getParentActivity(),getParentActivity().rightButton,popTestSt,0);
                   popupWindow.setDismissListener(new InteractivePopupWindow.PopDismissListener() {
                       @Override
                       public void popDismiss() {
                           LogUtil.i("==============2", "onDismiss");
                           PreferencesUtil.saveValue(getParentActivity(), InteractivePopupWindow.INTERACTIVE_TIP_ADD_MEMBER, true);
                           newPopAddPhoto();
                       }
                   });
                   popupWindow.showPopupWindow(true);

               } else {
                   handler.sendEmptyMessageDelayed(GET_DELAY_RIGHT,500);
                   InteractivePopupWindow.firstOpPop = true;
               }

//           newPopAddPhoto();
           }

       }
//       else {
//           //相当于Fragment的onPause
//           if(popupWindow != null){
//               popupWindow.dismissPopupWindow();
//           }
//       }
    }

    private void showMemberEmptyView() {
        if (memberRefreshLayout.getVisibility() == View.VISIBLE) {
            memberRefreshLayout.setVisibility(View.GONE);
        }

        emptyMemberLinear.setVisibility(View.VISIBLE);
        emptyMemberIv.setVisibility(View.VISIBLE);
        emptyMemberTv.setVisibility(View.VISIBLE);
    }

    private void showGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.VISIBLE) {
            groupRefreshLayout.setVisibility(View.GONE);
        }
        emptyGroupLinear.setVisibility(View.VISIBLE);
        emptyGroupIv.setVisibility(View.VISIBLE);
        emptyGroupTv.setVisibility(View.VISIBLE);
    }

    private void hideMemberEmptyView() {
        if (memberRefreshLayout.getVisibility() == View.GONE) {
            memberRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyMemberLinear.setVisibility(View.GONE);
    }

    private void hideGroupEmptyView() {
        if (groupRefreshLayout.getVisibility() == View.GONE) {
            groupRefreshLayout.setVisibility(View.VISIBLE);
        }
        emptyGroupLinear.setVisibility(View.GONE);
    }

    private List<View> initPagerView() {
        List<View> mLists = new ArrayList<>();
        View userView = LayoutInflater.from(mContext).inflate(R.layout.family_list_view_layout, null);
        final GridView userGridView = (GridView) userView.findViewById(R.id.family_grid_view);
        final ImageButton userIb = (ImageButton) userView.findViewById(R.id.ib_top);
        memberRefreshLayout = (MySwipeRefreshLayout) userView.findViewById(R.id.swipe_refresh_layout);
        emptyMemberLinear = (RelativeLayout) userView.findViewById(R.id.family_empty_linear);
        emptyMemberIv = (ImageView) userView.findViewById(R.id.family_memeber_image_empty);
        emptyMemberTv =  userView.findViewById(R.id.family_memeber_text_empty);
        userGridView.setAdapter(memberAdapter);
        userIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userGridView.setSelection(0);
            }
        });
        userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                FamilyMemberEntity familyMemberEntity = memberAdapter.getList().get(arg2);
                String userId = familyMemberEntity.getUser_id();

                if (FamilyFragment.FAMILY_TREE.equals(userId)) {
                    /** modify by Jackie*/
//                    getUrl();
                    // 跳转至家谱界面
                    Intent intent = new Intent(getActivity(), FamilyTreeActivity.class);
                    startActivity(intent);
                    /** modify end by Jackie*/
                } else if (FamilyFragment.FAMILY_MORE_MEMBER.equals(userId)) {
                    isopen = true;
                    int size = memberAdapter.getList().size();
//                    empmemberEntityList = memberAdapter.getList();
                    memberAdapter.addMoreData(moreMemberList);
//                    if(memberAdapter.getList().get(size-1).getUser_given_name()==FAMILY_MORE_MEMBER){
//                        if(memberEntityList!=null){
//                            opendate.clear();
//                            opendate.addAll(memberAdapter.getList());
////                            opendate.addAll(1,memberEntityList);
//                        }
//                    }

                } else if (FamilyFragment.FAMILY_HIDE_MEMBER.equals(userId)) {
                    isopen = false;
                    memberAdapter.addNewData(memberList);
                } else {
                    if ("0".equals(familyMemberEntity.getFam_accept_flag())) {
                        //不是好友,提示等待接收
                        if(showSelectDialog != null && showSelectDialog.isShowing()){
                            return;
                        }
                        showNoFriendDialog(familyMemberEntity);
                        LogUtil.i("familyMemberEntity", familyMemberEntity.getUser_id());

                        return;
                    } else {
                        //member, 跳转到个人资料页面需要
                        //put请求消除爱心
                        if ("".equals(familyMemberEntity.getMiss())) {
                            updateMiss(familyMemberEntity.getUser_id());
                            arg0.findViewById(R.id.myfamily_image_right).setVisibility(View.GONE);
                            familyMemberEntity.setMiss(null);
                        }
                        Intent intent = new Intent(getActivity(), FamilyProfileActivity.class);
                        intent.putExtra(UserEntity.EXTRA_MEMBER_ID, familyMemberEntity.getUser_id());
                        intent.putExtra(UserEntity.EXTRA_GROUP_ID,familyMemberEntity.getGroup_id());
                        intent.putExtra(UserEntity.EXTRA_GROUP_NAME, familyMemberEntity.getUser_given_name());

//                        intent.putExtra("relationship",familyMemberEntity.getTree_type_name());
//                        intent.putExtra("fam_nickname",familyMemberEntity.getFam_nickname());
//                        intent.putExtra("member_status",familyMemberEntity.getUser_status());
                        intent.putExtra("getDofeel_code", familyMemberEntity.getDofeel_code());
//                        startActivity(intent);
//                        startActivityForResult(intent, 1);
                    }
                }
            }
        });
        memberRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isMemberRefresh = true;
                isup = false;
                opendate.clear();
//                requestData();
                getData();
            }
        });
        userGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
                return false;
            }
        });
        userGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (userGridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    memberRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userGridView.getFirstVisiblePosition() == 0) {
                    userIb.setVisibility(View.GONE);
                    memberRefreshLayout.setEnabled(true);
                } else {
                    userIb.setVisibility(View.VISIBLE);
                    memberRefreshLayout.setEnabled(false);
                }
            }
        });

        mLists.add(userView);
        View groupView = LayoutInflater.from(mContext).inflate(R.layout.family_list_view_layout, null);
        groupListView = (GridView) groupView.findViewById(R.id.family_grid_view);
//        final GridView groupGridView = (GridView) groupView.findViewById(R.id.family_grid_view);

        final ImageButton groupIb = (ImageButton) groupView.findViewById(R.id.ib_top);
        groupRefreshLayout = (MySwipeRefreshLayout) groupView.findViewById(R.id.swipe_refresh_layout);
        emptyGroupLinear = (RelativeLayout) groupView.findViewById(R.id.family_empty_linear);
        emptyGroupIv = (ImageView) groupView.findViewById(R.id.family_group_image_empty);
        emptyGroupTv = groupView.findViewById(R.id.family_group_text_empty);
        emptyGroupTv.setVisibility(View.VISIBLE);
        groupListView.setAdapter(groupAdapter);
        groupIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupListView.setSelection(0);
            }
        });
        groupRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isGroupRefresh = true;
//                requestData();
                groupAdapter.clearBitmap(groupEntityList);
                groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
                groupListView.setAdapter(groupAdapter);
                getData();
            }

        });
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                //群组聊天
                Intent intent = new Intent(getActivity(), MessageChatActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("groupId", groupAdapter.getGroupList().get(arg2).getGroup_id());
                intent.putExtra("titleName", groupAdapter.getGroupList().get(arg2).getGroup_name());
                startActivityForResult(intent, 1);
//                startActivityForResult(intent,1);
            }
        });
        groupListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
                }
                return false;
            }
        });
        groupListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                    groupRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (groupListView.getFirstVisiblePosition() == 0) {
                    groupIb.setVisibility(View.GONE);
                    groupRefreshLayout.setEnabled(true);
                } else {
                    groupIb.setVisibility(View.VISIBLE);
                    groupRefreshLayout.setEnabled(false);
                }
            }
        });
        mLists.add(groupView);
        return mLists;
    }

    @Override
    public void onStart() {
        Log.i("onStart===","onStart");
        super.onStart();
        getData();
    }

    public void updateMiss(String member_id) {
        final RequestInfo requestInfo = new RequestInfo();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("member_id", member_id);
        requestInfo.jsonParam = UrlUtil.mapToJsonstring(params);
        requestInfo.url = String.format(Constant.API_UPDATE_MISS, MainActivity.getUser().getUser_id());
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
        new HttpTools(getActivity()).put(requestInfo, Tag, new HttpCallback() {
            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }

            @Override
            public void onResult(String string) {
                try {
                    JSONObject jsonObject = new JSONObject(string);
                    if ("200".equals(jsonObject.getString("response_status_code"))) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.text_successfully_dismiss_miss), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Exception e) {
                MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
            }

            @Override
            public void onCancelled() {

            }

            @Override
            public void onLoading(long count, long current) {

            }
        });

//            }
//        }.start();

    }


    class MyOnPageChanger implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                message_member_tv.setBackgroundResource(R.drawable.message_member_selected_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_normal_shap);
                message_group_tv.setTextColor(Color.parseColor("#666666"));
                message_member_tv.setTextColor(Color.parseColor("#ffffff"));
            } else {
                message_member_tv.setBackgroundResource(R.drawable.message_member_normal_shap);
                message_group_tv.setBackgroundResource(R.drawable.message_group_selected_shap);
                message_group_tv.setTextColor(Color.parseColor("#ffffff"));
                message_member_tv.setTextColor(Color.parseColor("#666666"));
            }
        }

    }

    class FamilyPagerAdapter extends PagerAdapter {

        private List<View> mLists;

        public FamilyPagerAdapter(List<View> array) {
            this.mLists = array;
        }

        @Override
        public int getCount() {
            return mLists.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mLists.get(arg1));
            return mLists.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }
    }

    private void showSelectDialog() {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        View selectIntention = factory.inflate(R.layout.dialog_message_title_right, null);

        showSelectDialog = new MyDialog(getParentActivity(), null, selectIntention);
        TextView tvAddNewMember = (TextView) selectIntention.findViewById(R.id.tv_add_new_member);
        TextView tvCreateNewGroup = (TextView) selectIntention.findViewById(R.id.tv_create_new_group);
        TextView cancelTv = (TextView) selectIntention.findViewById(R.id.tv_cancel);
        tvAddNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开添加成员页面
                startActivity(new Intent(getActivity(), AddNewMembersActivity.class));
                showSelectDialog.dismiss();
            }
        });

        tvCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getActivity(), CreateGroupActivity.class));
                //打开创建群组页面
                Intent intent = new Intent(getActivity(), InviteMemberActivity.class);
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

    private void getData() {
        if (!NetworkUtil.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
            finishReFresh();
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                new HttpTools(getActivity()).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null, Tag, new HttpCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFinish() {

                        vProgress.setVisibility(View.GONE);

                    }

                    @Override
                    public void onResult(String response) {
                        GsonBuilder gsonb = new GsonBuilder();
                        Gson gson = gsonb.create();
                        finishReFresh();
//                        if (mProgressDialog.isShowing()) {
//                            mProgressDialog.dismiss();
//                        }
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showMemberEmptyView();
                            showGroupEmptyView();
                        }
                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
                            showMemberEmptyView();
                            showGroupEmptyView();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
                            }.getType());
                            List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
                            }.getType());
                            Map<String, List> map = new HashMap<>();
                            if (memberList != null && memberList.size() > 0) {
                                hideMemberEmptyView();
                                map.put("private", memberList);
                            } else {
                                showMemberEmptyView();
                            }
                            if (groupList != null && groupList.size() > 0) {
                                hideGroupEmptyView();
                                map.put("group", groupList);
                            } else {
                                showGroupEmptyView();
                            }
//                            if(memberAdapter=null){
//                                int size = memberAdapter.getList().size();
//                                if (memberAdapter.getList().get(size-1).getUser_given_name()==FAMILY_MORE_MEMBER){
//                                    isopen  = true;
//                                }
//                            }
                            if (map.size() > 0) {
                                if (isopen) {
                                    Message.obtain(handler, GET_OP, map).sendToTarget();
                                } else {
                                    Message.obtain(handler, GET_DATA, map).sendToTarget();
                                }
                            }
                        } catch (JSONException e) {
                            finishReFresh();
                            showGroupEmptyView();
                            showMemberEmptyView();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
                        finishReFresh();
                    }

                    @Override
                    public void onCancelled() {
                    }

                    @Override
                    public void onLoading(long count, long current) {

                    }
                });
            }
        }.start();
    }

    @Override
    public void requestData() {

//        if (!NetworkUtil.isNetworkConnected(getActivity())) {
//            Toast.makeText(getActivity(), getResources().getString(R.string.text_no_network), Toast.LENGTH_SHORT).show();
//            finishReFresh();
//            return;
//        }
//
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                new HttpTools(getActivity()).get(String.format(Constant.API_GET_EVERYONE, MainActivity.getUser().getUser_id()), null,Tag, new HttpCallback() {
//                    @Override
//                    public void onStart() {
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        vProgress.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onResult(String response) {
//                        GsonBuilder gsonb = new GsonBuilder();
//                        Gson gson = gsonb.create();
//                        finishReFresh();
////                        if (mProgressDialog.isShowing()) {
////                            mProgressDialog.dismiss();
////                        }
//                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
//                            showMemberEmptyView();
//                            showGroupEmptyView();
//                        }
//                        if (TextUtils.isEmpty(response) || "{}".equals(response)) {
//                            showMemberEmptyView();
//                            showGroupEmptyView();
//                        }
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            List<FamilyMemberEntity> memberList = gson.fromJson(jsonObject.getString("user"), new TypeToken<ArrayList<FamilyMemberEntity>>() {
//                            }.getType());
//                            List<FamilyGroupEntity> groupList = gson.fromJson(jsonObject.getString("group"), new TypeToken<ArrayList<FamilyGroupEntity>>() {
//                            }.getType());
//                            Map<String, List> map = new HashMap<>();
//                            if (memberList != null && memberList.size() > 0) {
//                                hideMemberEmptyView();
//                                map.put("private", memberList);
//                            } else {
//                                showMemberEmptyView();
//                            }
//                            if (groupList != null && groupList.size() > 0) {
//                                hideGroupEmptyView();
//                                map.put("group", groupList);
//                            } else {
//                                showGroupEmptyView();
//                            }
////                            if(memberAdapter=null){
////                                int size = memberAdapter.getList().size();
////                                if (memberAdapter.getList().get(size-1).getUser_given_name()==FAMILY_MORE_MEMBER){
////                                    isopen  = true;
////                                }
////                            }
//                            if (map.size() > 0) {
//                                if(isopen){
//                                    Message.obtain(handler, GET_OP, map).sendToTarget();
//                                }else {
//                                    Message.obtain(handler, GET_DATA, map).sendToTarget();
//                                }
//                            }
//                        } catch (JSONException e) {
//                            finishReFresh();
//                            showGroupEmptyView();
//                            showMemberEmptyView();
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        MessageUtil.showMessage(getActivity(), R.string.msg_action_failed);
//                        finishReFresh();
//                    }
//
//                    @Override
//                    public void onCancelled() {
//                    }
//
//                    @Override
//                    public void onLoading(long count, long current) {
//
//                    }
//                });
//            }
//        }.start();

    }

    private void finishReFresh() {
        if (isMemberRefresh) {
            memberRefreshLayout.setRefreshing(false);
            isMemberRefresh = false;
        }
        if (isGroupRefresh) {
            groupRefreshLayout.setRefreshing(false);
            isGroupRefresh = false;
        }
    }

//    class FamilyGroupAdapter extends BaseAdapter implements Filterable {
//        List<FamilyGroupEntity> groupList;
//
//
//        public FamilyGroupAdapter(List<FamilyGroupEntity> groupList) {
//            this.groupList = groupList;
//        }
//
//        public void addData(List<FamilyGroupEntity> list) {
//            if (null == list || list.size() == 0) {
//                return;
//            }
//            groupList.clear();
//            groupList.addAll(list);
//            notifyDataSetChanged();
//        }
//
//        public List<FamilyGroupEntity> getGroupList() {
//            return groupList;
//        }
//
//        @Override
//        public int getCount() {
//            return groupList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return groupList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            if (null == convertView) {
//                convertView = LayoutInflater.from(mContext).inflate(R.layout.gridview_item_for_family, null);
//                viewHolder = new ViewHolder();
//                viewHolder.imageMain = (CircularNetworkImage) convertView.findViewById(R.id.family_image_main);
//                viewHolder.textName = (TextView) convertView.findViewById(R.id.family_name);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            FamilyGroupEntity familyGroupEntity = groupList.get(position);
//            viewHolder.textName.setText(familyGroupEntity.getGroup_name());
//            VolleyUtil.initNetworkImageView(mContext, viewHolder.imageMain, String.format(Constant.API_GET_GROUP_PHOTO,
//                    familyGroupEntity.getGroup_id()), R.drawable.default_head_icon, R.drawable.default_head_icon);
//            return convertView;
//        }
//
//        @Override
//        public Filter getFilter() {
//            return null;
//        }
//
//        class ViewHolder {
//            CircularNetworkImage imageMain;
//            TextView textName;
//        }
//    }

    private final static int ADD_MEMBER = 10;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("F_requestCode====",requestCode+"");
        Log.i("F_resultCode====",resultCode+"");
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    if(groupEntityList != null){
                        groupAdapter.clearBitmap(groupEntityList);
                        groupAdapter = new FamilyGroupAdapter(mContext, groupEntityList);
                        groupListView.setAdapter(groupAdapter);
                        getData();
                    }

                }
                break;

            case ADD_MEMBER:
                if (resultCode == getActivity().RESULT_OK) {
                    vProgress.setVisibility(View.GONE);
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_successed);
                }else {
                    MessageUtil.showMessage(getActivity(), R.string.msg_action_canceled);
                }
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_member_tv:
                pager.setCurrentItem(0);
                if (!TextUtils.isEmpty(MemeberSearch)) {
                    etSearch.setText(MemeberSearch);
                } else {
                    etSearch.setText("");
                }
                etSearch.setSelection(etSearch.length());
                break;
            case R.id.message_group_tv:
                pager.setCurrentItem(1);
                if (!TextUtils.isEmpty(GroupSearch)) {
                    etSearch.setText(GroupSearch);
                } else {
                    etSearch.setText("");
                }
                opendate.clear();
                opendate.addAll(memberAdapter.getList());
                etSearch.setSelection(etSearch.length());
                break;
        }
    }

    private void showPDF(String url) {
        if (TextUtils.isEmpty(url))
            return;
        Intent intent = new Intent(getActivity(), ViewPDFActivity.class);
        startActivity(intent);
    }
}