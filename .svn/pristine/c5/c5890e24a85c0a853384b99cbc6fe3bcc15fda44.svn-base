/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author tuanda
 */
public class AIOConstants {

    
    public static String KEYWORD_FORGOT_PSW = "XEENG MK";
    public static String DAUSO_FORGOT_PSW = "8075";
    
    public static final String SEPERATOR_ELEMENT = "|";
    public static final String SEPERATOR_ARRAY = ";";
    public static final String SEPERATOR_DIFF_ELEMENT = "^";
    public static final String STRING_SEPERATOR_ELEMENT = "\\|";
    
    public static String DEFAULT_UPLOAD_URL = "upload/image/";
    public static final String DEFAUL_ICON_SUFFIX = "-icon.jpg";
    public static final String DEFAULT_NORMAL_FILE = ".jpg";
    public static final String DEFAUL_ICON_BASE64_SUFFIX = "-icon.txt";
    public static final String DEFAUL_ICON80_BASE64_SUFFIX = "-icon80.txt"; //for android and iOS
    public static final String DEFAUL_NORMAL_BASE64_FILE = ".txt";
    public static final int VIPGAME_PARTNER_ID = 0;
    public static final int XOMTUI_PARTNER_ID = 68;
    public static final int MEDIA_MXH_ID = 73;
    public static final int ME_MXH_ID = 74;
    public static final int VMG_PARTNER_ID = 69;
    public static final int LOTUS_PARTNER_ID = 75;
    public static final int MAX_RETRY_LOGIN = 3;
    public static final int PROTOCOL_PRIMITIVE = 0;
    public static final int PROTOCOL_BETA = 1;
    public static final int PROTOCOL_UPDATE_CANCEL = 2;
    public static final int PROTOCOL_ADVERTISING = 3;
    public static final int PROTOCOL_MODIFY_MID = 4;
    public static final int PROTOCOL_ACTIVE_LOGIN = 5;
    public static final int PROTOCOL_MXH = 6;
    public static final int PROTOCOL_REFACTOR_ALBUM = 7;
    public static final int PROTOCOL_REFACTOR_BIENG = 8;
    public static final int FIRST_BYTE_GAME = 1;
    public static final int FIRST_BYTE_AVATAR = 2;
    public static final int FIRST_BYTE_PING = 3;
    public static String SEPERATOR_BYTE_1;
    public static String SEPERATOR_BYTE_2;
    public static String SEPERATOR_BYTE_3;
    public static String SEPERATOR_NEW_MID;
    public static String BYTE_5;
    public static byte[] dataPing;
    public static final int RUNNING_ADVERTISING = 0;
    public static final int FIRST_LOGIN_ADVERTISING = 2;
    public static final int CHANGE_GAME_POSITION_ADVERTISING = 3;
    public static final int LINK_MESSAGE_ADVERTISING = 4;
    public static final int MAX_BLOG_MAIN_IDEA = 100;
    public static final int MAX_BLOG_MAIN_IDEA_ANDROID = 400;
    public static final int PUBLIC_ROLE = 0;
    public static final int ONLY_FRIENDS_ROLE = 1;
    public static final int ONLY_ME_ROLE = 4;
    public static final String DEFAULT_ERROR_CARD = "Hiện hệ thống nạp thẻ đang quá tải bạn thử lại xem sao";
    public static final int PAGE_DEFAULT_SIZE = 5;
    public static final int PAGE_10_DEFAULT_SIZE = 10;
    public static final int PAGE_50_DEFAULT_SIZE = 50;
    public static final int BIA_FILE_TYPE = 1;
    public static final int AVATAR_FILE_TYPE = 0;
    public static final int NORMAL_FILE_TYPE = 2;
    public static final int MAX_ALERT = 10;
    public static final int MAX_COMMENT = 10;
    public static final int CREATE_BLOG_SYSTEM_OBJECT_ID = 1;
    public static final int MODIFY_BLOG_SYSTEM_OBJECT_ID = 2;
    public static final int POST_WALL_BLOG_SYSTEM_OBJECT_ID = 3;
    public static final int POST_WALL_SYSTEM_OBJECT_ID = 3;
    public static final int FILE_SYSTEM_OBJECT_ID = 2;
    public static final int ALBUM_SYSTEM_OBJECT_ID = 3;
    public static final int BLOG_SYSTEM_OBJECT_ID = 1;
    public static final int ALBUMS_POSITION = 1;
    public static final int ALBUM_DETAIL_POSITION = 2;
    public static final int THEO_DONG_SU_KIEN_CAT = 1;
    public static final int CUOI_CAT = 63;
    public static final int TAM_SU_CAT = 67;
    public static final int TYPE_FILE_AVATAR = 2;
    public static final int TYPE_AVATAR_AVATAR = 0;
    public static final int TYPE_ANH_BIA_AVATAR = 1;
    public static final int M4V_PARTNER = 67;
    public static final int MEDIA_PARTNER = 66;
    public static final int HUNGQUAN_PARTNER = 5;
    public static final int ANDROID_DEVICE = 3;
    public static final int IPHONE_DEVICE = 4;
    public static final int TIMES = 4;
    public static final int FLASH_DEVICE = 2;
    public static final int MOBILE_DEVICE = 1;
    public static String IMG_PATH_PROCESSING = "";
    public static String NEW_LINE = "";
    public static String MOBIFONE_PATTERN_IP1 = "^(\113.187|\183.91|\203.162|\123.30)[0-9.]{0,10}$";
    public static String MOBIFONE_PATTERN_IP2 = "^(\101.99|\206.53)[0-9.]{0,10}$";
    public static String MOBIFONE_PATTERN_IP3 = "^(\\91.203|\217.212)[0-9.]{0,10}$";
    public static String VINAPHONE_PATTERN_IP = "^(\113.185)[0-9.]{0,10}$";
    public static String MOBIFONE_PREFIX = "^(90|93|120|121|122|124|126|128|090|093|0120|0121|0122|0124|0126|0128|8490|8493|84120|84121|84122|84124|84126|84128)[0-9]{7}$";
    public static String VINAPHONE_PREFIX = "^(91|94|123|124|125|127|129|091|094|0123|0124|0125|0127|0129|8491|8494|84123|84124|84125|84127|84129)[0-9]{7}$";

    static {
        init();
    }

    private static void init() {
    	
        SEPERATOR_BYTE_1 	= new String(new byte[]{(byte) (1)});
        SEPERATOR_BYTE_2 	= new String(new byte[]{(byte) (2)});
        SEPERATOR_BYTE_3 	= new String(new byte[]{(byte) (3)});
        SEPERATOR_NEW_MID 	= new String(new byte[]{(byte) (4)});
        BYTE_5 				= new String(new byte[]{(byte) (5)});

        dataPing = ("1" + SEPERATOR_BYTE_1 + "1" + SEPERATOR_NEW_MID).getBytes();

        Properties appConfig = new Properties();
        try {
            appConfig.load(new FileInputStream("conf/param.properties"));
            DEFAULT_UPLOAD_URL = appConfig.getProperty("UPLOAD_MXH_DIR");
            IMG_PATH_PROCESSING = appConfig.getProperty("IMG_PATH");
        } catch (IOException ex) {
        }

        NEW_LINE = System.getProperty("line.separator");

    }
}
