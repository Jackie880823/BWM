package com.bondwithme.BondWithMe.ui;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bondwithme.BondWithMe.R;
<<<<<<< HEAD
=======
import com.bondwithme.BondWithMe.entity.PhotoEntity;
import com.bondwithme.BondWithMe.entity.UserEntity;
import com.bondwithme.BondWithMe.http.VolleyUtil;
import com.bondwithme.BondWithMe.util.LogUtil;
import com.bondwithme.BondWithMe.util.MyDateUtils;
import com.bondwithme.BondWithMe.widget.CircularNetworkImage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
>>>>>>> 88f54315fd6daf8c186c295608ccc9b6fda12851

public class FamilyViewProfileActivity extends BaseActivity {


    @Override
    protected void initBottomBar() {
        super.initTitleBar();
    }

    @Override
    protected void initTitleBar() {
        super.initTitleBar();
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setTitle() {
        /**
         * begin QK
         */
        tvTitle.setText(getResources().getString(R.string.title_family_profile));
        /**
         * end
         */
    }

    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return new FamilyViewProfileFragment();
    }

    @Override
    public void initView() {

    }

<<<<<<< HEAD
=======
    private void setTvBirthday(String strDOB) {
        if (strDOB != null && !strDOB.equals("0000-00-00")){
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(strDOB);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //不同语言设置不同日期显示
            if (Locale.getDefault().toString().equals("zh_CN")){
            tvBirthday.setText(date.getMonth() + "月" + date.getDay() + "日");
//                DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
//                tvBirthday.setText(dateFormat.format(date));
            }else {
                /**wing modified for system month name desc*/
//                tvBirthday.setText(this.getResources().getStringArray(R.array.months)[date.getMonth()] + " " + date.getDate());
                tvBirthday.setText(MyDateUtils.getMonthNameArray(false)[date.getMonth()] + " " + date.getDate());
                /**wing modified for system month name desc*/
            }
        }
    }
>>>>>>> 88f54315fd6daf8c186c295608ccc9b6fda12851

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
