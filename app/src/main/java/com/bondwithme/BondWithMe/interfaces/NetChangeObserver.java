package com.bondwithme.BondWithMe.interfaces;

/**
 * Created by wing on 15/5/18.
 */
/**
 * 监听网络状态改变的观察者
 */
public interface NetChangeObserver {

    /**
     * 网络状态连接时调用
     */
    public void OnConnect(int netType);

    /**
     * 网络状态断开时调用
     */
    public void OnDisConnect();


}
