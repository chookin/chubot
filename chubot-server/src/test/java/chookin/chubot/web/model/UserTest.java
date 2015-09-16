package chookin.chubot.web.model;

import chookin.chubot.web.controller.UserController;
import org.junit.Test;

/**
 * Created by zhuyin on 9/15/15.
 */
public class UserTest {

    @Test
    public void testCrpytoPassword() throws Exception {
        String pwd = "admin";
        String crpt = User.cryptoPassword(pwd);
        System.out.println(crpt);
        System.out.println(UserController.crypto("192.168.80.1", crpt));
    }
}