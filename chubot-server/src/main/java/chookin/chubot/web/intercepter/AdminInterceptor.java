package chookin.chubot.web.intercepter;

import chookin.chubot.web.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 9/14/15.
 */
public class AdminInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation ai) {
        Controller controller = ai.getController();
        User user = controller.getSessionAttr("user");
        if (user != null ){
            ai.invoke();
        }else{
            controller.setAttr("msg", "需要管理员权限");
            controller.renderError(500);
        }
    }
}
