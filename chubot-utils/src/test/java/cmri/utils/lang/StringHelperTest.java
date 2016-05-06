package cmri.utils.lang;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class StringHelperTest {

    @Test
    public void testParseStringStringMap() throws Exception {
        String str = "RK=CSWyS2NdfZ; pgv_pvi=4184944640; uid=25493040; pt2gguin=o0469308668; ptisp=cm; ptcz=0802eaad41f9d8893e3ee158457764b1c187a0447079210aa1e80db84c759b10; isVideo_DC=0; piao_city=10; pgv_info=ssid=s6632480880; pgv_pvid=2620759212; o_cookie=469308668";
        Map<String, String> out = StringHelper.parseHttpRequestCookie(str);
        System.out.println(out);

        str = "Referer=http://y.qq.com/; Accept-Language=zh-CN,zh;q=0.8,en;q=0.6,es;q=0.4";
        out = StringHelper.parseHttpRequestHeader(str);
        System.out.println(out);
        Assert.assertEquals("{Accept-Language=zh-CN,zh;q=0.8,en;q=0.6,es;q=0.4, Referer=http://y.qq.com/}", out.toString());
    }

    @Test
    public void testConvertHtmlLatin2UTF8() throws UnsupportedEncodingException {
        String str = "原来你的时间不只是我一个人。&#60;br&#62;故事的结局却让我增添多少伤痕？&#60;br&#62;有时候眼泪也就是 歪小九&#38;&#35;32&#59;看到花朵的绽放了么？你的身边有人并肩而行么？收到过去的来信了么？&#60;br&#62;这&#3606;&#3657;&#3634;&#3648;&#3608;&#3629;&#3617;&#3637;&#3592;&#3619;&#3636;&#3591;一片春色中";
        String out = StringHelper.convertHtmlLatin2UTF8(str);
        System.out.println(out);
    }

    @Test
    public void testAppendNull(){
        String str = "me";
        String dst = str + null;
        System.out.println(dst);
    }

    @Test
    public void testEscapeUnicode(){
        String str = "\\u6ce2\\u514b\\u68cb\\u724c";
        String out = StringEscapeUtils.unescapeJava(str);
        Assert.assertEquals("波克棋牌", out);
        System.out.println("out of escape unicode is " + out);
    }
}