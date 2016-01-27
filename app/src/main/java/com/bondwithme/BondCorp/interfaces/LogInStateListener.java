package com.bondwithme.BondCorp.interfaces;

import com.bondwithme.BondCorp.entity.FaceBookUserEntity;

/**
 * Created by christepherzhang on 15/7/16.
 */
public interface LogInStateListener {
        public  void OnLoginSuccess(FaceBookUserEntity faceBookUserEntity,String logType);
        public  void OnLoginError(String error);
}
