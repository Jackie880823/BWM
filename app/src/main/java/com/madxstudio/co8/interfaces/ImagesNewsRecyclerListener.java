package com.madxstudio.co8.interfaces;

import com.madxstudio.co8.adapter.VideoHolder;
import com.madxstudio.co8.adapter.WriteNewHeadHolder;
import com.madxstudio.co8.entity.PushedPhotoEntity;

/**
 * Created 10/23/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface ImagesNewsRecyclerListener {
    void loadHeadView(WriteNewHeadHolder headHolder);

    void loadVideoView(VideoHolder videoHolder);

    void deletePhoto(PushedPhotoEntity photo);

    void loadFinish();
}
