package com.bondwithme.BondWithMe.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

import java.lang.reflect.Field;

/**
 * Created by wing on 15/3/22.
 */
public class CustomGridView extends GridView {
    public CustomGridView(Context context){
        super(context);
    }

    public CustomGridView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public int getColumnWidth(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) return super.getColumnWidth();
        else{
            try{
                Field field = GridView.class.getDeclaredField("mColumnWidth");
                field.setAccessible(true);
                Integer value = (Integer) field.get(this);
                field.setAccessible(false);
                return value.intValue();
            }catch(NoSuchFieldException e){
                throw new RuntimeException(e);
            }catch(IllegalAccessException e){
                throw new RuntimeException(e);
            }
        }
    }
}
