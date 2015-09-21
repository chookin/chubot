package chookin.chubot.web.intercepter;

import chookin.chubot.web.config.Const;
import chookin.chubot.web.controller.UserController;
import chookin.chubot.web.model.User;
import cmri.utils.configuration.ConfigManager;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Created by zhuyin on 9/14/15.
 */
public class LoginInterceptor implements Interceptor {
    private static final Logger LOG = Logger.getLogger(LoginInterceptor.class);
    @Override
    public void intercept(Invocation inv) {
        try {
            Controller controller = inv.getController();
            if (controller.getSessionAttr("user") == null) {
                if (!generateSession(controller)) {
                    controller.redirect("/user/login");
                    return;
                }
            }
            inv.invoke();
        }catch (Throwable t){
            LOG.error(null, t);
        }
    }

    /**
     * Get user info from cookie.
     * @return null if fail to get.
     */
    private User findUser(Controller controller){
        String trackId = controller.getCookie("trackId");
        if(StringUtils.isBlank(trackId)) {
            return null;
        }
        String[] infos = trackId.split(Const.ID_SEPARATOR);
        if(infos.length == 3){
            User user = User.dao.getUser(infos[0]);
            String clientIp = UserController.getIpAddr(controller.getRequest());
            if(user != null && UserController.crypto(clientIp, infos[1]).equals(user.get("password"))){
                return user;
            }
        }
        return null;
    }

    private boolean generateSession(Controller controller){
        User user = findUser(controller);
        if(user == null){
            controller.removeCookie("trackId");
            return false;
        }
        controller.getSession().setMaxInactiveInterval(ConfigManager.getAsInteger("session.maxInactiveInterval"));
        controller.setSessionAttr("user", user);
        return true;
    }
}

