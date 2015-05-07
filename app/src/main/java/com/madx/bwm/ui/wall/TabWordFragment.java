package com.madx.bwm.ui.wall;

import android.text.Editable;

import com.madx.bwm.R;
import com.madx.bwm.entity.UserEntity;
import com.madx.bwm.ui.BaseFragment;
import com.madx.bwm.widget.WallEditView;

import java.util.ArrayList;
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
    private List<UserEntity> members ;

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
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public WallEditView getEditText4Content(){
        return getViewById(R.id.wall_text_content);
    }

    public void changeAtDesc(List<UserEntity> members) {
        this.members = members;

        WallEditView editText = getEditText4Content();
        if(members != null && members.size() > 0) {
            String text = String.format(getParentActivity().getString(R.string.tag_members), members.size());
            editText.addAtDesc(text);
        } else {
            editText.addAtDesc("");
        }

    }

    @Override
    public void requestData() {

    }


}
