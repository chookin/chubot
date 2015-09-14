package chookin.chubot.web.intercepter;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 9/14/15.
 */
public class LoginInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        if(controller.getSessionAttr("user") != null){
            inv.invoke();
        }else{
            controller.redirect("/user/login.html");
        }
    }
}

