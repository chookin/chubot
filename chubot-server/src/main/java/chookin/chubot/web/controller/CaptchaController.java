package chookin.chubot.web.controller;

import chookin.chubot.web.intercepter.LoginInterceptor;
import cmri.utils.web.jfinal.render.AlphanumCatchaRender;
import cmri.utils.web.jfinal.render.CaptchaRender;
import cmri.utils.web.jfinal.render.MixedCaptchaRender;
import cmri.utils.configuration.ConfigManager;
import cmri.utils.lang.Pair;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;

/**
 * Created by zhuyin on 9/16/15.
 */
public class CaptchaController extends Controller {
    @Clear(LoginInterceptor.class)
    public void index(){
        Integer width = getParaToInt("width");
        Integer height = getParaToInt("height");
        Integer fontSize = getParaToInt("fontsize");
        CaptchaRender img;
        if(ConfigManager.getBool("captcha.simple", true)){
            img = new AlphanumCatchaRender(ConfigManager.getInt("captcha.number"), width, height, fontSize);
        }else {
            img = new MixedCaptchaRender(ConfigManager.getInt("captcha.number"), width, height, fontSize);
        }
        this.setSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY, new Pair<>(img.getCode(), System.currentTimeMillis()));
        render(img);
    }
}
