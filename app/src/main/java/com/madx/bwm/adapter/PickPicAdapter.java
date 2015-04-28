package com.madx.bwm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.madx.bwm.R;
import com.madx.bwm.widget.MyDialog;

import java.util.List;
import java.util.Map;

/**
 * Created by wing on 15/3/23.
 */
public class PickPicAdapter extends SimpleAdapter{

    /**
     * 当前类LGO信息的TAG，打印调试信息时用于识别输出LOG所在的类
     */
    private final static String TAG = PickPicAdapter.class.getSimpleName();

    private  List<? extends Map<String, ?>> mMata;

    /**
     *删除弹出提示框，删除已经添加的图片时弹出是否删除图片的提示，用户确认删除时才删除
     */
    private MyDialog mRemoveAlertDialog;
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

        final View view = super.getView(position, convertView, parent);

        view.findViewById(R.id.pic_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mRemoveAlertDialog == null){ //没有被实例化，先实例化
                    mRemoveAlertDialog = new MyDialog(view.getContext(), R.string.text_tips_title, R.string.alert_wall_del_photo);

                    mRemoveAlertDialog.setButtonAccept(R.string.accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点击确认删除图片并关闭提示框
                            mMata.remove(position);
                            notifyDataSetChanged();
                            if(mSelectImageListener != null) {
                                mSelectImageListener.onImageDelete(position);
                            }

                            mRemoveAlertDialog.dismiss();
                        }
                    });

                    mRemoveAlertDialog.setButtonCancel(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 点击取消关闭提示框不删除图片
                            mRemoveAlertDialog.dismiss();
                        }
                    });
                } else {
                    Log.i(TAG, "mRemoveAlertDialog is init");
                }
                mRemoveAlertDialog.show();
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
