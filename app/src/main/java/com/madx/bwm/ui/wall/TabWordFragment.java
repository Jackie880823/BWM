package com.madx.bwm.ui.wall;

import android.text.Editable;
import android.util.Log;

import com.madx.bwm.R;
import com.madx.bwm.entity.GroupEntity;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.ui.BaseFragment;
import com.madx.bwm.widget.WallEditView;

import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.madx.bwm.ui.TabWordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.madx.bwm.ui.TabWordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabWordFragment extends BaseFragment<WallNewActivity> {

    private final static String TAG = TabPictureFragment.class.getSimpleName();
    private List<UserEntity> mMembers;
    private List<GroupEntity> mGroups;

    public static TabWordFragment newInstance(String... params) {

        return createInstance(new TabWordFragment());
    }

    public TabWordFragment() {
        super();
        // Required empty public constructor
    }

    //
    //    @Override
    //    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    //                             Bundle savedInstanceState) {
    //        // Inflate the layout for this fragment
    //        return inflater.inflate(R.layout.fragment_event, container, false);
    //    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_tab_word;
    }

    @Override
    public void initView() {

        WallEditView text_content = getViewById(R.id.wall_text_content);
        text_content.setTextChangeListener(new WallEditView.TextChangeListener() {
            int lastChange = CHANGE_MODE_NORMAL;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s, int change) {
                switch(change) {
                    case CHANGE_MODE_NORMAL:
                        if(lastChange == CHANGE_MODE_BLACK_CHANGE) {
                            lastChange = change;
                            changeAtDesc(mMembers, mGroups, true);
                            return;
                        }
                        break;
                    case CHANGE_MODE_DELETE_AT_ALL:
                        if(mMembers != null) {
                            mMembers.clear();
                        }
                        if(mGroups != null) {
                            mGroups.clear();
                        }
                        break;

                    case CHANGE_MODE_DELETE_AT_GROUPS:
                        if(mGroups != null) {
                            mGroups.clear();
                        }
                        break;

                    case CHANGE_MODE_DELETE_AT_MEMBER:
                        if(mMembers != null) {
                            mMembers.clear();
                        }
                        break;
                }
                lastChange = change;
            }
        });

    }

    public WallEditView getEditText4Content() {
        return getViewById(R.id.wall_text_content);
    }

    public void changeAtDesc(List<UserEntity> members, List<GroupEntity> groups, boolean checkVisible) {
        mMembers = members;
        mGroups = groups;

        WallEditView editText = getEditText4Content();
        String memberText;
        String groupText;
        if(members != null && mMembers.size() > 0) {
            memberText = String.format(getParentActivity().getString(R.string.text_wall_content_at_member_desc) + " ", mMembers.size());
            Log.i(TAG, "changeAtDesc& member of at description is " + memberText);
        } else {
            Log.i(TAG, "changeAtDesc& no member of at description");
            memberText = "";
        }
        if(groups != null && mGroups.size() > 0) {
            groupText = String.format(getParentActivity().getString(R.string.text_wall_content_at_group_desc) + " ", mGroups.size());
            Log.i(TAG, "changeAtDesc& group of at description is " + groupText);
        } else {
            Log.i(TAG, "changeAtDesc& no group of at description");
            groupText = "";
        }
        if(!checkVisible) {
            editText.addAtDesc(memberText, groupText, true);
        } else {
            editText.addAtDesc(memberText, groupText, isVisible());
        }
    }

    @Override
    public void requestData() {

    }


}
