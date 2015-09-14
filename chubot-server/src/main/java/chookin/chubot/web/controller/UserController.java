package chookin.chubot.web.controller;

import chookin.chubot.web.config.Const;
import chookin.chubot.web.intercepter.LoginInterceptor;
import chookin.chubot.web.model.User;
import chookin.chubot.web.validator.LoginValidator;
import chookin.chubot.web.validator.RegistValidator;
import chookin.chubot.web.validator.UserUpdateValidator;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.POST;

import java.sql.Timestamp;

/**
 * Created by zhuyin on 9/14/15.
 */
public class UserController extends Controller {
    public void index(){
        setAttr("user", User.dao.get(getParaToInt(0, 0)));
        render("/user/user.html");
    }

    @Clear(LoginInterceptor.class)
    @Before({LoginValidator.class, POST.class})
    public void login(){
        String email = getPara("email");
        String password = getPara("password");
        User user = User.dao.getByEmailAndPassword(email, password);
        if (user != null){
            user.set("login_times", 1 + user.getInt("login_times"));
            user.set("last_login_time", new Timestamp(System.currentTimeMillis()));
            String bbsID = email + Const.ID_SEPARATOR + password;
            setCookie("bbsID", bbsID, 3600*24*30);
            setSessionAttr("user", user);
            setSessionAttr("userID", user.get("id"));
            redirect("/");
        }else{
            setAttr("error", "用户名或密码错误");
            renderJson();
        }
    }

    public void logout(){
        removeSessionAttr("user");
        removeSessionAttr("userID");
        removeCookie("bbsID");
        redirect("/");
    }

    @Before(RegistValidator.class)
    public void save(){
        User user = getModel(User.class);
        user.save();
        setAttr("msg", "恭喜你，注册成功，请登录：");
        render("/user/login.html");
    }

    @Before(UserUpdateValidator.class)
    public void edit(){
        setAttr("user", User.dao.get(getParaToInt(0, 0)));
        render("/user/edit.html");
    }
}
