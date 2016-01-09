package chookin.chubot.web.model;


import cmri.utils.web.jfinal.BaseModel;
import cmri.utils.web.jfinal.tablebind.TableBind;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Timestamp;

/**
 * Created by zhuyin on 9/14/15.
 */
@TableBind(tableName = "user")
public class User extends BaseModel<User> {
    public static final User dao = new User();
    public User(){
        super("user");
    }
    public User getUser(int id) {
        return loadModel(id);
    }
    public User getUser(String identify){
        if(identify.contains("@")) {
            return dao.findFirst("select id, name, email, password, loginTimes from user where email=?", identify);
        }else{
            return dao.findFirst("select id, name, email, password, loginTimes from user where name=?", identify);
        }
    }
    public User getUser(String identify, String password){
        if(identify.contains("@")) {
            return dao.findFirst("select id, name, email, password, loginTimes from user where email=? and password=?", identify, password);
        }else{
            return dao.findFirst("select id, name, email, password, loginTimes from user where name=? and password=?", identify, password);
        }
    }

    public boolean save(){
        removeCache(this.getLong("id"));
        return super.save();
    }
    static String cryptoPassword(String word){
        return DigestUtils.md5Hex(DigestUtils.sha1Hex(word));
    }
    public boolean update() {
        this.set("updateTime", new Timestamp(System.currentTimeMillis()));
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
