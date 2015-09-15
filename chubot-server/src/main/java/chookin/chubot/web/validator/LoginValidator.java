package chookin.chubot.web.validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

/**
 * Created by zhuyin on 9/14/15.
 */
public class LoginValidator extends Validator {
    @Override
    protected void validate(Controller c) {
        validateRequired("password", "error", "密码不能为空");
    }

    @Override
    protected void handleError(Controller c) {
        c.keepPara("user");
    }
}
