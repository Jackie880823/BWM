package com.bondwithme.BondWithMe.ui;

import com.bondwithme.BondWithMe.R;

/**
 * Created by quankun on 16/1/25.
 */
public class WriteNewsFragment extends BaseFragment<WriteNewsActivity> {

    public static WriteNewsFragment newInstance(String... params) {

        return createInstance(new WriteNewsFragment());
    }

    public WriteNewsFragment() {
        super();
        // Required empty public constructor
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.activity_write_new;
    }

    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {

    }

    /**
     * 返回键监听
     *
     * @return
     */
    public boolean backCheck() {
//        if(isEventDate()) {
//            showSaveAlert();
//            return true;
//        } else {
//            getParentActivity().finish();
//            return false;
//        }
        return false;
    }

    @Override
    public void requestData() {

    }
}
