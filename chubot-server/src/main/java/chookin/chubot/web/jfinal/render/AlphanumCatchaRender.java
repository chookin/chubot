package chookin.chubot.web.jfinal.render;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码Render，这个验证码Render在构造函数里就已经创建好了随机码以及md5散列后的随机码。
 * 调用方式如下：
 * CaptchaRender captchaRender = new CaptchaRender();
 * String cryptoCode = captchaRender.getCryptoCode();
 * 保存md5RandonCode到session、cookie或者其他地方
 * render(captchaRender);
 * 基于JFinal的版本修改。
 *
 * http://my.oschina.net/myaniu/blog/142935
 */
public class AlphanumCatchaRender extends CaptchaRender{
    /**
     * 随机码生成字典
     */
    private static final String[] alphanumBase = {"3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y"};

    public AlphanumCatchaRender(int codeNumber, int width, int height, int fontSize) {
        super(codeNumber, width, height, fontSize);
    }

    protected String generateCode(){
        // 生成随机类
        Random random = new Random();
        String sRand = "";
        for (int i = 0; i < codeNumber; i++) {
            String rand = String.valueOf(alphanumBase[random.nextInt(alphanumBase.length)]);
            sRand += rand;
        }
        return sRand;
    }

    protected void drawGraphic(BufferedImage image){
        // 获取图形上下文
        Graphics g = image.createGraphics();
        // 生成随机类
        Random random = new Random();
        // 设定背景色
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        // 设定字体
        g.setFont(new Font("Times New Roman", Font.PLAIN, fontSize));

        // 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        //取随机产生的认证码(img_randNumber位数字)
        for (int i = 0; i < codeNumber; i++) {
            String rand = String.valueOf(this.code.charAt(i));
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(rand, 16 * i + 6, 21);
        }
        // 图象生效
        g.dispose();
    }
}
