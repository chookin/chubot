package chookin.chubot.web.model;


import chookin.chubot.web.jfinal.Model;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by zhuyin on 9/14/15.
 */
public class User extends Model<User> {
    public static final User dao = new User();
    public User(){
        super("user");
    }
    public User get(int id) {
        return loadModel(id);
    }
    public User getByEmailAndPassword(String email, String password){
        return dao.findFirst("select id, email, password, login_times from user where email=? and password=?", email, password);
    }

    public boolean save(){
        String password = getMD5(this.getStr("password").getBytes());
        this.set("password", password).set("create_time", new Date());
        removeCache(this.getInt("id"));

        return super.save();
    }
    public boolean update() {
        this.set("update_time", new Timestamp(System.currentTimeMillis()));
        return super.update();
    }
    public boolean containEmail(String email) {
        return dao.findFirst("select email from user where email=? limit 1", email) != null;
    }
    public boolean containUsername(String name) {
        return dao.findFirst("select name from user where username=? limit 1", name) != null;
    }
    public boolean containEmailExceptThis(int userID, String email) {
        return dao.findFirst("select email from user where email=? and id!=? limit 1", email, userID) != null;
    }
    public boolean containUsernameExceptThis(int userID, String username) {
        return dao.findFirst("select name from user where name=? and id!=? limit 1", username, userID) != null;
    }

    private String getMD5(byte[] src){
        StringBuilder sb=new StringBuilder();
        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fail to get md5 algorithm", e);
        }
        md.update(src);
        for(byte b : md.digest())
            sb.append(Integer.toString(b>>>4&0xF,16)).append(Integer.toString(b&0xF,16));
        return sb.toString();
    }
}
