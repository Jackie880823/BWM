package com.bondwithme.BondWithMe.interfaces;

import com.bondwithme.BondWithMe.ui.wall.HeadHolder;
import com.bondwithme.BondWithMe.ui.wall.VideoHolder;

/**
 * Created 10/23/15.
 *
 * @author Jackie
 * @version 1.0
 */
public interface ImagesRecyclerListener {
    void loadHeadView(HeadHolder headHolder);
    void loadVideoView(VideoHolder videoHolder);
    void deletePhoto(int position);
    void loadFinish();
}
