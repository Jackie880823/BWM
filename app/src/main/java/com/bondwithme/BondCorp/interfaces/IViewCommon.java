package com.madxstudio.co8.interfaces;

import android.view.View;

public interface IViewCommon {

    public void initView ();
    public void requestData();
    public <V extends View> V getViewById (int id);

}
