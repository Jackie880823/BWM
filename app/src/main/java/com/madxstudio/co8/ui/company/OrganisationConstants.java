package com.madxstudio.co8.ui.company;

import com.madxstudio.co8.App;
import com.madxstudio.co8.R;

/**
 * Created 16/3/31.
 *
 * @author Jackie
 * @version 1.0
 */
public class OrganisationConstants {
    public static final String CO8_API_SERVER = App.getContextInstance().getString(R.string.co8_api_server);
    /**
     * 测试所用的公司ID正式版时要删除
     */
    public static final String TEST_COMPANY_ID = "2";
    public static final String TEST_USER_ID = "3";

    public static final String API_GET_ORGANISATION_DETAILS = CO8_API_SERVER + "organisation/%s";
    public static final String API_PUT_ORGANISATION_DETAILS = CO8_API_SERVER + "organisation/%s";
    public static final String API_GET_ORGANISATION_COVER = CO8_API_SERVER + "photo_profile/%s/fid/organisation";
    public static final String API_POST_ORGANISATION_COVER = CO8_API_SERVER + "orgCoverPic";
    public static final String API_REMOVE_ADMIN = CO8_API_SERVER + "removeAdmin/%s";
    public static final String API_POST_ADD_ADMIN = CO8_API_SERVER + "addAdmin";

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
    public static final String MEMBER_ID = "member_id";

}
