package cmri.utils.lang;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuyin on 7/6/14.
 */
public final class StringHelper {

    /**
     * http://www.jb51.net/tools/zhengze.html
     * <p>
     * 常用的元字符
     * -------------
     * 代码	说明
     * *    重复零次或更多次
     * +	重复一次或更多次
     * ?	重复零次或一次
     * {n}	重复n次
     * {n,}	重复n次或更多次
     * {n,m}	重复n到m次
     * <p>
     * .	匹配除换行符以外的任意字符
     * \w	匹配字母或数字或下划线或汉字
     * \s	匹配任意的空白符,包括空格，制表符(Tab)，换行符，中文全角空格等
     * \d	匹配数字
     * \b	匹配单词的开始或结束
     * ^	匹配字符串的开始
     * $	匹配字符串的结束
     * <p>
     * \W	匹配任意不是字母，数字，下划线，汉字的字符
     * \S	匹配任意不是空白符的字符
     * \D	匹配任意非数字的字符
     * \B	匹配不是单词开头或结束的位置
     * [^x]	匹配除了x以外的任意字符
     * [^aeiou]	匹配除了aeiou这几个字母以外的任意字符
     * [0-9] 指定一个范围,代表的含意与\d就是完全一致的：一位数字
     * \  使用字符'\'转义特殊字符
     * <p>
     * <br>
     * 示例:{@code regex}为"W(or)(ld!)",那么 group为0是整个匹配;group为1是得到第一组匹配,即与(or)匹配的;group为2是得到第二组匹配,即与(ld!)匹配的
     *
     * @return the first matched item.
     */
    public static String parseRegex(String str, String regex, int group) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(str);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }

    /**
     * @return all the matched items.
     */
    public static List<String> parseCollectionByRegex(String str, String regex, int group) {
        List<String> regions = new ArrayList<>();
        if (str == null) {
            return regions;
        }
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while (m.find()) {
            regions.add(m.group(group));
        }
        return regions;
    }

    /**
     * 解析http cookie字符串,如'RK=CSWyS2NdfZ; pgv_pvi=4184944640; uid=25493040; pt2gguin=o0469308668; ptisp=cm; ptcz=0802eaad41f9d8893e3ee158457764b1c187a0447079210aa1e80db84c759b10; isVideo_DC=0; piao_city=10; pgv_info=ssid=s6632480880; pgv_pvid=2620759212; o_cookie=469308668'
     */
    public static Map<String, String> parseHttpRequestCookie(String str) {
        /**
         * {@code ( ?<!exp) },零宽度负回顾后发断言,断言此位置的前面不能匹配表达式exp, 例如(?<![a-z])\d{7}匹配前面不是小写字母的七位数字
         */
        String regexSep = "(?<!\\\\); "; // 前面不是"\\"的"; "为分隔符(注意：分号后面有空格, 因为有特殊情况, 如Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8)
        return parseStringStringMap(str, regexSep);
    }

    /**
     * 解析http header字符串
     */
    public static Map<String, String> parseHttpRequestHeader(String str) {
        String regexSep = "(?<!\\\\); ";
        return parseStringStringMap(str, regexSep);
    }

    /**
     * 解析http request data字符串
     */
    public static Map<String, String> parseHttpRequestData(String str) {
        String regexSep = "(?<!\\\\)&";
        return parseStringStringMap(str, regexSep);
    }

    public static Map<String, String> parseStringStringMap(String str, String regexSep) {
        if (StringUtils.isEmpty(str)) {
            return new HashMap<>();
        }
        Map<String, String> map = new TreeMap<>();
        String[] arr = str.split(regexSep);
        for (String item : arr) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            String[] kv = item.split("(?<!\\\\): ", 2);
            if (kv.length != 2) {
                kv = item.split("(?<!\\\\)=", 2);
                if (kv.length < 2) {
                    continue;
                }
            }
            String key = kv[0].trim().replace("\\;", ";").replace("\\=", "=");
            String val = kv[1].trim().replace("\\;", ";").replace("\\=", "=");
            map.put(key, val);
        }
        return map;
    }

    /**
     * convert ISO-8859-1 Latin-1 to UTF-8
     *
     * @param str the string contains Latin-1 character
     * @return convert result
     */
    public static String convertHtmlLatin2UTF8(String str) {
        String regex = "&#([\\d]+);";
        String out = str;
        List<String> regions = parseCollectionByRegex(str, regex, 0);
        for (String region : regions) {
            String key = parseRegex(region, regex, 1);
            int val = Integer.parseInt(key);
            char ch = (char) val;
            out = out.replace(region, String.valueOf(ch));
        }
        return out;
    }

    public static String join(Map<String, String> obj, String kvSep, String pairSep) {
        Validate.notNull(kvSep, "kvSep");
        Validate.notNull(pairSep, "pairSep");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> cookie : obj.entrySet()) {
            if (!first)
                sb.append(pairSep);
            else
                first = false;
            sb.append(cookie.getKey()).append(kvSep).append(cookie.getValue());
        }
        return sb.toString();
    }

    public static <T> String join(Collection<T> items, String sep) {
        Validate.notNull(items, "items");
        Validate.notNull(sep, "sep");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T item : items) {
            if (!first)
                sb.append(sep);
            else
                first = false;
            sb.append(item.toString());
        }
        return sb.toString();
    }
}
