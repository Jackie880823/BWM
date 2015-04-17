package com.madx.bwm;

/**
 * Created by wing on 15/1/29.
 */
public class Constant {

    /**
     * for app
     */
    public static final String PREFERRENCE_NAME = "bwm_preferences";

    public static final int ACTION_CREATE_WALL = 10;
    public static final int ACTION_UPDATE_WALL = 11;
    public static final int ACTION_COMMENT_WALL = 12;

    public static final int ACTION_EVENT_CREATE = 20;
    public static final int ACTION_EVENT_UPDATE = 21;
    public static final int ACTION_EVENT_UPDATE_BIRTHDAY = 22;
    public static final int ACTION_EVENT_UPDATE_MEMBERS = 23;


    public static String GET_MULTI_ORIGINALPHOTO = "API_SERVER/bondwithme/index.php/api/multiPhoto";
    /**
     * for api
     */
    public static String API_SERVER = "http://pt.bondwith.me";
//    public static String API_SERVER = "API_SERVER";

    public static String API_WALL_MAIN = "API_SERVER/bondwithme/index.php/api/wall";
    public static String API_WALL_DETAIL = "API_SERVER/bondwithme/index.php/api/wall";
    public static String API_WALL_LOVE = "API_SERVER/bondwithme/index.php/api/love";
    public static String API_WALL_TEXT_POST = "API_SERVER/bondwithme/index.php/api/posts";
    public static String API_WALL_PIC_POST = "API_SERVER/bondwithme/index.php/api/uploadPhoto";
    public static String API_WALL_COMMENT_TEXT_POST = "API_SERVER/bondwithme/index.php/api/posts";
    public static String API_WALL_COMMENT_LIST = "API_SERVER/bondwithme/index.php/api/comment";
    public static String API_WALL_COMMENT_LOVE = "API_SERVER/bondwithme/index.php/api/love_comment";
    public static String API_WALL_COMMENT_DELETE = "API_SERVER/bondwithme/index.php/api/comment/%s";
    public static String API_WALL_DELETE = "API_SERVER/bondwithme/index.php/api/removeContent/%s";



    //event
    public static String API_EVENT_MAIN = "API_SERVER/bondwithme/index.php/api/event";
    public static String API_EVENT_COMMENT = "API_SERVER/bondwithme/index.php/api/comment";
    public static String API_EVENT_CREATE = "API_SERVER/bondwithme/index.php/api/event";
    public static String API_EVENT_UPDATE = "API_SERVER/bondwithme/index.php/api/event/";
    public static String API_EVENT_POST_COMMENT = "API_SERVER/bondwithme/index.php/api/posts";
    public static String API_GET_EVENT_DETAIL = "API_SERVER/bondwithme/index.php/api/event";


    public static String API_EVENT_INVITED = "API_SERVER/bondwithme/index.php/api/eventMember";
    public static String API_EVENT_COMMENT_LOVE = "API_SERVER/bondwithme/index.php/api/love_comment";
    public static String API_EVENT_CANCEL = "API_SERVER/bondwithme/index.php/api/cancelEvent/%s";
    public static String API_EVENT_REMOVE_MEMBER = "API_SERVER/bondwithme/index.php/api/removeMember/%s";//删除成员
    public static String API_EVENT_ADD_MEMBERS = "API_SERVER/bondwithme/index.php/api/gFamilyMember";//添加成员

    public static String API_EVENT_INTENT = "API_SERVER/bondwithme/index.php/api/eventResponse/%s";
    public static String API_EVENT_COMMENT_DELETE = "API_SERVER/bondwithme/index.php/api/comment/%s";
    public static String API_EVENT_RESPONSE_INFOS = "API_SERVER/bondwithme/index.php/api/totalEventResp/%s";


    /*wall comments*/

    /*获取图片,三个参数1.module,2.user_id,3.fileId*/
    public static String API_GET_PIC = "API_SERVER/bondwithme/index.php/api/%s/%s/fid/%s"+"/"+System.currentTimeMillis();
    public static String API_GET_PHOTO = "API_SERVER/bondwithme/index.php/api/%s/%s/fid/profile";

    /**for pic*/
    public static String Module_Original = "photo_original";//original uploaded size
    public static String Module_preview_xl = "post_preview_xl";//800*800
    public static String Module_preview = "post_preview";//400*400
    public static String Module_preview_m = "post_preview_m";//200*200
    /**for head*/
    public static String Module_profile = "photo_profile";//200*200
    public static String Module_profile_s = "photo_profile_s";//100*100
    /*google map api*/
    /*params:1.center(x,y),2.pic size,3.mark location(x,y)*/
    public static String MAP_API_GET_LOCATION_PIC = "http://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=14&maptype=roadmap&size=%s&markers=color:blue|label:S|%s&sensor=false";

    /** more */
    public static String API_CONTACT_US = "API_SERVER/bondwithme/index.php/api/contactUs";
    public static String API_SETTING_CONFIG = "API_SERVER/bondwithme/index.php/api/setting/%s";
    public static String API_SHARE2FRIEND = "API_SERVER/bondwithme/index.php/api/shareToFriend";
    public static String API_BONDALERT_LIST = "API_SERVER/bondwithme/index.php/api/bondAlertList";
    public static String API_BONDALERT_NEWS = "API_SERVER/bondwithme/index.php/api/bondAlertList/%s/module/other";
    public static String API_BONDALERT_EVENT = "API_SERVER/bondwithme/index.php/api/bondAlertList/%S/module/event";
    public static String API_BONDALERT_RECOMMEND = "API_SERVER/bondwithme/index.php/api/recommendMember/%s";
    public static String API_BONDALERT_WALL = "API_SERVER/bondwithme/index.php/api/bondAlertList/%s/module/wall";
    public static String API_BONDALERT_DIG_DAY = "API_SERVER/bondwithme/index.php/api/bondAlertList/%s/module/bigDay";
    public static String API_BONDALERT_MEMEBER = "API_SERVER/bondwithme/index.php/api/memberAlertList/%s/module/member";
    public static String API_BONDALERT_MEMEBER_REMOVE = "API_SERVER/bondwithme/index.php/api/cancelRequest/";
    public static String API_BONDALERT_MEMEBER_RESEND = "API_SERVER/bondwithme/index.php/api/addViaUserID";
    public static String API_BONDALERT_ALL_COUNT = "API_SERVER/bondwithme/index.php/api/getNTotalBondAlert/%s";
    public static String API_BONDALERT_MODULES_COUNT = "API_SERVER/bondwithme/index.php/api/getTotalAlertByModule/%s";
    public static String API_BONDALERT_NEWS_ITEM = "API_SERVER/bondwithme/index.php/api/getOtherBondAlert/%s";//获取alert news详情



    /*christopher*/
    public static String API_MESSAGE_MAIN = "API_SERVER/bondwithme/index.php/api/messages/%s";//主界面

    public static String API_GET_MESSAGE = "API_SERVER/bondwithme/index.php/api/posts";//获得群组消息也是获得个人消息

    public static String API_GET_EVERYONE = "API_SERVER/bondwithme/index.php/api/everyone/%s";//获得好友列表(My Family界面的)

    public static String API_LOGIN = "API_SERVER/bondwithme/index.php/api/login";//登录

    public static String API_VERIFICATION = "API_SERVER/bondwithme/index.php/api/createVerification";//获得验证码-注册

    public static String API_GET_GROUP_PHOTO = "API_SERVER/bondwithme/index.php/api/photo_profile/%s/fid/group_profile";//群组图片

    public static String API_LOGINID_AVAILABILITY = "API_SERVER/bondwithme/index.php/api/loginID";//检查账号合法性

    public static String API_VERIFY_CODE = "API_SERVER/bondwithme/index.php/api/verification";//检查验证码是否正确

    public static String API_CREATE_NEW_USER = "API_SERVER/bondwithme/index.php/api/user";//创建账号上传个人信息

    public static String API_VERIFY_CODE_FOR_FORGETPASSWORD = "API_SERVER/bondwithme/index.php/api/forgetPassword";//获得验证码-忘记密码

    public static String API_UPDATE_PASSWORD = "API_SERVER/bondwithme/index.php/api/updatePassword/";//修改密码

    public static String API_UPLOAD_PROFILE_PICTURE = "API_SERVER/bondwithme/index.php/api/userProfilePic";//上传个人头像

    public static String API_MEMBER_PROFILE_DETAIL = "API_SERVER/bondwithme/index.php/api/memberDetail";//好友详细资料

    public static String API_MESSAGE_POST_TEXT = "API_SERVER/bondwithme/index.php/api/posts";//聊天->文本内容

    public static String API_MESSAGE_POST_PNG = "API_SERVER/bondwithme/index.php/api/posts";//聊天->照片

    public static String API_GROUP_MEMBERS = "API_SERVER/bondwithme/index.php/api/groupMember";//群组成员列表

    public static String API_GROUP_REMOVE_MEMBERS = "API_SERVER/bondwithme/index.php/api/removeMember/%s";//删除成员

    public static String API_GROUP_ADD_MEMBERS = "API_SERVER/bondwithme/index.php/api/gFamilyMember";//增加成员

    public static String API_CREATE_GROUP = "API_SERVER/bondwithme/index.php/api/gFamilyMember";//创建群组

    public static String API_UPLOAD_GROUP_PHOTO = "API_SERVER/bondwithme/index.php/api/groupProfilePic";//上传新的群组头像

    public static String API_UPDATE_GROUP_NAME = "API_SERVER/bondwithme/index.php/api/group/%s";//上传新的群组名

    public static String API_UPDATE_MY_PROFILE = "API_SERVER/bondwithme/index.php/api/user/%s";//修改个人资料

    public static String API_SEARCH_MEMBER = "API_SERVER/bondwithme/index.php/api/searchMember";//ID or account搜索

    public static String API_ADD_MEMBER = "API_SERVER/bondwithme/index.php/api/addViaUserID";//添加好友

    public static String API_ADD_MEMBER_THROUGH_CONTACT = "API_SERVER/bondwithme/index.php/api/addMemberViaContact";//添加好友从联系人那边

    public static String API_POST_STICKER = "API_SERVER/bondwithme/index.php/api/posts";//上传sticker

    public static String API_MISS_MEMBER = "API_SERVER/bondwithme/index.php/api/miss";//想念用户

    public static String API_FAMILY_TREE = "API_SERVER/bondwithme/index.php/api/familyTreeFile/%s";//获得family tree url

    public static String API_ALBUM_GALLERY = "API_SERVER/bondwithme/index.php/api/userPhotos";//album gallery

    public static String API_PATH_RELATIONSHIP = "API_SERVER/bondwithme/index.php/api/familyLink";//album gallery

    public static String API_LEAVE_GROUP = "API_SERVER/bondwithme/index.php/api/group/%s";//leave group

    public static String API_UPDATE_RELATIONSHIP_NICKNAME = "API_SERVER/bondwithme/index.php/api/updateRelationship/%s";//修改关系和nickname

    public static String API_ADD_SEARCH_MEMBER = "API_SERVER/bondwithme/index.php/api/addViaSearch";//新的添加添加好友

    public static String API_GET_MEMBER_TYPE = "API_SERVER/bondwithme/index.php/api/memberAction/%s/member/%s";//获得add member flow type

    public static String API_SET_RELATIONSHIP = "API_SERVER/bondwithme/index.php/api/setRelationship";//获得add member flow type, 没有relationship的时候上传关系

    public static String API_UPDATE_MISS = "API_SERVER/bondwithme/index.php/api/miss/%s";//消除好友的miss

    //获取sticker, 4个参数. 1.user_id, 2.sticker_name, 3.sticker_path, 4.sticker_type
    public static String API_STICKER = "API_SERVER/bondwithme/index.php/api/sticker_l/%s/fid/%s_B/fpath/%s/ftype/%s";
    public static String Sticker_Png = ".png";
    public static String Sticker_Gif = ".gif";


}
