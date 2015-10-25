package com.bondwithme.BondWithMe.interfaces;

import com.bondwithme.BondWithMe.adapter.VideoHolder;
import com.bondwithme.BondWithMe.entity.PushedPhotoEntity;
import com.bondwithme.BondWithMe.ui.wall.HeadHolder;

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
