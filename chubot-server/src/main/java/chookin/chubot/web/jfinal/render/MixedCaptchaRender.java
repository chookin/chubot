package chookin.chubot.web.jfinal.render;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 随机生成一个指定位置的验证码,一般为四位,然后把该验证码保存到session中.在通过Java的绘图类以图片的形式输出该验证码。为了增加验证码的安全级别,可以输出图片的同时输出干扰线,最后在用户提交数据的时候,在服务器端将用户提交的验证码和Session保存的验证码进行比较.
 验证码所需的技术
 i.因为验证码中的文字,数字,应为都是可变的,故要用到随机生成数技术。
 ii.如果验证码中包含汉字,则要用到汉字生成技术.
 iii.可以使用Ajax技术实现局部刷新
 iv.可以使用图片的缩放和旋转技术,
 vi.随机绘制干扰线(可以是折现,直线等)
 vii.如果考虑到验证码的安全性,可以使用MD5加密.

 http://blog.csdn.net/lulei9876/article/details/8365500
 */
public class MixedCaptchaRender extends CaptchaRender {
    public MixedCaptchaRender(int codeNumber, int width, int height, int fontSize) {
        super(codeNumber, width, height, fontSize);
    }

    protected void drawGraphic(BufferedImage image){
        Graphics g=image.getGraphics();     //创建Graphics对象,其作用相当于画笔
        Graphics2D g2d=(Graphics2D)g;       //创建Grapchics2D对象
        Random random=new Random();
        Font mfont=new Font("楷体",Font.BOLD,fontSize); //定义字体样式
        g.setColor(getRandColor(200,250));
        g.fillRect(0, 0, width, height);    //绘制背景
        g.setFont(mfont);                   //设置字体
        g.setColor(getRandColor(180, 200));

        //绘制100条颜色和位置全部为随机产生的线条,该线条为2f
        for(int i=0;i<100;i++){
            int x=random.nextInt(width-1);
            int y=random.nextInt(height-1);
            int x1=random.nextInt(6)+1;
            int y1=random.nextInt(12)+1;
            BasicStroke bs=new BasicStroke(2f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); //定制线条样式
            Line2D line=new Line2D.Double(x,y,x+x1,y+y1);
            g2d.setStroke(bs);
            g2d.draw(line);     //绘制直线
        }
        for(int i=0;i<codeNumber;i++) {
            String rand = String.valueOf(this.code.charAt(i));
            Color color = new Color(20 + random.nextInt(110), 20 + random.nextInt(110), random.nextInt(110));
            g.setColor(color);
            //将生成的随机数进行随机缩放并旋转制定角度 PS.建议不要对文字进行缩放与旋转,因为这样图片可能不正常显示
            /*将文字旋转制定角度*/
            Graphics2D g2d_word = (Graphics2D) g;
            AffineTransform trans = new AffineTransform();
            trans.rotate((45) * 3.14 / 180, 15 * i + 8, 7);
            /*缩放文字*/
            float scaleSize = random.nextFloat() + 0.8f;
            if (scaleSize > 1f) scaleSize = 1f;
            trans.scale(scaleSize, scaleSize);
            g2d_word.setTransform(trans);
            g.drawString(rand, 15 * i + 18, 14);
        }
    }
    protected String generateCode(){
        //输出由英文，数字，和中文随机组成的验证文字，具体的组合方式根据生成随机数确定。
        String sRand="";
        String ctmp;
        int itmp;
        Random random=new Random(System.currentTimeMillis());
        for(int i=0;i<codeNumber;i++) {
            switch (random.nextInt(3)) {
                case 1:     //生成A-Z的字母
                    itmp = random.nextInt(26) + 65;
                    ctmp = String.valueOf((char) itmp);
                    break;
                case 2:     //生成汉字
                    String[] rBase={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
                    //生成第一位区码
                    int r1=random.nextInt(3)+11;
                    String str_r1=rBase[r1];
                    //生成第二位区码
                    int r2;
                    if(r1==13){
                        r2=random.nextInt(7);
                    }else{
                        r2=random.nextInt(16);
                    }
                    String str_r2=rBase[r2];
                    //生成第一位位码
                    int r3=random.nextInt(6)+10;
                    String str_r3=rBase[r3];
                    //生成第二位位码
                    int r4;
                    if(r3==10){
                        r4=random.nextInt(15)+1;
                    }else if(r3==15){
                        r4=random.nextInt(15);
                    }else{
                        r4=random.nextInt(16);
                    }
                    String str_r4=rBase[r4];
                    //将生成的机内码转换为汉字
                    byte[] bytes=new byte[2];
                    //将生成的区码保存到字节数组的第一个元素中
                    String str_12=str_r1+str_r2;
                    int tempLow=Integer.parseInt(str_12, 16);
                    bytes[0]=(byte) tempLow;
                    //将生成的位码保存到字节数组的第二个元素中
                    String str_34=str_r3+str_r4;
                    int tempHigh=Integer.parseInt(str_34, 16);
                    bytes[1]=(byte)tempHigh;
                    ctmp=new String(bytes);
                    break;
                default:
                    itmp = random.nextInt(10) + 48;
                    ctmp = String.valueOf((char) itmp);
                    break;
            }
            sRand += ctmp;
        }
        return sRand;
    }
}
