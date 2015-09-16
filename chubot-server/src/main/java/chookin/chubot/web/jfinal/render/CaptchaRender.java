package chookin.chubot.web.jfinal.render;

import com.jfinal.render.Render;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;


public abstract class CaptchaRender extends Render {
    private static final long serialVersionUID = -7599510915228560611L;

    /**
     * 默认存储时使用的key,将md5散列后的随机码保存至session，cookie时使用。
     */
    public static final String DEFAULT_CAPTCHA_MD5_CODE_KEY = "_CAPTCHA_MD5_CODE_";

    /**
     * 图片宽度
     */
    protected final int width;

    /**
     * 图片高度
     */
    protected final int height;

    protected final int fontSize;
    /**
     * 字符数量
     */
    protected final int codeNumber;

    /**
     * 生成的随机码
     */
    protected final String code;

    /**
     * @param codeNumber 随机生成多少个字符,最少4个字符。
     */
    public CaptchaRender(int codeNumber) {
        this(codeNumber, 16* codeNumber + 12, 26, 20);
    }

    public CaptchaRender(int codeNumber, int width, int height, int fontSize){
        if(codeNumber < 4)
        {
            codeNumber = 4;
        }
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.codeNumber = codeNumber;
        this.code = generateCode();
    }
    public String getCode(){
        return this.code;
    }

    /**
     * 依据字典生成随即码
     * @return 随机码
     */
    protected abstract String generateCode();
    /**
     * 绘制验证码
     * @param image BufferedImage对象
     */
    protected abstract void drawGraphic(BufferedImage image);
    /**
     * 渲染图片
     */
    public void render() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        drawGraphic(image);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        ServletOutputStream sos = null;
        try {
            sos = response.getOutputStream();
            ImageIO.write(image, "jpeg",sos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if (sos != null)
                try {sos.close();} catch (IOException e) {e.printStackTrace();}
        }
    }
    protected Color getRandColor(int s,int e){
        Random random=new Random ();
        if(s>255) s=255;
        if(e>255) e=255;
        int r,g,b;
        r=s+random.nextInt(e-s);    //随机生成RGB颜色中的r值
        g=s+random.nextInt(e-s);    //随机生成RGB颜色中的g值
        b=s+random.nextInt(e-s);    //随机生成RGB颜色中的b值
        return new Color(r,g,b);
    }
}
