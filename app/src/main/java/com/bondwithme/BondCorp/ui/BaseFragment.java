package com.madxstudio.co8.ui;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madxstudio.co8.interfaces.IViewCommon;

/**
 * fragment 基类
 * @author wing
 */
public abstract class BaseFragment<P extends Activity> extends Fragment implements IViewCommon {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM_PREFIX = "param";

    protected int paramsCount;

    private OnFragmentInteractionListener mListener;
    private P mActivity;

    protected static <T extends BaseFragment> T createInstance(T fragment ,String... params) {
        if(params!=null&&params.length>0){
            Bundle args = new Bundle();
            int length = params.length;
            for (int i = 0; i < length; i++) {
                args.putString(ARG_PARAM_PREFIX+i, params[i]);
            }
            fragment.setArguments(args);
        }
        return fragment;
    }

    protected int layoutId;

    protected View rootView;

    public abstract void setLayoutId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setLayoutId();
        if (null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (null != parent) {
                parent.removeView(rootView);
            }
        } else {
            rootView = inflater.inflate(layoutId, null);
            initView();
            requestData();
        }
        return rootView;
    }

    /**强制定义title,请在重写时直接调用setTitle方法,如果不想设置title比如Activity只有一个fragment时重写不做任何事就OK*/
    protected abstract void setParentTitle();

    /**设置父窗title,如果父窗为 BaseActivity*/
    protected void setTitle(String title){
        if(mActivity!=null&&mActivity instanceof BaseActivity){
            ((BaseActivity)mActivity).tvTitle.setText(title);
            mTitle = title;
        }
    }

    protected String mTitle;

    protected P getParentActivity(){
        return mActivity;
    }

    @Override
    public <V extends View> V getViewById(int id) {
        return (V)rootView.findViewById(id);
    }

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    public abstract void initViews();
//    public abstract void requestData();

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setParentTitle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            setParentTitle();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a = null;
        if (context instanceof Activity){
            a=(Activity) context;
        }
        if(a!=null) {
            this.mActivity = (P) a;
            try {
                mListener = (OnFragmentInteractionListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void refreshView(){
        rootView = LayoutInflater.from(getActivity()).inflate(layoutId, null);
        initView();
        requestData();
    }

}
