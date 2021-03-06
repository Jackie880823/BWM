package com.madxstudio.co8;

import com.madxstudio.co8.util.FileUtil;

/**
 * Created by wing on 15/1/29.
 */
public class Constant {

    /**
     * JPEG格式的常量，保存扩展名为jpeg的图片时使用这个常量来拼接文件
     */
    public static final String EXTENSION_JPEG = ".jpeg";

    /**
     * JPEG格式的常量，保存扩展名为jpeg的图片时使用这个常量来拼接文件
     */
    public static final String EXTENSION_JPG = ".jpg";

    /**
     * PNG格式的常量，保存扩展名为png的图片时使用这个常量来拼接文件
     */
    public static final String EXTENSION_PNG = ".png";

    /**
     * GIF格式的常量，保存扩展名为gif的图片时使用这个常量来拼接文件
     */
    public static final String EXTENSION_GIF = ".gif";

    /**
     * http协议头，在{@link android.net.Uri}中可以用这个来判断是不是从网络来的路径
     */
    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    /**
     * 网络请求返回代码信息
     */
    public static final String RESPONSE_STATUS_CODE = "response_status_code";
    /**
     * 网络请求返回状态信息
     */
    public static final String RESPONSE_STATUS = "response_status";
    /**
     * 状态成功
     */
    public static final String STATUS_SUCCESS = "Success";

    /**
     * mail
     */
    public final static String REPORT_EMAIL_FROM_ADDRESS = "it.dude@bondwith.me";
    public final static String REPORT_EMAIL_USERNAME = "AKIAJGKWD34V5H7NB2KQ";
    public final static String REPORT_EMAIL_PASSWORD = "Ao9FeNkRizW7yDpmKzUxuzWLcWtUsm5LJ35HXag5+7WI";

    public static final boolean REPORT_EMAIL_VALIDATE = false;
    public static final String REPORT_EMAIL_SERVER_PORT = "25";
    public static final String REPORT_EMAIL_SERVER_HOST = "email-smtp.us-east-1.amazonaws.com";
    public static final String REPORT_EMAIL_TO_ADDRESS = "uncle.bug@bondwith.me";//

    /**
     * for app
     */
    public static final String PREFERRENCE_NAME = "bwm_preferences";
    public static final String GCM_PREF_REG_ID = "gcm_registration_id";
    public static final String GCM_PREF_APP_VERSION = "gcm_appVersion";
    public static final String JPUSH_PREF_REG_ID = "jpush_registration_id";
    public static final String JPUSH_PREF_APP_VERSION = "jpush_appVersion";
    public static final String HTTP_TOKEN = "token";
    public static final String LOGIN_USER = "user";
    public static final String APP_CRASH = "app_crash";

    /**
     * 更新图片的意图请求标识
     */
    public static final int INTENT_REQUEST_UPDATE_PHOTOS = 9;
    /**
     * 创建日记的意图标识
     */
    public static final int INTENT_REQUEST_CREATE_WALL = 10;
    public static final int INTENT_REQUEST_UPDATE_WALL = 11;
    public static final int INTENT_UPDATE_DIARY = 12;
    public static final int ACTION_COMMENT_MEMBERS = 13;
    public static final int ACTION_COMMENT_GROUPS = 14;

    /**
     * WorkSpace跳入详情请求
     */
    public static final int ENTITY_REQUEST_CODE = 100;

    public static final String WALL_ENTITY = "wall_entity";
    public static final String IS_DELETE = "is_delete";
    /**
     * 打开相册的意图请求标识
     */
    public static final int INTENT_REQUEST_HEAD_PHOTO = 15;
    /**
     * 打开相机的意图标识
     */
    public static final int INTENT_REQUEST_HEAD_CAMERA = 16;
    /**
     *
     */
    public static final int INTENT_REQUEST_GET_LOCATION = 17;
    public static final int INTENT_REQUEST_GET_MEMBERS = 18;
    /**
     * 打开GPS请求
     */
    public static final int INTENT_REQUEST_OPEN_GPS = 19;
    /**
     * 请求获取多张图片的意图请求
     */
    public static final int INTENT_REQUEST_HEAD_MULTI_PHOTO = 20;
    public static final int INTENT_REQUEST_FEELING = 21;

    /**
     * 请求编辑评论的意图
     */
    public static final int EDIT_COMMENT_REQUEST_CODE = 1000;

    public static final int ACTION_EVENT_CREATE = 20;
    public static final int ACTION_EVENT_UPDATE = 21;
    public static final int ACTION_EVENT_UPDATE_BIRTHDAY = 22;
    public static final int ACTION_EVENT_UPDATE_MEMBERS = 23;

    public static final int ACTION_NEWS_CREATE = 24;
    public static final int INTENT_UPDATE_NEWS = 25;

    public static final int ACTION_MESSAGE_CREATE = 26;

    public final static int CREATE_NEW_ORG = 27;
    public final static int SEARCH_ORG_DATA = 28;

    public static final String ACTION_SHOW_NOTIFY_USER = "ACTION_SHOW_NOTIFY_USER";
    public static final String ACTION_SHOW_NOTIFY_GROUP = "ACTION_SHOW_NOTIFY_GROUP";
    public static final String ACTION_SHOW_LOVED_USER = "ACTION_SHOW_LOVED_USER";

    public static final int MESSAGE_CHART_TYPE_MEMBER = 0;
    public static final int MESSAGE_CHART_TYPE_GROUP = 1;
    public static final String MESSAGE_CHART_TYPE = "type";
    public static final String MESSAGE_CHART_GROUP_ID = "groupId";
    public static final String MESSAGE_CHART_STATUS = "status";
    public static final String MESSAGE_CHART_TITLE_NAME = "titleName";
    public static final String SELECT_MEMBER_NORMAL_DATA = "normalData";
    public static final String SELECT_MEMBER_DATA = "members_data";

    public static final String GROUP_DEFAULT = "group_default";

    /**
     * 与心情相关
     */
    public final static String PATH_PREFIX = "feeling";
    public final static String FEEL_ICON_NAME = PATH_PREFIX + "/%s";
    public static final String EXTRA_CHECK_ITEM_INDEX = "check_item_index";

    public static final String REQUEST_TYPE = "REQUEST_TYPE";
    public static final String ADMIN_REQUEST = "ADMIN_REQUEST";
    public static final String GENERAL_REQUEST = "GENERAL_REQUEST";
    public static final String REQUEST_ADD_ADMIN = "REQUEST_ADD_ADMIN";

    public static final String ORG_TRANSMIT_DATA = "ORG_TRANSMIT_DATA";
    public static final String ORG_TRANSMIT_GROUP = "ORG_TRANSMIT_GROUP";
    public static final String ORG_TRANSMIT_STAFF = "ORG_TRANSMIT_STAFF";
    public static final String ORG_TRANSMIT_OTHER = "ORG_TRANSMIT_OTHER";
    public static final String ORG_TRANSMIT_PENDING_REQUEST = "ORG_TRANSMIT_PENDING_REQUEST";
    public static final String FAMILY_PARENT = "Supervisor";
    public static final String FAMILY_CHILDREN = "Subordinate";
    public static final String FAMILY_SIBLING = "Colleague";
    public static final String RELATION_SHIP_SUPPLIER = "Supplier";
    public static final String RELATION_SHIP_CUSTOMER = "Customer";
    public static final String RELATION_SHIP = "Relationship";

    public static final String CREATE_COUNTRY_NAME = "CountryName";
    public static final String LOOK_USER_PROFILE = "OnlyLook";
    public static final String FROM_PENDING_REQUEST = "pendingRequest";

    public static final  int SHOW_PROFILE_ONE_BUT = 0;
    public static final  int SHOW_PROFILE_TWO_BUT = 1;
    public static final  int SHOW_PROFILE_NO_BUT = 2;
    /**
     * 临时文件用户裁剪
     */
    public final static String CACHE_PIC_NAME_TEMP = "head_cache_temp";

    /**
     * for api
     */
//    public static final String API_SERVER = "http://ptb2be.bondwith.me";
//    public static final int TRACKER_SITE_ID = 5;//dev
    public static final String API_SERVER = App.getContextInstance().getString(R.string.api_server);
    public static final int TRACKER_SITE_ID = Integer.valueOf(App.getContextInstance().getString(R.string.tracker_site_id));//dev

    public static final String API_CHECK_VERSION = API_SERVER + "/co8/index.php/api/appVersion";
    public static final String GET_MULTI_ORIGINALPHOTO = API_SERVER + "/co8/index.php/api/multiPhoto";

    public static final String API_WALL_MAIN = API_SERVER + "/co8/index.php/api/wall";
    public static final String API_WALL_DETAIL = API_SERVER + "/co8/index.php/api/wall";
    public static final String API_WALL_LOVE = API_SERVER + "/co8/index.php/api/love";
    public static final String API_WALL_TEXT_POST = API_SERVER + "/co8/index.php/api/posts";
    public static final String API_WALL_PIC_POST = API_SERVER + "/co8/index.php/api/uploadPhoto";
    public static final String API_WALL_COMMENT_TEXT_POST = API_SERVER + "/co8/index.php/api/posts";
    public static final String API_WALL_COMMENT_PIC_POST = API_SERVER + "/co8/index.php/api/uploadCommentPhoto";
    public static final String API_WALL_COMMENT_LIST = API_SERVER + "/co8/index.php/api/comment";
    public static final String API_WALL_COMMENT_LOVE = API_SERVER + "/co8/index.php/api/love_comment";
    public static final String API_WALL_COMMENT_DELETE = API_SERVER + "/co8/index.php/api/comment/%s";
    public static final String API_WALL_DELETE = API_SERVER + "/co8/index.php/api/removeContent/%s";
    public static final String API_WALL_GET_LOVE_MEMBER_LIST = API_SERVER + "/co8/index.php/api/loveMemberList";

    public static final String API_REGIST_PUSH = API_SERVER + "/co8/index.php/api/pushToken";
    public static final String UN_REGISTER_URL = API_SERVER + "/co8/index.php/api/auth/%s";


    //event
    public static final String API_EVENT_MAIN = API_SERVER + "/co8/index.php/api/event";
    public static final String API_EVENT_COMMENT = API_SERVER + "/co8/index.php/api/comment";
    public static final String API_EVENT_CREATE = API_SERVER + "/co8/index.php/api/event";
    public static final String API_EVENT_UPDATE = API_SERVER + "/co8/index.php/api/event/";
    public static final String API_EVENT_POST_COMMENT = API_SERVER + "/co8/index.php/api/posts";
    public static final String API_EVENT_COMMENT_PIC_POST = API_SERVER + "/co8/index.php/api/uploadCommentPhoto";
    public static final String API_GET_EVENT_DETAIL = API_SERVER + "/co8/index.php/api/event";
    public static final String API_GET_EVENT_GROUP_MEMBERS = API_SERVER + "/co8/index.php/api/filterGroupMember";


    public static final String API_EVENT_INVITED = API_SERVER + "/co8/index.php/api/eventMember";
    public static final String API_EVENT_COMMENT_LOVE = API_SERVER + "/co8/index.php/api/love_comment";
    public static final String API_EVENT_CANCEL = API_SERVER + "/co8/index.php/api/cancelEvent/%s";
    public static final String API_EVENT_REMOVE_MEMBER = API_SERVER + "/co8/index.php/api/removeMember/%s";//删除成员
    public static final String API_EVENT_ADD_MEMBERS = API_SERVER + "/co8/index.php/api/gFamilyMember";//添加成员

    public static final String API_EVENT_INTENT = API_SERVER + "/co8/index.php/api/eventResponse/%s";
    public static final String API_EVENT_COMMENT_DELETE = API_SERVER + "/co8/index.php/api/comment/%s";
    public static final String API_EVENT_RESPONSE_INFOS = API_SERVER + "/co8/index.php/api/totalEventResp/%s";


    /*wall comments*/

    /*获取图片,三个参数1.module,2.user_id,3.fileId*/
    public static final String API_GET_PIC = API_SERVER + "/co8/index.php/api/%s/%s/fid/%s" + "/" + System.currentTimeMillis();
    public static final String API_GET_COMMENT_PIC = API_SERVER + "/co8/index.php/api/%s/%s/fid/%s";
    public static final String API_GET_PHOTO = API_SERVER + "/co8/index.php/api/%s/%s/fid/profile";
    public static final String API_GET_WORKSPACE_BACKGROUND_PHOTO = API_SERVER +
            "/co8/index.php/api/photo_profile/%s/fid/content?%s";

    /**
     * 获取视频的链接，两个参数：1.content_creator_id; 2.视频文件名称`
     */
    public static final String API_GET_VIDEO = API_SERVER + "/co8/index.php/api/video_preview/%s/fid/%s";

    /**
     * 获取视频小图的链接，两个参数：1.content_creator_id; 2.视频文件名称
     */
    public static final String API_GET_VIDEO_THUMBNAIL = API_SERVER + "/co8/index.php/api/video_thumbnail/%s/fid/%s";


    /**
     * for pic
     */
    public static final String Module_Original = "photo_original";//original uploaded size
    public static final String MODULE_CONTENT_COVER = "content_cover";//original uploaded size
    public static final String Module_preview_xl = "post_preview_xl";//800*800
    public static final String Module_preview = "post_preview";//400*400
    public static final String Module_preview_m = "post_preview_m";//200*200
    /**
     * for head
     */
    public static final String Module_profile = "photo_profile";//200*200
    public static final String Module_profile_s = "photo_profile_s";//100*100
    /*google map api*/
    /*params:1.center(x,y),2.pic size,3.mark location(x,y)*/
    public static final String MAP_API_GET_LOCATION_PIC_BY_GOOGLE = "http://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=14&maptype=roadmap&size=%s&markers=color:blue|label:T|%s&sensor=false";
    public static final String MAP_API_GET_LOCATION_PIC_BY_BAIDU = "http://api.map.baidu.com/staticimage?center=%s&zoom=14&size=%s&markers=%s&markerStyles=m,T";

    /**
     * more
     */
    public static final String API_CONTACT_US = API_SERVER + "/co8/index.php/api/contactUs";
    public static final String API_SETTING_CONFIG = API_SERVER + "/co8/index.php/api/setting/%s";
    public static final String API_SHARE2FRIEND = API_SERVER + "/co8/index.php/api/shareToFriend";
    public static final String API_NEWS = API_SERVER + "/co8/index.php/api/newsList/%s";
    //    public static final String API_BONDALERT_LIST = API_SERVER+"/co8/index.php/api/bondAlertList";
    public static final String API_BONDALERT_LIST = API_SERVER + "/co8/index.php/api/bondAlertList/%S/module/miss";
    public static final String API_BONDALERT_NEWS = API_SERVER + "/co8/index.php/api/bondAlertList/%s/module/other";
    public static final String API_BONDALERT_EVENT = API_SERVER + "/co8/index.php/api/bondAlertList/%S/module/event";
    public static final String API_BONDALERT_RECOMMEND = API_SERVER + "/co8/index.php/api/recommendMember/%s";
    public static final String API_BONDALERT_WALL = API_SERVER + "/co8/index.php/api/bondAlertList/%s/module/wall";
    public static final String API_BONDALERT_DIG_DAY = API_SERVER + "/co8/index.php/api/bondAlertList/%s/module/bigDay";
    public static final String API_BONDALERT_MEMEBER = API_SERVER + "/co8/index.php/api/memberAlertList/%s/module/member";
    public static final String API_BONDALERT_MEMEBER_REMOVE = API_SERVER + "/co8/index.php/api/cancelRequest/";
    public static final String API_BONDALERT_MEMEBER_RESEND = API_SERVER + "/co8/index.php/api/addViaUserID";
    public static final String API_BONDALERT_ALL_COUNT = API_SERVER + "/co8/index.php/api/getNTotalBondAlert/%s";
    public static final String API_BONDALERT_MODULES_COUNT = API_SERVER + "/co8/index.php/api/getTotalAlertByModule/%s";
    public static final String API_BONDALERT_NEWS_ITEM = API_SERVER + "/co8/index.php/api/getOtherBondAlert/%s";//获取alert news详情
    public static final String API_BONDALERT_GROUP = API_SERVER + "/co8/index.php/api/bondAlertList/%s/module/group";
    public static final String API_BONDALERT_GROUP_CONFIRM = API_SERVER + "/co8/index.php/api/confirmJoinGroup/%s";
    public static final String API_BONDALERT_GROUP_REJECT = API_SERVER + "/co8/index.php/api/rejectJoinGroup/%s";
    public static final String API_BONDALERT_REMOVE_RECOMMEND = API_SERVER + "/co8/index.php/api/removeRecomUser/";
    public static final String API_REWARDS = API_SERVER + "/co8/index.php/api/rewardList/%s";

    /**
     * Api for sticker store
     */
//    public static final String API_STICKERSTORE_FIRST_STICKER = API_SERVER + "/co8/index.php/api/sticker_l/%s/fid/%s/fpath/%s/ftype/%s";
    public static final String API_STICKER_GROUP = API_SERVER + "/co8/index.php/api/stickerGroupList";
    public static final String API_STICKER_ITEM = API_SERVER + "/co8/index.php/api/stickerItemList";
    public static final String API_STICKER_BANNER = API_SERVER + "/co8/index.php/api/stickerBanner";
    public static final String API_STICKER_BANNER_PIC = API_SERVER + "/co8/images/%s";
    //    public static final String API_STICKER_ZIP = API_SERVER + "/co8/index.php/api/sticker_zip/%s/fpath/%s";
    public static final String API_STICKER_UPDATE = API_SERVER + "/co8/index.php/api/stickerCheckUpdate";


    /*christopher*/
    public static final String API_MESSAGE_MAIN = API_SERVER + "/co8/index.php/api/messages/%s";//主界面

    //public static final String API_GET_MESSAGE = API_SERVER+"/co8/index.php/api/posts";//获得群组消息也是获得个人消息

    public static final String API_GET_EVERYONE = API_SERVER + "/co8/index.php/api/everyone/%s";//获得好友列表(My Family界面的)

    public static final String API_LOGIN = API_SERVER + "/co8/index.php/api/login";//登录

    public static final String API_VERIFICATION = API_SERVER + "/co8/index.php/api/createVerification";//获得验证码-注册

    public static final String API_GET_GROUP_PHOTO = API_SERVER + "/co8/index.php/api/photo_profile/%s/fid/group_profile";//群组图片

    public static final String API_LOGINID_AVAILABILITY = API_SERVER + "/co8/index.php/api/loginID";//检查账号合法性

    public static final String API_VERIFY_CODE = API_SERVER + "/co8/index.php/api/verification";//检查验证码是否正确

    public static final String API_CREATE_NEW_USER = API_SERVER + "/co8/index.php/api/user";//创建账号上传个人信息

    public static final String API_VERIFY_CODE_FOR_FORGETPASSWORD = API_SERVER + "/co8/index.php/api/forgetPassword";//获得验证码-忘记密码

    public static final String API_UPDATE_PASSWORD = API_SERVER + "/co8/index.php/api/updatePassword/";//修改密码

    public static final String API_UPLOAD_PROFILE_PICTURE = API_SERVER + "/co8/index.php/api/userProfilePic";//上传个人头像

    public static final String API_MEMBER_PROFILE_DETAIL = API_SERVER + "/co8/index.php/api/memberDetail";//好友详细资料

    public static final String API_MESSAGE_POST_TEXT = API_SERVER + "/co8/index.php/api/posts";//聊天->文本内容

    public static final String API_COMMENT_POST_TEXT = API_SERVER + "/co8/index.php/api/uploadCommentPhoto";//评论发送图片

    //public static final String API_MESSAGE_POST_PNG = API_SERVER+"/co8/index.php/api/posts";//聊天->照片

    public static final String API_GROUP_MEMBERS = API_SERVER + "/co8/index.php/api/groupMember";//群组成员列表

    public static final String API_GROUP_REMOVE_MEMBERS = API_SERVER + "/co8/index.php/api/removeMember/%s";//删除成员

    public static final String API_GROUP_ADD_MEMBERS = API_SERVER + "/co8/index.php/api/gFamilyMember";//增加成员

    public static final String API_CREATE_GROUP = API_SERVER + "/co8/index.php/api/gFamilyMember";//创建群组

    public static final String API_UPLOAD_GROUP_PHOTO = API_SERVER + "/co8/index.php/api/groupProfilePic";//上传新的群组头像

    public static final String API_UPDATE_GROUP_NAME = API_SERVER + "/co8/index.php/api/group/%s";//上传新的群组名

    public static final String API_UPDATE_MY_PROFILE = API_SERVER + "/co8/index.php/api/user/%s";//修改个人资料

    public static final String API_SEARCH_MEMBER = API_SERVER + "/co8/index.php/api/searchMember";//ID or account搜索

    public static final String API_ADD_MEMBER = API_SERVER + "/co8/index.php/api/addViaUserID";//添加好友

    public static final String API_ADD_MEMBER_THROUGH_CONTACT = API_SERVER + "/co8/index.php/api/addMemberViaContact";//添加好友从联系人那边

    //public static final String API_POST_STICKER = API_SERVER+"/co8/index.php/api/posts";//上传sticker

    public static final String API_MISS_MEMBER = API_SERVER + "/co8/index.php/api/miss";//想念用户

    public static final String API_FAMILY_TREE = API_SERVER + "/co8/index.php/api/familyTreeFile/%s";//获得family tree url
    /**
     * 获得家庭关系的URL,
     */
    public static final String API_FAMILY_RELATIONSHIP = API_SERVER + "/co8/index.php/api/listDirectMemberWithColleage/%s/member_id/%s";

    public static final String API_ALBUM_GALLERY = API_SERVER + "/co8/index.php/api/userPhotos";//album gallery

    public static final String API_PATH_RELATIONSHIP = API_SERVER + "/co8/index.php/api/familyLink";//album gallery

    public static final String API_LEAVE_GROUP = API_SERVER + "/co8/index.php/api/group/%s";//leave group

    public static final String API_UPDATE_RELATIONSHIP_NICKNAME = API_SERVER + "/co8/index.php/api/updateRelationship/%s";//修改关系和nickname

    public static final String API_ADD_SEARCH_MEMBER = API_SERVER + "/co8/index.php/api/addViaSearch";//新的添加添加好友

    public static final String API_GET_MEMBER_TYPE = API_SERVER + "/co8/index.php/api/memberAction/%s/member/%s";//获得add member flow type

    public static final String API_SET_RELATIONSHIP = API_SERVER + "/co8/index.php/api/setRelationship";//获得add member flow type, 没有relationship的时候上传关系

    public static final String API_UPDATE_MISS = API_SERVER + "/co8/index.php/api/miss/%s";//消除好友的miss

    /**
     * 编辑日记
     */
    public static final String API_PUT_WALL = API_SERVER + "/co8/index.php/api/updateContent/%s";
    /**
     * 编辑评论
     */
    public static final String API_PUT_COMMENT = API_SERVER + "/co8/index.php/api/updateComment/%s";
    /**
     * 照片最大号，用于在日志列表直接上传照片的时候，上传全部照片前先更新。
     */
    public static final String API_PUT_PHOTO_MAX = API_SERVER + "/co8/index.php/api/updatePhotoMax/%s";
    public static final String API_UPLOAD_VIDEO = API_SERVER + "/co8/index.php/api/uploadVideo";

    //获取sticker, 4个参数. 1.user_id, 2.sticker_name, 3.sticker_path, 4.sticker_type
    public static final String API_STICKER = API_SERVER + "/co8/index.php/api/sticker_l/%s/fid/%s_B/fpath/%s/ftype/%s";
    public static final String Sticker_Png = ".png";
    public static final String Sticker_Gif = ".gif";

    public static final String API_GET_CHAT_MESSAGE_LIST = API_SERVER + "/co8/index.php/api/messages/%s/type/%s";//获取消息成员列表
    public static final String API_GET_YEAR_ALBUM_LIST = API_SERVER + "/co8/index.php/api/userAlbum";//获取my album一年的所有列表
    public static final String API_GET_MONTH_ALBUM_LIST = API_SERVER + "/co8/index.php/api/userAlbumByMonth";//获取my album哪个月的所有列表

    // Intent传递数据时的鍵
    /**
     * 地址名称
     */
    public static final String EXTRA_LOCATION_NAME = "location_name";
    /**
     * 地址纬度
     */
    public static final String EXTRA_LATITUDE = "latitude";
    /**
     * 地址经度
     */
    public static final String EXTRA_LONGITUDE = "longitude";

    /**
     * Intent传的实体键
     */
    public static final String EXTRA_ENTITY = "Extra_Entity";

    // 用于网络连拉的参数
    public static final String PARAM_USER_ID = "user_id";
    public static final String PARAM_CONTENT_CREATOR_ID = "content_creator_id";
    public static final String PARAM_CONTENT_TYPE = "content_type";
    public static final String PARAM_TEXT_DESCRIPTION = "text_description";
    public static final String PARAM_LOC_LATITUDE = "loc_latitude";
    public static final String PARAM_LOC_LONGITUDE = "loc_longitude";
    public static final String PARAM_LOC_NAME = "loc_name";
    public static final String PARAM_LOC_CAPTION = "loc_caption";
    public static final String PARAM_STICKER_GROUP_PATH = "sticker_group_path";
    public static final String PARAM_LOC_TYPE = "loc_type";
    public static final String PARAM_PHOTO_MAX = "photo_max";

    public static final String HAS_LOGED_IN = "has_loged_in";
    public static final String HAS_DOWNLOAD = "has_download";

    public static final String TYPE_PHONE = "phone";
    public static final String TYPE_USERNAME = "username";
    public static final String TYPE_FACEBOOK = "facebook";
    public static final String TYPE_FORGOT_PASSWORD = "forgot_password";

    public static final String USER_APP_OS = "android";

    public static final String SUCCESS = "Success";
    public static final String FAIL = "Fail";
    public static final String TYPE = "type";

    public static final String FILE_PATH_NAME = "CO8";

    public static final String IS_SIGN_UP = "signUp";
    public static final String IS_FROM_ORG = "fromOrg";

    public static final String IS_FIRST_CREATE_ORG = "createNewOrg";
    /**
     * about wall, user extra
     */
    public static final String CONTENT_GROUP_ID = "content_group_id";
    public static final String GROUP_ID = "group_id";
    public static final String CONTENT_ID = "content_id";
    public static final String USER_ID = "user_id";
    public static final String AGREE_COUNT = "agree_count";
    public static final String REQUEST_URL = "request_url";
    public static final String CONDITION = "condition";
    public static final String POSITION = "position";
    public static final String PHOTO_POSITION = "photo_position";


    public static final String LIMIT = "limit";
    public static final String START = "start";
    public static final String MEMBER_ID = "member_id";

    public static final String COMMENT_OWNER_ID = "comment_owner_id";
    public static final String CONTENT_TYPE = "content_type";
    public static final String COMMENT_CONTENT = "comment_content";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String STICKER_GROUP_PATH = "sticker_group_path";
    public static final String STICKER_NAME = "sticker_name";
    public static final String STICKER_TYPE = "sticker_type";
    public static final String FILE = "file";
    public static final String PHOTO_FULLSIZE = "photo_fullsize";
    public static final String API_START_CHECK_LOG_ID = API_SERVER + "/co8/index.php/api/loginID";//查询账号是否可用和获取验证码／重新获取验证码
    public static final String API_START_PHONE_CREATE_USER = API_SERVER + "/co8/index.php/api/verifyUser"; //Verify code and Create User (Phone)
    public static final String API_START_USERNAME_CREATE_USER = API_SERVER + "/co8/index.php/api/verifyUser"; //Verify code and Create User (Username)
    public static final String API_START_COMPLETE_PROFILE = API_SERVER + "/co8/index.php/api/profile"; //Complete profile


    public static final String API_START_FORGOT_PASSWORD_GET_CODE = API_SERVER + "/co8/index.php/api/forgetPassword"; //忘记密码，获取验证码
    public static final String API_START_FORGOT_PASSWORD_VERIFY_CODE = API_SERVER + "/co8/index.php/api/verification"; //verify code(忘记密码)

    public static final String API_START_THIRD_PARTY_CHECK_ID = API_SERVER + "/co8/index.php/api/loginID"; //查询账号是否已登记
    public static final String API_START_THIRD_PARTY_GET_CODE = API_SERVER + "/co8/index.php/api/loginID"; //查询账号是否可用和获取验证码／重新获取验证码
    public static final String API_START_THIRD_PARTY_CREATE_USER = API_SERVER + "/co8/index.php/api/verifyUser"; //Verify code and Create User(facebook)


    public static final String API_MORE_ARCHIVE_LIST = API_SERVER + "/co8/index.php/api/archivePostMain";
    public static final String API_MORE_ARCHIVE_POSTING_LIST = API_SERVER + "/co8/index.php/api/archivePost";
    public static final String API_MORE_ARCHIVE_POSTING_DETAIL = API_SERVER + "/co8/index.php/api/archivePostDetail";
    public static final String API_MORE_ARCHIVE_COMMENT_LIST = API_SERVER + "/co8/index.php/api/comment";
    public static final String API_MORE_ARCHIVE_GROUP_MEMEBER_LIST = API_SERVER + "/co8/index.php/api/archivePostMain";

    public static final String API_MESSAGE_GROUP_IS_FRIEND = API_SERVER + "/co8/index.php/api/memberAction/%s/member/%s/type/check ";

    public static final String TRACKER_URL = "http://bwstat.bondwith.me/";
    public static final String TRACKER_AUTH_TOKEN = "3bde48623ab1cea339c606abd09debd7";


    public static final String API_MESSAGE_DOWNLOAD_AUDIO = API_SERVER + "/co8/index.php/api/audio_preview/%s/fid/%s";
    public static final String API_MESSAGE_DOWNLOAD_VIDEO = API_SERVER + "/co8/index.php/api/video_preview/%s/fid/%s";
    public static final String API_MESSAGE_DOWNLOAD_VIDEO_PIC = API_SERVER + "/co8/index.php/api/video_thumbnail/%s/fid/%s";

    /**
     * 视频存放路径
     */
    public static final String VIDEO_PATH = FileUtil.getCacheFilePath(App.getContextInstance(), true) + "/Video/";

    //删除message消息
    public static final String API_MESSAGE_DELETE = API_SERVER + "/co8/index.php/api/removeContent/%s";
    public static final String API_WALL_UPDATE_CAPTION = API_SERVER + "/co8/index.php/api/editPhoto/%s";
    public static final String API_WALL_DELETE_PHOTO = API_SERVER + "/co8/index.php/api/deletePhoto/%s";

    //新的得到表情包
    public static final String API_STICKER_GROUP_LIST = API_SERVER + "/co8/index.php/api/stickerGroupList";
    //新的下载表情包
    public static final String API_DOWNLOAD_STICKER_ZIP = API_SERVER + "/co8/index.php/api/sticker_zip/%s/format/%s/fpath/%s";
    //新的显示表情图
    public static final String API_STICKER_ORIGINAL_IMAGE = API_SERVER + "/co8/index.php/api/sticker_l/%s/fcode/%s/version/%s";

    public static final String API_SEARCH_BWM_USER = API_SERVER + "/co8/index.php/api/searchBWMUser";//搜索BWM用户

    public static final String API_MATCH_CONTACT_LIST = API_SERVER + "/co8/index.php/api/matchContactList";//通讯录对比

    public static final String API_QRCode = API_SERVER + "/co8/index.php/api/photo_profile/%s/fid/qr";

    public static final String API_GET_PIC_PROFILE = API_SERVER + "/co8/index.php/api/photo_profile/%s/fid/cover";
    public static final String API_POST_PIC_PROFILE = API_SERVER + "/co8/index.php/api/userCoverPic";

    public static final String API_GET_PROFILE_QR = API_SERVER + "/co8/index.php/api/photo_profile/%s/fid/qr";

    //新的rewards
    public static final String API_GET_My_REWARD = API_SERVER + "/co8/index.php/api/myRewardList/%s";
    public static final String API_GET_REWARD_LIST = API_SERVER + "/co8/index.php/api/rewardList/%s";
    public static final String API_POST_REWARD_CODE = API_SERVER + "/co8/index.php/api/redeemReward";

    //news
    public static final String API_GET_NEWS_LIST = API_SERVER + "/co8/index.php/api/news";
    public static final String API_POST_NEWS = API_SERVER + "/co8/index.php/api/news";
    public static final String API_GET_NEWS_DETAIL = API_SERVER + "/co8/index.php/api/news";
    public static final String API_PUT_NEWS = API_SERVER + "/co8/index.php/api/updateContent/%s";
    public static final String API_GOOD_JOB_MEMBER = API_SERVER + "/co8/index.php/api/goodjob";//goodjob用户
    public static final String API_UPDATE_GOOD_JOB = API_SERVER + "/co8/index.php/api/goodjob/%s";//消除好友的goodjob

    public static final String API_GET_GROUP_DEFAULT = API_SERVER + "/co8/index.php/api/groupInfo/%s";
    public static final String API_CHECK_HAS_PENDING_REQUEST = API_SERVER + "/co8/index.php/api/pendingMember/%s";
    public static final String API_REJECT_PENDING_MEMBER = API_SERVER + "/co8/index.php/api/rejectPendingMember/%s";

    public static final String API_ORG_SEARCH = API_SERVER + "/co8/index.php/api/searchOrganisation";//获取公司名字列表

    public static final String API_ORG_CREATE = API_SERVER + "/co8/index.php/api/organisation";//创建新公司
    public static final String API_ORG_JOIN = API_SERVER + "/co8/index.php/api/joinOrg";//申请加入公司
    public static final String API_ORG_RESEND_JOIN = API_SERVER + "/co8/index.php/api/resendJoinOrgReq/%s";//重发申请加入公司
    public static final String API_ORG_CANCEL_JOIN = API_SERVER + "/co8/index.php/api/cancelJoinOrgReq/%s";//取消加入公司

    public static final String API_GET_GROUP_LIST = API_SERVER + "/co8/index.php/api/myGroup/%s";//获取组群列表
    public static final String API_GET_ALL_STAFF = API_SERVER + "/co8/index.php/api/allStaff/%s";//所有同事列表
    public static final String API_GET_ALL_OTHER = API_SERVER + "/co8/index.php/api/myOthers/%s";//所有其他成员列表
    public static final String API_GET_ALL_MAIN_MESSAGE = API_SERVER + "/co8/index.php/api/messages/%s/type/all";//所有消息列表
    public static final String API_GET_REMOVE_MESSAGE = API_SERVER + "/co8/index.php/api/hidePost/%s";//删除消息列表
    public static final String API_REMOVE_OWN_SUPPLIER = API_SERVER + "/co8/index.php/api/removeOther/%s";//删除自己的客户或供应商
    public static final String API_GET_USER_ORG = API_SERVER + "/co8/index.php/api/userOrgStatus/%s";//获取自己是否加入公司
    public static final String API_SET_MEETING_REMINDER = API_SERVER + "/co8/index.php/api/setReminder/%s";//设置会议reminder
}
