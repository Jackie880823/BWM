package com.bondwithme.BondCorp.ui.family;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.tools.HttpTools;
import com.bondwithme.BondCorp.Constant;
import com.bondwithme.BondCorp.R;
import com.bondwithme.BondCorp.adapter.RelationshipAdapter;
import com.bondwithme.BondCorp.entity.FamilyMemberEntity;
import com.bondwithme.BondCorp.entity.RelationshipEnum;
import com.bondwithme.BondCorp.ui.BaseFragment;
import com.bondwithme.BondCorp.ui.MainActivity;
import com.bondwithme.BondCorp.ui.OnFamilyItemClickListener;
import com.bondwithme.BondCorp.util.LogUtil;
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
     * 点用户记录
     */
    private ArrayList<String> clickUseIds = new ArrayList<>();

    /**
     * 加载视图
     */
    private RelativeLayout rlProgress;
    /**
     * 返回提示用户自己的树
     */
    private TextView tvGoToMe;
    /**
     * 返回上一次点击的提示
     */
    private TextView tvPrevious;


    private String selectUseId;

    public String getSelectUseId() {
        return selectUseId;
    }

    private String useId;

    public static FamilyTreeFragment newInstance(String... params) {
        return createInstance(new FamilyTreeFragment(), params);
    }

    public FamilyTreeFragment(){
        super();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.relationship_fragment;
    }

    @Override
    protected void setParentTitle() {

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

        tvGoToMe = getViewById(R.id.back_me_btn);

        // 回去到用户提示设置成黑底白字
        tvGoToMe.setTextColor(Color.WHITE);
        tvGoToMe.setBackgroundResource(R.drawable.family_tree_btn_gb);

        tvPrevious = getViewById(R.id.previous_btn);

        tvGoToMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectUseId.equals(useId) && rlProgress.getVisibility() != View.VISIBLE) {
                    selectUseId = useId;
                    requestData();
                }
            }
        });

        tvPrevious.setEnabled(false);
        tvPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selectUseId = getPrevious();
                } catch(Exception e) {
                    selectUseId = useId;
                } finally {
                    requestData();
                }
            }

            private String getPrevious() throws Exception {
                String result = null;
                if(!clickUseIds.isEmpty()) {
                    result = clickUseIds.remove(clickUseIds.size() - 1);
                    if(selectUseId.equals(result)) {
                        result = getPrevious();
                    }
                    return result;
                } else {
                    throw new Exception("list size is empty");
                }

            }
        });

        selectUseId = useId = MainActivity.getUser().getUser_id();
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
        if(selectUseId.equals(useId)) {

            //  返回用户清除记录
            clickUseIds.clear();

            // 上一步提示设置成白底黑字
            tvPrevious.setTextColor(Color.BLACK);
            tvPrevious.setEnabled(false);
//            tvPrevious.setBackgroundColor(Color.WHITE);
        } else {
            // 上一步提示设置成黑底白字
            tvPrevious.setTextColor(Color.WHITE);
            tvPrevious.setEnabled(true);
//            tvPrevious.setBackgroundColor(Color.BLACK);
        }

        rlProgress.setVisibility(View.VISIBLE);
        String url = String.format(Constant.API_FAMILY_RELATIONSHIP, useId, selectUseId);
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
                    if(selectUseId.equals(familyMemberEntity.getUser_id())) {
                        LogUtil.i(TAG, "parseResponse& add first");
                        siblingMembers.add(0, familyMemberEntity);
                    } else {
                        siblingMembers.add(familyMemberEntity);
                    }
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
        if(!selectUseId.equals(entityUseId)) {
            selectUseId = entity.getUser_id();
            clickUseIds.add(entity.getUser_id());
            requestData();
        }
    }
}
