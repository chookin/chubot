package cmri.utils.lang;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhuyin on 7/30/14.
 */
public class TimeHelper {
    public static final long DAY_MILLISECONDS = 1000L * 3600 * 24;
    public static final long WEEK_MILLISECONDS = DAY_MILLISECONDS * 7;
    public static final long MONTH_MILLISECONDS = DAY_MILLISECONDS * 30;
    public static final long YEAR_MILLISECONDS = DAY_MILLISECONDS * 365;

    public static Calendar parseCalendar(String strDate, String dateformat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseDate(strDate, dateformat));
        return calendar;
    }

    /**
     * 解析时间
     *
     * @param dateDesc 日期时间描述,如:2009-03-22, 2009年8月1日 22:13:01, 18天前, 1个月前
     * @return 解析到的日期
     */
    public static Date parseDate(String dateDesc) {
        if(StringUtils.isBlank(dateDesc)){
            return null;
        }
        dateDesc = dateDesc.trim();
        String str = StringHelper.parseRegex(dateDesc, "(\\d+-\\d+-\\d+ \\d+:\\d+:\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y-M-d H:m:s");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+-\\d+-\\d+ \\d+:\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y-M-d H:m");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+-\\d+-\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y-M-d");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+\\.\\d+\\.\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y.M.d");
        }

        str = StringHelper.parseRegex(dateDesc, "(\\d+年\\d+月\\d+日 \\d+:\\d+:\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y年M月d日 H:m:s");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+年\\d+月\\d+日 \\d+:\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y年M月d日 H:m");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+年\\d+月\\d+)", 1);
        if (str != null) {
            return parseDate(str, "y年M月d");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+月\\d+)", 1);
        if (str != null) { // 不含年份信息
            str = LocalDateTime.now().getYear() + "年" + str;
            return parseDate(str, "y年M月d");
        }

        str = StringHelper.parseRegex(dateDesc, "(\\d+:\\d+:\\d+)", 1);
        if (str != null) { // 不含日期信息
            str = toString(new Date(), "yyyy-MM-dd ") + str;
            return parseDate(str, "yyyy-MM-dd H:m:s");
        }
        str = StringHelper.parseRegex(dateDesc, "(\\d+:\\d+)", 1);
        if (str != null) { // 不含日期信息
            str = toString(new Date(), "yyyy-MM-dd ") + str;
            return parseDate(str, "yyyy-MM-dd H:m");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime me = LocalDateTime.now();
        if(dateDesc.equals("昨天")){
            me = now.plusDays(-1);
        }else {
            str = StringHelper.parseRegex(dateDesc, "(\\d+)", 1);
            if(str != null) {
                int diff = Integer.parseInt(str);

                if (dateDesc.contains("天前")) {
                    me = now.plusDays(-diff);
                } else if (dateDesc.contains("月前")) {
                    me = now.plusMonths(-diff);
                } else if (dateDesc.contains("年前")) {
                    me = now.plusYears(-diff);
                }
            }else{
                throw new IllegalArgumentException( "cannot parse date from '" + dateDesc + "'");
            }
        }
        return Date.from(me.toInstant(ZoneOffset.UTC));
    }

    /**
     * G 年代标志符
     * y 年
     * M 月
     * d 日
     * h 时 在上午或下午 (1~12)
     * H 时 在一天中 (0~23)
     * m 分
     * s 秒
     * S 毫秒
     * E 星期
     * D 一年中的第几天
     * F 一月中第几个星期几
     * w 一年中第几个星期
     * W 一月中第几个星期
     * a 上午 / 下午 标记符
     * k 时 在一天中 (1~24)
     * K 时 在上午或下午 (0~11)
     * z 时区
     * yyyy-MM-dd H:m:s
     */
    public static Date parseDate(String str, String dateformat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new IllegalArgumentException( "cannot convert '" + str + "' to date of dateformat '"+ dateformat + "'", e);
        }
    }

    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return isWeekend(calendar);
    }

    public static boolean isWeekend(Calendar calendar) {
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekDay == 1 || weekDay == 7) {//SUNDAY, SATURDAY
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取上一个工作日是哪一天
     * @param calendar 计算哪一时刻的上一工作日
     * @return 上一个工作日
     */
    public static Calendar getPrevWorkDay(Calendar calendar) {
        Calendar curDay = (Calendar) calendar.clone();
        do {
            curDay.add(Calendar.DAY_OF_YEAR, -1);
        } while (isWeekend(curDay));// 如果是周末,继续往前查,直到是工作日的
        return curDay;
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1; // 在格里高利历和罗马儒略历中一年中的第一个月是 JANUARY，它为 0
    }

    public static int getCurrentQuarter() {
        return (getCurrentMonth() + 2) / 3;
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String toString(Date date) {
        return toString(date, "yyyy-MM-dd HH:mm:ss");
    }
    public static String toString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
