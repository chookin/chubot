package chookin.chubot.web.validator;

import chookin.chubot.web.model.User;
import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhuyin on 9/14/15.
 */
public class UserUpdateValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        validateEmail("user.email", "emailMsg", "错误的邮箱地址");
        validateRegex("user.username", "[a-zA-Z0-9_\\u4e00-\\u9fa5]{2,8}", "usernameMsg", "用户名的长度介于2-8之间，只能包含中文，数字，字母，下划线");
        validateString("user.headImg", 0, 120, "headImgMsg", "长度不能超过120");
        validateString("user.blogUrl", 0, 80, "blogUrlMsg", "长度不能超过80");
        validateString("user.feeling", 0, 280, "feelingMsg", "长度不能超过280");
        String email = c.getPara("user.email");
        if(StringUtils.isNoneBlank(email) && User.dao.containEmailExceptThis(c.getParaToInt("user.id"), email)){
            addError("emailMsg", "该email已经被注册过了：（");
        }
        String username = c.getPara("user.username");
        if(StringUtils.isNoneBlank(username) && User.dao.containUsernameExceptThis(c.getParaToInt("user.id"), username)){
            addError("usernameMsg", "该用户名已经被注册过了：（");
        }
    }

    @Override
    protected void handleError(Controller c) {
        c.keepModel(User.class);
        c.render("/user/edit.html");
    }
}
