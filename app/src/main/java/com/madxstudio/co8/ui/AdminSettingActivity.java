package com.madxstudio.co8.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;

public class AdminSettingActivity extends BaseActivity {

    private ListView lvSettings;

    private int[] settings = new int[]{R.string.pending_requests, R.string.view_all_staff, R.string.view_others};

    @Override
    public int getLayout() {
        return R.layout.activity_admin_setting;
    }

    /**
     * 初始底部栏，没有可以不操作
     */
    @Override
    protected void initBottomBar() {

    }

    /**
     * 设置title
     */
    @Override
    protected void setTitle() {
        tvTitle.setText(R.string.title_admin_settings);
    }

    /**
     * TitilBar 右边事件
     */
    @Override
    protected void titleRightEvent() {

    }

    @Override
    protected Fragment getFragment() {
        return null;
    }

    @Override
    public void initView() {
        lvSettings = getViewById(R.id.admin_settings_list);
        AdminSettingsAdapter adminSettingsAdapter = new AdminSettingsAdapter(this, settings);
        lvSettings.setAdapter(adminSettingsAdapter);
        lvSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent pendingRequestsIntent = new Intent(AdminSettingActivity.this, OrgDetailActivity.class);
                        pendingRequestsIntent.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_OTHER);
                        pendingRequestsIntent.putExtra(Constant.REQUEST_TYPE, Constant.ADMIN_REQUEST);
                        startActivity(pendingRequestsIntent);
                        break;
                    case 1:
                        Intent allStaffIntent = new Intent(AdminSettingActivity.this, OrgDetailActivity.class);
                        allStaffIntent.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_STAFF);
                        allStaffIntent.putExtra(Constant.REQUEST_TYPE, Constant.ADMIN_REQUEST);
                        startActivity(allStaffIntent);
                        break;
                    case 2:
                        Intent othersIntent = new Intent(AdminSettingActivity.this, OrgDetailActivity.class);
                        othersIntent.putExtra(Constant.ORG_TRANSMIT_DATA, Constant.ORG_TRANSMIT_OTHER);
                        othersIntent.putExtra(Constant.REQUEST_TYPE, Constant.ADMIN_REQUEST);
                        startActivity(othersIntent);
                        break;
                }
            }
        });
    }

    @Override
    public void requestData() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private static class AdminSettingsAdapter extends BaseAdapter {
        private Context context;
        private int[] items;
        public AdminSettingsAdapter(Context context, int[] items) {
            this.context = context;
            this.items = items;
        }

        /**
         * How many items are in the data set represented by this Adapter.
         *
         * @return Count of items.
         */
        @Override
        public int getCount() {
            return items.length;
        }

        /**
         * Get the data item associated with the specified position in the data set.
         *
         * @param position Position of the item whose data we want within the adapter's
         *                 data set.
         * @return The data at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return items[position];
        }

        /**
         * Get the row id associated with the specified position in the list.
         *
         * @param position The position of the item within the adapter's data set whose row id we want.
         * @return The id of the item at the specified position.
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * Get a View that displays the data at the specified position in the data set. You can either
         * create a View manually or inflate it from an XML layout file. When the View is inflated, the
         * parent View (GridView, ListView...) will apply default layout parameters unless you use
         * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
         * to specify a root view and to prevent attachment to the root.
         *
         * @param position    The position of the item within the adapter's data set of the item whose view
         *                    we want.
         * @param convertView The old view to reuse, if possible. Note: You should check that this view
         *                    is non-null and of an appropriate type before using. If it is not possible to convert
         *                    this view to display the correct data, this method can create a new view.
         *                    Heterogeneous lists can specify their number of view types, so that this View is
         *                    always of the right type (see {@link #getViewTypeCount()} and
         *                    {@link #getItemViewType(int)}).
         * @param parent      The parent that this view will eventually be attached to
         * @return A View corresponding to the data at the specified position.
         */
        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_admin_settings, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv_admin_settings_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv.setText(items[position]);
            return convertView;
        }
    }

    private static class ViewHolder{
        protected TextView tv;
    }
}
