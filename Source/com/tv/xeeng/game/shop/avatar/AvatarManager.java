/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tv.xeeng.game.shop.avatar;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.imageio.ImageIO;

import com.tv.xeeng.databaseDriven.DBPoolConnection;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.Couple;
import com.tv.xeeng.game.data.SimpleException;
import com.tv.xeeng.game.data.Triple;

/**
 *
 * @author tuanda
 */
public class AvatarManager {

    private static Hashtable<Integer, AvatarEntity> avatars = new Hashtable<>();
    private static Hashtable<Long, Couple<ArrayList<AvatarUserEntity>, Integer>> avatarsUser = new Hashtable<>();
    private static ArrayList<AvatarCategoryEntity> lstCategory = new ArrayList<>();

    public static void reload() throws SQLException {
        String query = "{call GetAvatar()}";
        try (Connection conn = DBPoolConnection.getConnection();
                CallableStatement cs = conn.prepareCall(query);
                ResultSet rs = cs.executeQuery()) {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("avatarID");
                    String name = rs.getString("name");
                    String category = rs.getString("categoryName");
                    int categoryid = rs.getInt("categoryID");
                    String icon = rs.getString("icon");
                    String bigIcon = rs.getString("bigIcon");
                    String detailIcon = rs.getString("detailIcon");
                    String description = rs.getString("description");
                    long price = rs.getLong("price");
                    int appearDate = rs.getInt("appearDate");
                    int likeRate = rs.getInt("likeRate");
                    boolean isMale = rs.getBoolean("sex");
                    long ownerID = rs.getLong("ownerID");
                    AvatarEntity ent = new AvatarEntity(id, name, category, categoryid, icon, bigIcon,
                            detailIcon, description, price, appearDate, likeRate, isMale, ownerID);
                    avatars.put(ent.id, ent);

                }
            }

        }

        reloadCategory();
    }

    public static String getCategory() {
        StringBuilder sb = new StringBuilder();
        for (AvatarCategoryEntity a : lstCategory) {
            sb.append(a.toString()).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();

    }

    private static void reloadCategory() throws SQLException {
        String query = "{call GetCategoryAvatar()}";
        try (Connection conn = DBPoolConnection.getConnection();
                CallableStatement cs = conn.prepareCall(query);
                ResultSet rs = cs.executeQuery()) {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    AvatarCategoryEntity temp = new AvatarCategoryEntity(id, name);
                    lstCategory.add(temp);
                }
            }

        }
    }

    public static int likeAvatar(int id, long uid) throws SQLException, SimpleException {
        String query = "{call LikeAvatar(?,?)}";
        try (Connection conn = DBPoolConnection.getConnection();
                CallableStatement cs = conn.prepareCall(query)) {
            cs.clearParameters();
            cs.setInt(1, id);
            cs.setLong(2, uid);
            ResultSet rs = cs.executeQuery();
            int res = 0;
            if(rs!= null && rs.next()) {
                res = rs.getInt("likeRate");
            }
            if(res == 0) { // liked
                throw new SimpleException("Bạn đã like avatar này rồi!");
            }
            AvatarEntity ent = avatars.get(id);
            ent.likeRate = res;
            return res;
        }
    }
    private static boolean hasAvatar(int id, ArrayList<AvatarUserEntity> temp) {
        for(AvatarUserEntity a : temp) {
            if(a.avatarID == id) return true;
        }
        return false;
    }
    public static void chooseAvatar(long uid, int id) throws SQLException, SimpleException {
        Couple<ArrayList<AvatarUserEntity>, Integer> ent;
        if(avatarsUser.containsKey(uid)) {
            ent = avatarsUser.get(uid);
        } else {
            ent = getAvatarForUserFromDB(uid);
        }
        ArrayList<AvatarUserEntity> temp = ent.e1;
        if(!hasAvatar(id, temp)) {
            throw new SimpleException("Bạn không có avatar này!");
        }
        String query = "{call ChooseAvatar(?,?)}";
        try (Connection conn = DBPoolConnection.getConnection();
                CallableStatement cs = conn.prepareCall(query)) {
            cs.clearParameters();
            cs.setInt(1, id);
            cs.setLong(2, uid);
            cs.executeUpdate();            
            ent.e2 = id;
            avatarsUser.put(uid, ent);
        }
    }

    public static String getAvatarForUser(long userID, boolean isJava) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ArrayList<AvatarUserEntity> temp;
        Couple<ArrayList<AvatarUserEntity>, Integer> temp1;// = avatarsUser.get(userID);
        if (!avatarsUser.containsKey(userID)) {
            temp1 = getAvatarForUserFromDB(userID);
            avatarsUser.put(userID, temp1);
        } else {
            temp1 = avatarsUser.get(userID);
        }
        int chooseID = temp1.e2;
        temp = temp1.e1;
        int len = temp.size();
        for (int i = 0; i < len; i++) {
            sb.append(temp.get(i).toString(isJava)).append(AIOConstants.SEPERATOR_BYTE_2);
        }
        if (len > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(AIOConstants.SEPERATOR_BYTE_3).append(chooseID);
        return sb.toString();
    }

    public static String getChooseAvatarForUser(long userID, boolean isJava) throws SQLException {
        StringBuilder sb = new StringBuilder();
        Couple<ArrayList<AvatarUserEntity>, Integer> temp1;// = avatarsUser.get(userID);
        if (!avatarsUser.containsKey(userID)) {
            temp1 = getAvatarForUserFromDB(userID);
            avatarsUser.put(userID, temp1);
        } else {
            temp1 = avatarsUser.get(userID);
        }
        int chooseID = temp1.e2;
        sb.append(avatarDetail(chooseID, isJava));
        sb.append(AIOConstants.SEPERATOR_BYTE_1).append(chooseID);
        return sb.toString();
    }

    private static Couple<ArrayList<AvatarUserEntity>, Integer> getAvatarForUserFromDB(long userID) throws SQLException {
        String query = "{call GetAvatarForUser(?)}";
        ArrayList<AvatarUserEntity> res = new ArrayList<>();
        int chooseID = 0;
        try (Connection conn = DBPoolConnection.getConnection();) {
            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, userID);
            ResultSet rs = cs.executeQuery();
            if (rs != null) {
                while (rs.next()) {
                    int dateLeft = rs.getInt("dateLeft");
                    int avatarID = rs.getInt("avatarID");
                    String name = rs.getString("name");
                    String icon = rs.getString("icon");
                    String bigIcon = rs.getString("bigIcon");
                    long price = rs.getLong("price");
                    boolean isMale = rs.getBoolean("sex");
                    res.add(new AvatarUserEntity(icon, bigIcon, avatarID,
                            name, price, isMale, dateLeft));
                }
            }
            query = "{call GetChooseAvatarForUser(?)}";
            cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setLong(1, userID);
            rs = cs.executeQuery();
            if (rs != null && rs.next()) {
                chooseID = rs.getInt("avatarID");
            }

        }

        return new Couple<>(res, chooseID);
    }

    public static int buyAvatar(int avatarID, long userID) throws SQLException {
        String query = "{call BuyAvatar(?,?)}";
        Connection conn = DBPoolConnection.getConnection();
        int res = 0;
        try {
            try (CallableStatement cs = conn.prepareCall(query)) {
                cs.clearParameters();
                cs.setInt(1, avatarID);
                cs.setLong(2, userID);
                ResultSet rs = cs.executeQuery();
                if (rs != null && rs.next()) {
                    res = rs.getInt("result");
                }
            }

        } finally {
            conn.close();
        }
        return res;
    }
    private static int MAX_AVATAR_PER_PAGE = 10;

    public static String avatarByType(boolean isJava, int page, int category) {
        StringBuilder sb = new StringBuilder();
        Enumeration<AvatarEntity> enums = avatars.elements();
        int i = 0;
        int index = 0;
        while (enums.hasMoreElements() && index < MAX_AVATAR_PER_PAGE) {
            i++;
            AvatarEntity at = enums.nextElement();
            if (at.categoryID == category || category == 0) {
                if (MAX_AVATAR_PER_PAGE * page - 1 > i && (MAX_AVATAR_PER_PAGE * (page - 1) - 1 < i)) {
                    index++;
                    sb.append(at.toString(isJava)).append(AIOConstants.SEPERATOR_BYTE_2);
                }
            }
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String avatarDetail(int avatarID, boolean isJava) {
        StringBuilder sb = new StringBuilder();
        try {
            AvatarEntity temp = avatars.get(avatarID);
            sb.append(temp.getDetail(isJava));
        } catch (Throwable e) {
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        //importAvatarData();
        importGiftData();
    }

    private static void importGiftData() {
        String name = "Bia";
        String fileName = "GiftGameImage/" + "bia.png";
        Triple<String, String, String> detail = readImage(fileName);
        String icon = detail.e1;
        String bigIcon = detail.e2;
        long price = 5000;
        int category = 1;
        String passiveChat = "Ô ngon!";
        String acctiveChat = "Bia nào!";
        String query = "{call InsertGiftGame(?,?,?,?,?,?,?)}";
        ArrayList<AvatarUserEntity> res = new ArrayList<>();
        try (Connection conn = DBPoolConnection.getConnection();) {
            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setString(1, name);
            cs.setString(2, icon);
            cs.setString(3, bigIcon);
            //cs.setString(4, realIcon);
            cs.setLong(4, price);
            cs.setInt(5, category);
            cs.setString(6, passiveChat);
            cs.setString(7, acctiveChat);
            cs.execute();
        } catch (Throwable e) {
        }
    }

    private static void importAvatarData() {
        String name = "Tiểu thư";
        String fileName = "Avatar_java_New/" + "16tieuthu.png";
        Triple<String, String, String> detail = readImage(fileName);
        String icon = detail.e1;
        String bigIcon = detail.e2;
        String realIcon = detail.e3;
        long price = 5000;
        int appearDate = 15;
        //int likeRate = 0;
        int category = 1;
        long ownerID = 1;
        int sex = 0;
        String query = "{call InsertAvatar(?,?,?,?,?,?,?,?,?)}";
        ArrayList<AvatarUserEntity> res = new ArrayList<>();
        try (Connection conn = DBPoolConnection.getConnection();) {
            CallableStatement cs = conn.prepareCall(query);
            cs.clearParameters();
            cs.setString(1, name);
            cs.setString(2, icon);
            cs.setString(3, bigIcon);
            cs.setString(4, realIcon);
            cs.setLong(5, price);
            cs.setInt(6, appearDate);
            cs.setLong(7, ownerID);


            cs.setBoolean(8, sex == 1);
            cs.setInt(9, category);
            cs.execute();
        } catch (Throwable e) {
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage,
            int type, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    private static Triple<String, String, String> readImage(String fileName) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(fileName));

            int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img
                    .getType();
            BufferedImage smallImage = resizeImage(img, type, 40, 40);
            BufferedImage bigImage = resizeImage(img, type, 60, 60);
            BufferedImage detailImage = resizeImage(img, type, 200, 200);
            return new Triple<>(convertImageBase64(smallImage),
                    convertImageBase64(bigImage), convertImageBase64(detailImage));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertImageBase64(BufferedImage imageBuf)
            throws IOException {
        ByteArrayOutputStream f = new ByteArrayOutputStream();
        ImageIO.write(imageBuf, "jpg", f);
        byte[] arrByte = f.toByteArray();
        return com.sun.midp.io.Base64.encode(arrByte, 0,
                arrByte.length);

    }
}
