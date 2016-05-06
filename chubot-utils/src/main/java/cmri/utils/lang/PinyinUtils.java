package cmri.utils.lang;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.log4j.Logger;

import java.util.Objects;

/**
 * refer: http://noobjava.iteye.com/blog/855811
 */
public class PinyinUtils {
    private static final Logger LOG = Logger.getLogger(PinyinUtils.class);
    /**
     * 将汉字转换为全拼
     *
     * @param src 汉字字符串
     * @param sep 分隔符
     * @return 将汉字转为其拼音后的字符串
     */
    public static String getPinYin(String src, String sep) {
        char[] t1 = src.toCharArray();
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        StringBuilder strb = new StringBuilder();
        try {
            for (char aT1 : t1) {
                // 判断是否为汉字字符
                // System.out.println(t1[i]);
                if (Character.toString(aT1).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] t2 = PinyinHelper.toHanyuPinyinStringArray(aT1, t3);// 将汉字的几种全拼都存到t2数组中
                    appendSep(strb, sep);
                    strb.append(t2[0]); // 取出该汉字全拼的第一种读音并连接到字符串t4后
                } else {
                    // 如果不是汉字字符，直接取出字符并连接到字符串t4后
                    if(!Objects.equals(Character.toString(aT1), sep)){
                        appendSep(strb, sep);
                    }
                    strb.append(aT1);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            LOG.error(null, e);
        }
        return strb.toString();
    }
    private static void appendSep(StringBuilder strb, String sep){
        if(strb.lastIndexOf(sep) != strb.length() - sep.length()){ // if not ends with sep
            strb.append(sep);
        }
    }
    /**
     * 提取每个汉字的首字母
     * @param str 汉字字符串
     * @return 汉字拼音的首字母组合
     */
    public static String getPinYinHeadChar(String str) {
        StringBuilder strb = new StringBuilder();
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                strb.append(pinyinArray[0].charAt(0));
            } else {
                strb.append(word);
            }
        }
        return strb.toString();
    }

    /**
     * 将字符串转换成ASCII码
     *
     * @param cnStr 汉字字符串
     * @return ASCII码串
     */
    public static String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (byte aBGBK : bGBK) {
            // System.out.println(Integer.toHexString(bGBK[i] & 0xff));
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(aBGBK & 0xff)).append(" ");
        }
        return strBuf.toString();
    }

    public static void main(String[] args) {
        String cnStr = "中华人民共和国";
        System.out.println(getPinYin(cnStr, "-"));
        System.out.println(getPinYinHeadChar(cnStr));
        System.out.println(getCnASCII(cnStr));
    }
}
