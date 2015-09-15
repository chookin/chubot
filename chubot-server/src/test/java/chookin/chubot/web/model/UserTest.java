package chookin.chubot.web.model;

import org.junit.Test;

/**
 * Created by zhuyin on 9/15/15.
 */
public class UserTest {

    @Test
    public void testCrpytoPassword() throws Exception {
        String pwd = "admin";
        String crpt = User.crpytoPassword(pwd);
        System.out.println(crpt);
    }
}