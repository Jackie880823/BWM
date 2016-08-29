package com.madxstudio.co8.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.madxstudio.co8.R;

/**
 * fragmentActivity 基类
 * @author wing
 */
public abstract class BaseFragmentActivity extends SuperActivity implements OnClickListener, BaseFragment
        .OnFragmentInteractionListener{


    private FragmentManager fm;

    protected boolean savedState = false;

    /*当activity无法处理，需要fragent处理时，使用该接口*/
    protected CommandListener familyCommandListener;         //很重要，把activity中的命令，传递给fragment
    protected CommandListener commandlistener;
    /********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        fm = getSupportFragmentManager();
    }

    /***********************************************/
    @Override
    public void onClick(View v) {

//        if(commandlistener!=null){
//            commandlistener.execute(v);
//        }

    }

    public void setCommandlistener(CommandListener commandlistener) {
        this.commandlistener = commandlistener;
    }
    public void setFamilyCommandListener(CommandListener commandlistener) {
        this.familyCommandListener = commandlistener;
    }
    public interface CommandListener
    {
        public boolean execute(View v);
    }

    protected void changeFragment(Fragment f, boolean init){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, f);
        if(!init)
            ft.addToBackStack(null);
        ft.commit();
    }


}