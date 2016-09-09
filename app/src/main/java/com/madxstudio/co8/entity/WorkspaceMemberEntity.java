/*
 *
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 *             $                                                   $
 *             $                       _oo0oo_                     $
 *             $                      o8888888o                    $
 *             $                      88" . "88                    $
 *             $                      (| -_- |)                    $
 *             $                      0\  =  /0                    $
 *             $                    ___/`-_-'\___                  $
 *             $                  .' \\|     |$ '.                 $
 *             $                 / \\|||  :  |||$ \                $
 *             $                / _||||| -:- |||||- \              $
 *             $               |   | \\\  -  $/ |   |              $
 *             $               | \_|  ''\- -/''  |_/ |             $
 *             $               \  .-\__  '-'  ___/-. /             $
 *             $             ___'. .'  /-_._-\  `. .'___           $
 *             $          ."" '<  `.___\_<|>_/___.' >' "".         $
 *             $         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       $
 *             $         \  \ `_.   \_ __\ /__ _/   .-` /  /       $
 *             $     =====`-.____`.___ \_____/___.-`___.-'=====    $
 *             $                       `=-_-='                     $
 *             $     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   $
 *             $                                                   $
 *             $          Buddha Bless         Never Bug           $
 *             $                                                   $
 *             $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 */

package com.madxstudio.co8.entity;

import com.madxstudio.co8.ui.workspace.detail.PostDetailActivity;

import java.io.Serializable;

/**
 * {@link PostDetailActivity} 中Member列表详情
 * Created 16/9/9.
 *
 * @author Jackie
 * @version 1.0
 */
public class WorkspaceMemberEntity implements Serializable{

    /**
     * user_id : 1
     * user_given_name : jennco8
     * author : 1
     * is_creator : 1
     * added_flag : 1
     * group_id : null
     */
    private String user_id;
    private String user_given_name;
    private String author;
    private String is_creator;
    private String added_flag;
    private Object group_id;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_given_name() {
        return user_given_name;
    }

    public void setUser_given_name(String user_given_name) {
        this.user_given_name = user_given_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIs_creator() {
        return is_creator;
    }

    public void setIs_creator(String is_creator) {
        this.is_creator = is_creator;
    }

    public String getAdded_flag() {
        return added_flag;
    }

    public void setAdded_flag(String added_flag) {
        this.added_flag = added_flag;
    }

    public Object getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Object group_id) {
        this.group_id = group_id;
    }

    @Override
    public String toString() {
        return "WorkspaceMemberEntity{" +
                "user_id='" + user_id + '\'' +
                ", user_given_name='" + user_given_name + '\'' +
                ", author='" + author + '\'' +
                ", is_creator='" + is_creator + '\'' +
                ", added_flag='" + added_flag + '\'' +
                ", group_id=" + group_id +
                '}';
    }
}
