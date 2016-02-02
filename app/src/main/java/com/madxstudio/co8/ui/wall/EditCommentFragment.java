package com.madxstudio.co8.ui.wall;

import android.view.View;
import android.widget.EditText;

import com.android.volley.ext.HttpCallback;
import com.android.volley.ext.RequestInfo;
import com.android.volley.ext.tools.HttpTools;
import com.madxstudio.co8.Constant;
import com.madxstudio.co8.R;
import com.madxstudio.co8.entity.WallCommentEntity;
import com.madxstudio.co8.http.UrlUtil;
import com.madxstudio.co8.ui.BaseFragment;
import com.madxstudio.co8.util.LogUtil;
import com.madxstudio.co8.util.MyDateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created 15/12/31.
 *
 * @author Jackie
 * @version 1.0
 */
public class EditCommentFragment extends BaseFragment<EditCommentActivity> {
    private static final String TAG = "EditCommentFragment";
    private EditCommentFragmentListener listener;
    private EditText etComment;
    private View progress;

    public static EditCommentFragment newInstance(String... params) {
        return createInstance(new EditCommentFragment(), params);
    }

    public EditCommentFragment() {
        super();
    }

    @Override
    public void setLayoutId() {
        this.layoutId = R.layout.fragment_edit_comment;
    }

    @Override
    protected void setParentTitle() {

    }

    private WallCommentEntity entity;

    @Override
    public void initView() {
        etComment = getViewById(R.id.et_comment);
        progress = getViewById(R.id.rl_progress);
        if (listener != null) {
            entity = listener.getEntity();
            if (entity != null) {
                etComment.setText(entity.getComment_content());
            }
        }
    }

    /**
     * 上传修改好的评论
     */
    public void putComment() {
        String newDesc = etComment.getText().toString();
        if (!newDesc.equals(entity.getText_description())) {
            entity.setComment_content(newDesc);
            Map<String, String> param = new HashMap<>();
            param.put("comment_owner_id", entity.getComment_owner_id());  // comment creator ID 评论者的ID (当前用户ID)
            param.put("comment_content", newDesc);
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.url = String.format(Constant.API_PUT_COMMENT, entity.getComment_id());
            requestInfo.jsonParam = UrlUtil.mapToJsonstring(param);
            new HttpTools(getParentActivity()).put(requestInfo, Constant.API_PUT_COMMENT, new HttpCallback() {
                @Override
                public void onStart() {
                    progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onResult(String string) {
                    LogUtil.d(TAG, "onResult& response: " + string);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = format.format(new Date(MyDateUtils.formatTimestamp2UTC(System.currentTimeMillis())));
                    entity.setComment_creation_date(date);
                    entity.setComment_edited("1");
                    listener.putEntity(entity);
                }

                @Override
                public void onError(Exception e) {

                }

                @Override
                public void onCancelled() {

                }

                @Override
                public void onLoading(long count, long current) {

                }
            });
        }
    }

    @Override
    public void requestData() {

    }

    public void setListener(EditCommentFragmentListener listener) {
        this.listener = listener;
    }

    public interface EditCommentFragmentListener {

        /**
         * @return 获取评论实例
         */
        WallCommentEntity getEntity();

        void putEntity(WallCommentEntity wallCommentEntity);
    }
}
