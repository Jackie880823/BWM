package com.madxstudio.co8.ui.workspace;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.madxstudio.co8.R;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.ui.MainActivity;
import com.madxstudio.co8.ui.workspace.dummy.DummyContent;
import com.madxstudio.co8.ui.workspace.dummy.DummyContent.Workspace;


/**
 * @author Jackie
 *
 */
public class WorkSpaceFragment extends BaseFragment<MainActivity> {



    public static BaseFragment newInstance(String... params) {

        return createInstance(new WorkSpaceFragment(), params);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WorkSpaceFragment() {
    }

    @Override
    public void setLayoutId() {
        layoutId = R.layout.fragment_workspace;
    }

    /**
     * 强制定义title,请在重写时直接调用setTitle方法,如果不想设置title比如Activity只有一个fragment时重写不做任何事就OK
     */
    @Override
    protected void setParentTitle() {

    }

    @Override
    public void initView() {
        RecyclerView recyclerView = getViewById(R.id.workspace_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MyWorkSpaceRecyclerViewAdapter(DummyContent.ITEMS, new
                OnListFragmentInteractionListener() {

            @Override
            public void onListFragmentInteraction(Workspace item) {

            }
        }));
    }

    @Override
    public void requestData() {

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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Workspace item);
    }
}
