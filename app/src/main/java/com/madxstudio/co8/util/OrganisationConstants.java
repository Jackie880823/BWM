package com.madxstudio.co8.util;

import com.madxstudio.co8.App;
import com.madxstudio.co8.R;

/**
 * Created 16/3/31.
 *
 * @author Jackie
 * @version 1.0
 */
public class OrganisationConstants {
    public static final String CO8_API_SERVER = App.getContextInstance().getString(R.string.api_server) + "/co8/index.php/api/";

    /**
     * 测试所用的{@code org_id}正式版时要删除
     */
    public static final String TEST_COMPANY_ID = "2";

    /**
     * 测试所用的{@code user_id}正式版时要删除
     */
    public static final String TEST_USER_ID = "3";

    /**
     * GET – Get organization details 获取公司资料 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_GET_ORGANISATION_DETAILS = CO8_API_SERVER + "organisation/%s";

    /**
     * GET - Organization Cover  获取公司背景图片 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_GET_ORGANISATION_COVER = CO8_API_SERVER + "photo_profile/%s/fid/organisation";

    /**
     * Admin Pending Request list 获取管理员待批准列表 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_GET_ADMIN_PENDING_REQUEST_LIST = CO8_API_SERVER + "pendingRequest/%s/type/admin";

    /**
     * GET – All Others(Admin) 公司管理员获取全部其它成员列表 <br/>
     * 第一个%s匹配为当前登陆用户的{@code user_id} <br/>
     * 第二个%s匹配{@code org_id}
     */
    public static final String API_GET_ADMIN_ALL_OTHER = CO8_API_SERVER + "allOthers/%s/org/%s";
    /**
     * GET - Admin state
     * %s匹配{@code user_id}
     */
    public static final String API_GET_ADMIN_STATE = CO8_API_SERVER + "orgAdmin/%s";

    /**
     * PUT – Update organization details 更新公司资料 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_ORGANISATION_DETAILS = CO8_API_SERVER + "organisation/%s";

    /**
     * PUT – Approve user to join organization 同意加入公司 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_ACCEPT_JOIN_ORG_REQ = CO8_API_SERVER + "acceptJoinOrgReq/%s";
    /**
     * PUT – Approve user to leave organization 同意离开公司 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_ACCEPT_LEAVE_ORG_REQ = CO8_API_SERVER + "acceptLeaveOrgReq/%s";
    /**
     * PUT – Reject user to join organization 拒绝成员加入公司 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_REJECT_JOIN_ORG_REQ = CO8_API_SERVER + "rejectJoinOrgReq/%s";

    /**
     * PUT – Remove member from organization 删除公司员工 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_REMOVE_ORG_MEMBER = CO8_API_SERVER + "removeOrgMember/%s";

    /**
     * PUT – Remove supplier and customer for staff from organization 删除公司员工的供应商和客户 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_PUT_REMOVE_ORG_OTHER = CO8_API_SERVER + "removeOrgOther/%s";

    /**
     * PUT – Remove own supplier and customer 删除自己的供应商和客户<br/>
     * %s匹配为当前登陆用户的{@code user_id}
     */
    public static final String API_PUT_REMOVE_OTHER = CO8_API_SERVER + "removeOther/%s";

    /**
     * PUT – Remove Admin  删除公司管理员 <br/>
     * %s匹配{@code org_id}
     */
    public static final String API_REMOVE_ADMIN = CO8_API_SERVER + "removeAdmin/%s";

    /**
     * POST – Upload Organisation Cover photo
     */
    public static final String API_POST_ORGANISATION_COVER = CO8_API_SERVER + "orgCoverPic";

    /**
     * POST – Add Admin  添加公司管理员
     */
    public static final String API_POST_ADD_ADMIN = CO8_API_SERVER + "addAdmin";

    /**
     * POST – Request leave organisation申请离开公司
     */
    public static final String API_POST_LEAVE_ORGANISATION = CO8_API_SERVER + "leaveOrg";

    public static final String NEED_ADD_ADMIN_USER = "need_add_admin_user";

    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ADDRESS = "address";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";

    // FILE Info:
    public static final String FILE_KEY = "fileKey";
    public static final String FILE_NAME = "fileName";
    public static final String MIME_TYPE = "mimeType";
    public static final String FILE = "file";

    public static final String ORG_ID = "org_id";

    public static final String USER_ID = "user_id";
    public static final String USER_GIVEN_NAME = "user_given_name";
    public static final String MEMBER_ID = "member_id";
    public static final String ADMIN = "admin";

    public static final String MODULE_ACTION_LEAVE = "leave";
    public static final String MODULE_ACTION_JOIN = "join";

}
