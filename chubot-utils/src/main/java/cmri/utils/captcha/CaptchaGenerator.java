package cmri.utils.captcha;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * Created by zhuyin on 10/29/15.
 */
public abstract class CaptchaGenerator {
    protected final int width;
    protected final int height;
    protected final int fontSize;
    protected final int codeNumber;
    /**
     * 生成的随机码
     */
    protected final String code;
    private BufferedImage image;
    private String imageBase64;

    /**
     * @param codeNumber 随机生成多少个字符,最少4个字符。
     */
    public CaptchaGenerator(int codeNumber) {
        this(codeNumber, 16* codeNumber + 12, 26, 20);
    }

    public CaptchaGenerator(int codeNumber, int width, int height, int fontSize){
        if(codeNumber < 4){
            codeNumber = 4;
        }
        this.width = width;
        this.height = height;
        this.fontSize = fontSize;
        this.codeNumber = codeNumber;
        this.code = generateCode();
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.imageBase64 = encodeBase64(this.image);
    }
    public String getCode(){
        return this.code;
    }
    public BufferedImage getImage() {
        return this.image;
    }
    public String getImageBase64(){
       return imageBase64;
    }

    /**
     * 依据字典生成随即码
     * @return 随机码
     */
    abstract String generateCode();
    /**
     * 绘制验证码
     * @param image BufferedImage对象
     */
    abstract void drawGraphic(BufferedImage image);

    Color getRandColor(int s,int e){
        Random random=new Random ();
        if(s>255) s=255;
        if(e>255) e=255;
        int r,g,b;
        r=s+random.nextInt(e-s);    //随机生成RGB颜色中的r值
        g=s+random.nextInt(e-s);    //随机生成RGB颜色中的g值
        b=s+random.nextInt(e-s);    //随机生成RGB颜色中的b值
        return new Color(r,g,b);
    }

    String encodeBase64(BufferedImage image){
        drawGraphic(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
            return Base64.encodeBase64String(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("failed to generate captcha");
        }
    }
}
