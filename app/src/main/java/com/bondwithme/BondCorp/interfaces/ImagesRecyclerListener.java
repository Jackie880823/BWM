package com.bondwithme.BondCorp.interfaces;

import com.bondwithme.BondCorp.adapter.HeadHolder;
import com.bondwithme.BondCorp.adapter.VideoHolder;
import com.bondwithme.BondCorp.entity.PushedPhotoEntity;

/**
 * Created 10/23/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface ImagesRecyclerListener {
    void loadHeadView(HeadHolder headHolder);

    void loadVideoView(VideoHolder videoHolder);

    void deletePhoto(PushedPhotoEntity photo);

    void loadFinish();
}
