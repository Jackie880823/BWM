package com.bondwithme.BondWithMe.ui.family;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondWithMe.Constant;
import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.RelationshipAdapter;
import com.bondwithme.BondWithMe.entity.FamilyMemberEntity;
import com.bondwithme.BondWithMe.entity.RelationshipEnum;
import com.bondwithme.BondWithMe.ui.BaseFragment;
import com.bondwithme.BondWithMe.ui.MainActivity;
import com.bondwithme.BondWithMe.ui.OnFamilyItemClickListener;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * 家庭树{@link BaseFragment}用于显示用户家谱关系图
 * Created by Jackie on 8/13/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FamilyTreeFragment extends BaseFragment<FamilyTreeActivity> implements OnFamilyItemClickListener {
    private static final String TAG = FamilyTreeFragment.class.getSimpleName();

    /**
     * 父辈关系列表视图
     */
    private RecyclerView parentRelation;
    /**
     * 兄弟姐妹关系列表视图
     */
    private RecyclerView siblingRelation;
    /**
     * 孩子关系列表视图
     */
    private RecyclerView childrenRelation;
    /**
     * 父辈关系数据适配器
     */
    private RelationshipAdapter parentAdapter = null;
    /**
     * 兄弟姐妹关系数据适配器
     */
    private RelationshipAdapter siblingAdapter = null;
    /**
     * 孩子关系数据适配器
     */
    private RelationshipAdapter childrenAdapter = null;
    /**
     * 父辈关系数据
     */
    private ArrayList<FamilyMemberEntity> parentMembers = new ArrayList<>();
    /**
     * 兄弟姐妹关系数据
     */
    private ArrayList<FamilyMemberEntity> siblingMembers = new ArrayList<>();
    /**
     * 孩子关系数据
     */
    private ArrayList<FamilyMemberEntity> childrenMembers = new ArrayList<>();

    /**
     * 加载视图
     */
    private RelativeLayout rlProgress;
    /**
     * 返回提示
     */
    private TextView tvGoBack;


    private static String currentUseId = MainActivity.getUser().getUser_id();
    public static String getCurrentUseId() {
        return currentUseId;
    }

    private String useId = currentUseId;

    public static FamilyTreeFragment newInstance(String... params) {
        return createInstance(new FamilyTreeFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.relationship_fragment;
    }

    @Override
    public void initView() {

        rlProgress = getViewById(R.id.rl_progress);

        parentRelation = getViewById(R.id.parent_relation_recycler_view);
        siblingRelation = getViewById(R.id.sibling_relation_recycler_view);
        childrenRelation = getViewById(R.id.children_relation_recycler_view);

        initRecyclerView(parentRelation);
        initRecyclerView(siblingRelation);
        initRecyclerView(childrenRelation);

        tvGoBack = getViewById(R.id.back_me_tv);

        tvGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentUseId.equals(useId)) {
                    currentUseId = useId;
                    requestData();
                }
            }
        });

        currentUseId = useId;
    }

    /**
     * 初始化列表
     *
     * @param recyclerView 列表UI
     */
    private void initRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void requestData() {
        if(currentUseId.equals(useId)) {
            tvGoBack.setText(R.string.text_me);
        } else {
            tvGoBack.setText(R.string.go_back_to_me);
        }

        rlProgress.setVisibility(View.VISIBLE);
        String url = String.format(Constant.API_FAMILY_RELATIONSHIP, useId, currentUseId);
        LogUtil.i(TAG, "requestData& url: " + url);
        new HttpTools(getActivity()).get(url, null, TAG, new HttpCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish() {
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onResult(String response) {
                LogUtil.i(TAG, "onResult& response: " + response);
                parseResponse(response);
            }

            @Override
            public void onError(Exception e) {
                rlProgress.setVisibility(View.GONE);
                e.printStackTrace();
            }

            @Override
            public void onCancelled() {
                rlProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoading(long count, long current) {

            }
        });
    }

    /**
     * 解析请求返回的数据{@code response},得到各级关系列表并显示
     *
     * @param response 请求返回的数据
     */
    private void parseResponse(String response) {
        Gson gson = new Gson();
        ArrayList<FamilyMemberEntity> data = gson.fromJson(response, new TypeToken<ArrayList<FamilyMemberEntity>>() {}.getType());

        // 清空原有的父辈数据
        parentMembers.clear();
        // 清空原有的兄弟姐妹数据
        siblingMembers.clear();
        // 清空原有的孩子数据
        childrenMembers.clear();

        for(FamilyMemberEntity familyMemberEntity : data) {
            String type = familyMemberEntity.getRelationship_type();
            RelationshipEnum relationship = RelationshipEnum.valueOf(type);
            switch(relationship) {
                case parent:
                    parentMembers.add(familyMemberEntity);
                    break;

                case sibling:
                    siblingMembers.add(familyMemberEntity);
                    break;
                case children:
                    childrenMembers.add(familyMemberEntity);
                    break;

                default:
                    break;
            }

        }

        // 更新父辈关系列表
        updateAdapter(parentRelation, parentAdapter, parentMembers, RelationshipEnum.parent);
        // 更新兄弟姐妹关系列表
        updateAdapter(siblingRelation, siblingAdapter, siblingMembers, RelationshipEnum.sibling);
        // 更新孩子关系列表
        updateAdapter(childrenRelation, childrenAdapter, childrenMembers, RelationshipEnum.children);
    }

    /**
     * 更新{@code recyclerView}的{@code adapter}的数据
     *
     * @param recyclerView 列表视图{@link RecyclerView}
     * @param adapter      需要更新或初始化的{@link RelationshipAdapter}
     * @param members      装入{@code adapter}的数据
     * @param type         关系类型枚举:{@link RelationshipEnum#parent}; {@link RelationshipEnum#children}; {@link RelationshipEnum#sibling}
     */
    private void updateAdapter(RecyclerView recyclerView, RelationshipAdapter adapter, ArrayList<FamilyMemberEntity> members, RelationshipEnum type) {
        if(adapter == null) {
            adapter = new RelationshipAdapter(getActivity(), members, type);
            adapter.setListener(this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setData(members);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 被点击
     *
     * @param view   被点击的{@link View}
     * @param entity 当前点击的用户实例{@link FamilyMemberEntity}
     */
    @Override
    public void onClick(View view, FamilyMemberEntity entity) {
        String entityUseId = entity.getUser_id();
        if(!currentUseId.equals(entityUseId)) {
            currentUseId = entity.getUser_id();
            requestData();
        }
    }
}
