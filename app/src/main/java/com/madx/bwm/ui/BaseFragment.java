package com.madx.bwm.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madx.bwm.interfaces.IViewCommon;

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (P) activity;
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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

}
