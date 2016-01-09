package chookin.chubot.web.controller;

import chookin.chubot.web.config.Const;
import chookin.chubot.web.intercepter.LoginInterceptor;
import cmri.utils.web.jfinal.BaseController;
import cmri.utils.web.jfinal.render.CaptchaRender;
import chookin.chubot.web.model.User;
import chookin.chubot.web.validator.LoginValidator;
import chookin.chubot.web.validator.RegistValidator;
import chookin.chubot.web.validator.UserUpdateValidator;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.Pair;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.ext.interceptor.POST;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

/**
 * Created by zhuyin on 9/14/15.
 */
public class UserController extends BaseController {
    @Clear(LoginInterceptor.class)
    public void login(){
        index();
    }
    @Clear(LoginInterceptor.class)
    public void register(){
        index();
    }
    @Clear(LoginInterceptor.class)
    @Before({LoginValidator.class, POST.class})
    public void doLogin(){
        if (validateCaptcha()) {
            String userIdentify = getPara("user");
            String password = getPara("password");
            User user = User.dao.getUser(userIdentify, password);
            if (user != null) {
                user.set("loginTimes", 1 + user.getInt("loginTimes"));
                user.set("lastLoginTime", new Timestamp(System.currentTimeMillis()));
                String ip = getIpAddr(getRequest());
                user.set("lastLoginIp", ip);
                user.update();

                removeSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);

                setCookie("trackId", generateTrackId(ip, userIdentify, password), ConfigManager.getInt("cookie.expires"));
                getSession().setMaxInactiveInterval(ConfigManager.getInt("session.maxInactiveInterval"));
                setSessionAttr("user", user);
            } else {
                setAttr("error", "用户名或密码错误");
            }
        }
        renderJson();
    }
    @Before(POST.class)
    public void doLogout(){
        removeSessionAttr("user");
        removeCookie("trackId");
        index();
    }
    private boolean validateCaptcha(){
        String captcha = getPara("captcha");
        Pair<String, Long> pair = getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
        if(pair != null){
            if(pair.getValue() > System.currentTimeMillis() - ConfigManager.getInt("captcha.expires")*1000) {
                if (pair.getKey().equalsIgnoreCase(captcha)) {
                    return true;
                }
            }else {
                setAttr("error", "验证码过期");
                return false;
            }
        }
        setAttr("error", "验证码错误");
        return false;
    }

    private String generateTrackId(String host, String userIdentify, String password){
        return userIdentify + Const.ID_SEPARATOR + crypto(host, password) + Const.ID_SEPARATOR + System.currentTimeMillis();
    }
    public static String crypto(String host, String password){
        return DigestUtils.md5Hex(DigestUtils.sha1Hex(host + password));
    }
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
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
        setAttr("user", User.dao.getUser(getParaToInt(0, 0)));
        render("/user/edit.html");
    }
}
