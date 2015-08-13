package com.bondwithme.BondWithMe.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bondwithme.BondWithMe.R;
import com.bondwithme.BondWithMe.adapter.RelationshipAdapter;
import com.bondwithme.BondWithMe.entity.UserEntity;

import java.util.ArrayList;

/**
 * Created by Jackie on 8/13/15.
 *
 * @author Jackie
 * @version 1.0
 */
public class FamilyTreeFragment extends BaseFragment<MainActivity> {
    private static final String TAG = FamilyTreeFragment.class.getSimpleName();

    private ArrayList<ArrayList<UserEntity>> data = new ArrayList<>();
    private RecyclerView parentRelation;
    private RecyclerView brotherRelation;
    private RecyclerView childRelation;

    public static FamilyTreeFragment newInstance(String... params) {
        return createInstance(new FamilyTreeFragment(), params);
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.relationship_fragment;
    }

    @Override
    public void initView() {
        LinearLayoutManager ll0 = new LinearLayoutManager(getActivity());
        LinearLayoutManager ll1 = new LinearLayoutManager(getActivity());
        LinearLayoutManager ll2 = new LinearLayoutManager(getActivity());

        parentRelation = getViewById(R.id.parent_relation_recycler_view);
        brotherRelation = getViewById(R.id.brother_relation_recycler_view);
        childRelation = getViewById(R.id.child_relation_recycler_view);

        ll0.setOrientation(LinearLayoutManager.HORIZONTAL);
        ll1.setOrientation(LinearLayoutManager.HORIZONTAL);
        ll2.setOrientation(LinearLayoutManager.HORIZONTAL);

        parentRelation.setLayoutManager(ll0);
        brotherRelation.setLayoutManager(ll1);
        childRelation.setLayoutManager(ll2);
    }

    @Override
    public void requestData() {
        for(int i = 0; i < 10; i++) {
            ArrayList<UserEntity> users = new ArrayList<>();
            users.add(MainActivity.getUser());
            if(i % 2 == 0) {
                users.add(MainActivity.getUser());
            }
            data.add(users);
        }
        RelationshipAdapter adapter = new RelationshipAdapter(data, getActivity());
        parentRelation.setAdapter(adapter);
        brotherRelation.setAdapter(adapter);
        childRelation.setAdapter(adapter);
    }
}
