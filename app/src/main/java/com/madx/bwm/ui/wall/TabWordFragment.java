package com.madx.bwm.ui.wall;

import android.text.Editable;

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
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s, int change) {
                switch(change) {
                    case CHANGE_MODE_DLETE_AT_ALL:
                        if(mMembers != null) {
                            mMembers.clear();
                        }
                        if(mGroups != null) {
                            mGroups.clear();
                        }
                        break;

                    case CHANGE_MODE_DLETE_AT_GROUPS:
                        if(mGroups != null) {
                            mGroups.clear();
                        }
                        break;

                    case CHANGE_MODE_DLETE_AT_MEMBER:
                        if(mMembers != null) {
                            mMembers.clear();
                        }
                        break;
                }

            }
        });

    }

    public WallEditView getEditText4Content(){
        return getViewById(R.id.wall_text_content);
    }

    public void changeAtDesc(List<UserEntity> members, List<GroupEntity> groups) {
        this.mMembers = members;
        this.mGroups = groups;

        WallEditView editText = getEditText4Content();
        String memberText;
        String groupText;
        if(members != null && members.size() > 0) {
            memberText = String.format(getParentActivity().getString(R.string.text_wall_content_at_member_desc), members.size());
        } else {
            memberText = "";
        }
        if(groups != null && groups.size() > 0) {
            groupText = String.format(getParentActivity().getString(R.string.text_wall_content_at_member_desc), groups.size());
        } else {
            groupText = "";
        }

        editText.addAtDesc(memberText, groupText);

    }

    @Override
    public void requestData() {

    }


}
