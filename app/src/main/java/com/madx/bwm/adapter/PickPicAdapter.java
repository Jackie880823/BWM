package com.madx.bwm.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.madx.bwm.R;

import java.util.List;
import java.util.Map;

/**
 * Created by wing on 15/3/23.
 */
public class PickPicAdapter extends SimpleAdapter{

    private  List<? extends Map<String, ?>> mMata;
    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     *                 item. The layout file should include at least those named views defined in "to"
     * @param from     A list of column names that will be added to the Map associated with each
     *                 item.
     * @param to       The views that should display column in the "from" parameter. These should all be
     *                 TextViews. The first N views in this list are given the values of the first N columns
     */
    public PickPicAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mMata = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = super.getView(position, convertView, parent);

        Map<String ,Object> item = (Map<String, Object>) getItem(position);

//        ((ImageView)view.findViewById(R.id.iv_pic)).setImageBitmap((Bitmap) item.get("pic_resId"));

        view.findViewById(R.id.pic_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMata.remove(position);
                notifyDataSetChanged();
                if(mSelectImageListener!=null) {
                    mSelectImageListener.onImageDelete(position);
                }
            }
        });

        return view;
    }

    private SelectImageListener mSelectImageListener;

    public void setSelectImageListener(SelectImageListener selectImageListener){
        mSelectImageListener = selectImageListener;
    }

    public interface SelectImageListener{
        void onImageDelete(int index);
    }

}
