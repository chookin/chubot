package cmri.utils.captcha;

import cmri.utils.lang.RandomHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Created by zhuyin on 10/29/15.
 */
public class AlphanumCatcha extends CaptchaGenerator {
    /**
     * 随机码生成字典
     */
    private static final String alphanumBase = "3456789ABCDEFGHJKMNPQRSTUVWXY";

    public AlphanumCatcha(int codeNumber, int width, int height, int fontSize) {
        super(codeNumber, width, height, fontSize);
    }

    String generateCode() {
        return RandomHelper.rand(alphanumBase, codeNumber);
    }

    void drawGraphic(BufferedImage image) {
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
            String rand = String.valueOf(code.charAt(i));
            // 将认证码显示到图象中
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            // 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
            g.drawString(rand, 16 * i + 6, 21);
        }
        // 图象生效
        g.dispose();
    }
}