package com.madx.bwm.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.madx.bwm.R;
import com.madx.bwm.adapter.BirthdayAdapter;
import com.madx.bwm.entity.BirthdayEntity;

import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BirthdayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BirthdayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BirthdayFragment extends BaseFragment<BirthdayActivity> {

    private static List<BirthdayEntity> birthdayEvents;

    public static BirthdayFragment newInstance(List<BirthdayEntity> birthdayEvents,String... params) {
        BirthdayFragment.birthdayEvents = birthdayEvents;
        return createInstance(new BirthdayFragment());
    }

    public BirthdayFragment() {
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
        this.layoutId = R.layout.fragment_birthday;
    }

    private RecyclerView rvList;
    @Override
    public void initView() {

        rvList = getViewById(R.id.rv_birthdays);
        LinearLayoutManager llm = new LinearLayoutManager(getParentActivity());
        rvList.setLayoutManager(llm);
        rvList.setHasFixedSize(true);

        rvList.setAdapter(new BirthdayAdapter(getParentActivity(),birthdayEvents));


    }

    @Override
    public void requestData() {

    }
}
