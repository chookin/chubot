package chookin.chubot.web.model;


import chookin.chubot.web.jfinal.Model;
import org.apache.commons.codec.digest.DigestUtils;

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
    public User getUser(int id) {
        return loadModel(id);
    }
    public User getUser(String identify, String password){
        if(identify.contains("@")) {
            return dao.findFirst("select id, name, email, password, login_times from user where email=? and password=?", identify, password);
        }else{
            return dao.findFirst("select id, name, email, password, login_times from user where name=? and password=?", identify, password);
        }
    }

    public boolean save(){
        String password = this.getStr("password");
        this.set("password", crpytoPassword(password)).set("create_time", new Date());
        removeCache(this.getInt("id"));

        return super.save();
    }
    static String crpytoPassword(String password){
        return DigestUtils.md5Hex(DigestUtils.sha1Hex(password));
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
}
