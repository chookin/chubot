package cmri.utils.lang;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhuyin on 3/12/15.
 */
public class TimeHelperTest extends TestCase {
    public void testConvertToDateString() {
        String str = TimeHelper.toString(new Date(1), "yyyyMMddHHmmss.SSS");
        assertEquals("19700101080000.001", str);
    }

    @Test
    public void testConvertToUTC() {
        long epochMilli = System.currentTimeMillis();
        Instant instant = Instant.ofEpochMilli(epochMilli);
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        ZonedDateTime now = instant.atZone(zoneId);
        ZonedDateTime utc = now.withZoneSameInstant(ZoneOffset.UTC);
        System.out.println("now: " + now);
        System.out.println("utc: " + utc);
        System.out.println(epochMilli);
        System.out.println(Date.from(utc.toInstant()).getTime());
    }

    @Test
    public void testGetPrevDate(){
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 500);
        System.out.println(TimeHelper.toString(now));
        Date prev = calendar.getTime();
        System.out.println(TimeHelper.toString(prev));
        // 测试发现,prev的年份是明年的,因此不能使用该方法计算跨年的
    }

    @Test
    public void testParseDate() throws Exception {
        String str = "2015年3月10日";
        String patten = "y年M月d日";
        Date date = TimeHelper.parseDate(str, patten);
        String dst = TimeHelper.toString(date, patten);
        Assert.assertEquals(str, dst);
    }
}
