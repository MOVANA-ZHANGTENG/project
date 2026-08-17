package com.deer.wcs.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by guo on 2019-12-13.
 */
public class DateUtil {
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";



    /**
     * 获取当前日期并以字符串
     *
     * @return
     */
    public static String getNowDateString() {
        Date curdate = new Date();
        return dateToStr(curdate, "yyyy-MM-dd");
    }

    /**
     * 将字符串时间改成Date类型
     */
    public static Date formatDate(Date date, String format) {
        Date newDate;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            newDate = simpleDateFormat.parse(dateToStr(date, format));
        } catch (ParseException e) {
            newDate = now(format);
            e.printStackTrace();
        }
        return newDate;
    }

    /**
     * 将字符串时间改成Date类型
     */
    public static Date strToDate(String dateStr, String format) {
        if (format == null) {
            format = DEFAULT_DATETIME_FORMAT;
        }
        Date date = null;
        try {
            if (!StringUtils.isEmpty(dateStr)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                date = simpleDateFormat.parse(dateStr);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /***
     * 计算两个日期相差多少秒
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getTimeDelta(Date date1, Date date2) {
        date1 = date1 == null ? new Date() : date1;
        date2 = date2 == null ? new Date() : date2;
        long timeDelta = (date1.getTime() - date2.getTime()) / 1000; //单位是秒
        int secondsDelta = timeDelta > 0 ? (int) timeDelta : (int) Math.abs(timeDelta);
        return secondsDelta;
    }

    /**
     * 获取现在的时间
     *
     * @return Date
     */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取现在的时间并以指定格式输出
     *
     * @param format 时间格式
     * @return Date
     */
    public static Date now(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return strToDate(formatter.format(now()), format);
    }

    /**
     * 在指定日期上加指定的天数
     *
     * @param date 基础日期
     * @param day  天数
     * @return 增加天数后的日期
     */
    public static Date addDay(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /**
     * 在指定日期上加指定的天fen数
     *
     * @param date 基础日期
     * @param fen  fen数
     * @return 增加天数后的fen数
     */
    public static Date addFen(Date date, int fen) {
        date.setTime(date.getTime() + 60*1000*fen);
        return date;
    }

    public static Date getDateAfterHour(Date date, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hour);
        return c.getTime();
    }
    
    public static Date getDateAfterSec(Date date, Integer sec) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, sec);
        return c.getTime();
    }

    /**
     * 获取当前时间并以字符串
     *
     * @return
     */
    public static String getNowDateTimeString() {
        Date curdate = new Date();
        return dateToStr(curdate, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getNowDateTimeStringTZ() {
        Date curdate = new Date();
        return dateToStr(curdate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getDateTimeStringTZ(Date date) {
        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println("GMT time: " + sdf.format(date));
        return sdf.format(date);
    }

    public static String getDateTimeStringTZ(String date) {
        Date date1 = strToDate(date,null);
        final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        System.out.println("GMT time: " + sdf.format(date1));
        return sdf.format(date1);
    }

    public static String dateToStr(Date date, String format) {
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat(format == null || "".equals(format) ? DEFAULT_DATETIME_FORMAT : format);
        String str = simpleDateFormat.format(date);
        return str;
    }

    public static String dateToStr2(String format) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = inputFormat.parse(format);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String outputDate = outputFormat.format(date);
        return outputDate;

    }

    public static Long getNowTimestamp() {
        return (new Date()).getTime();
    }

    public static int getWeekOfYear() {
        return getWeekOfYearOfDate(new Date());
    }
    
    public static int getWeekOfYearOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取指定日期所在周的 周 几的 日期
     *
     * @param weekDay 1 为周日
     * @param date
     * @return
     */
    public static Date getDateOfWeekDayOfThisWeek(Integer weekDay, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int w = calendar.get(Calendar.DAY_OF_WEEK);
        int del = weekDay - w;
        return addDay(date, del);
    }

    public static int validateProductionBatch(String batch1, String batch2) {

        int year1 = Integer.parseInt(batch1.substring(0, 2));
        int year2 = Integer.parseInt(batch2.substring(0, 2));
        int week1 = Integer.parseInt(batch1.substring(3, 5));
        int week2 = Integer.parseInt(batch2.substring(3, 5));

//        System.out.println(year1+":"+year2+":"+week1+":"+week2);

        if (year1 < year2) {
            return -1;
        } else if (year1 > year2) {
            return 1;
        } else {
            if (week1 < week2) {
                return -1;
            } else if (week1 > week2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static int validateProductionBatch(String batch) {
        int nowYear = Integer.parseInt(getNowDateTimeString().substring(2, 4));
        int batchYear = Integer.parseInt(batch.substring(0, 2));
        int nowWeek = getWeekOfYear();
        int batchWeek = Integer.parseInt(batch.substring(3, 5));

        if (nowYear > batchYear) {
            return -1;
        } else if (nowYear < batchYear) {
            return 1;
        } else {
            if (nowWeek > batchWeek) {
                return -1;
            } else if (nowWeek < batchWeek) {
                return 1;
            } else {
                return 0;
            }
        }
    }



    /**
     * 计算给定时间字符串与当前时间相差的秒数
     * @param timeStr 时间字符串，格式为 "yyyy-MM-dd HH:mm:ss"
     * @return 相差的秒数
     */
    public static long getTimeDifferenceInSeconds(String timeStr) {
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 将字符串解析为 LocalDateTime 对象
        LocalDateTime givenTime = LocalDateTime.parse(timeStr, formatter);
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();

        // 将 LocalDateTime 转换为 Date 对象
        Date givenDate = Date.from(givenTime.atZone(ZoneId.systemDefault()).toInstant());
        Date currentDate = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());

        // 计算时间差（毫秒）
        long differenceInMillis = currentDate.getTime() - givenDate.getTime();
        // 将毫秒转换为秒
        return differenceInMillis / 1000;
    }

    /**
     * 计算连个日期之间相差的天数
     */

    public static long testBetweenDays(String dateStr1, String dateStr2) {


        // 获取日期
        Date date1 = strToDate(dateStr1, "yyyy-MM-dd");
        Date date2 = strToDate(dateStr2, "yyyy-MM-dd");

        // 获取相差的天数
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        long timeInMillis1 = calendar.getTimeInMillis();
        calendar.setTime(date2);
        long timeInMillis2 = calendar.getTimeInMillis();

        long betweenDays = (timeInMillis2 - timeInMillis1) / (1000L * 3600L * 24L);
        return betweenDays;
    }

    /**
     * 获取与当天的时间差
     */
    public static long getNowBetweenDate(String dateStr) {
        Date now = new Date();
        String nowStr = dateToStr(now, "yyyy-MM-dd");
        return testBetweenDays(dateStr, nowStr);
    }

    /**
     * 获取与当天的时间差(传入的值是未来的日期)
     */
    public static long getNowBetweenDate2(String dateStr) {
        Date now = new Date();
        String nowStr = dateToStr(now, "yyyy-MM-dd");
        return testBetweenDays(nowStr, dateStr);
    }

    public static Boolean isToday(String dateStr) {
        Date date = strToDate(dateStr, null);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar todayCalendar = Calendar.getInstance();

        if (calendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH)) {
            return true;
        }
        return false;
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String getDateFromBatchStr(String batch) {
        try {
            int year = Integer.parseInt("20" + batch.substring(0, 2));
            int week = Integer.parseInt(batch.substring(3, 5));

            Calendar  cal = Calendar.getInstance();
            cal.set(Calendar.WEEK_OF_YEAR, week);
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.DAY_OF_WEEK, 4);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            return sdf.format(cal.getTime());

        } catch (Exception ex) {
            return null;
        }
    }

    //比较时间大小的 time1-2是固定的时间7:50~8:00 time3是当前时间
    public static boolean compare(String time1,String time2,String time3){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date a = null;
        Date b = null;
        Date c = null;
        try {
            a = sdf.parse(time1);
            b = sdf.parse(time2);
            c = sdf.parse(time3);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(a.getTime()-c.getTime()>0 || b.getTime()-c.getTime()<0){
            return true;
        }
        else{
            return false;
        }
    }

    // 定义年月日格式的格式化器（适配20251220这种格式）
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 计算两个年月日格式字符串的时间差（分钟）
     * @param dateStr1 第一个日期字符串（格式：yyyyMMdd，如20251220）
     * @param dateStr2 第二个日期字符串（格式：yyyyMMdd，如20251219）
     * @return 两个日期的时间差（分钟），dateStr1 - dateStr2的结果，正数表示dateStr1更晚
     * @throws IllegalArgumentException 日期格式错误时抛出异常
     */
    public static long calculateMinutesDifference(String dateStr1, String dateStr2) {
        try {
            // 1. 将字符串解析为LocalDateTime对象（包含时分秒）
            LocalDateTime datetime1 = LocalDateTime.parse(dateStr1, DATE_FORMATTER);
            LocalDateTime datetime2 = LocalDateTime.parse(dateStr2, DATE_FORMATTER);

            // 2. 直接计算两个时间的分钟差（ChronoUnit.MINUTES会自动处理天/时/分的换算）
            return ChronoUnit.MINUTES.between(datetime1, datetime2);

        } catch (Exception e) {
            // 捕获解析异常，抛出友好的提示
            throw new IllegalArgumentException("日期格式错误！请确保输入格式为yyyy-MM-dd HH:mm:ss，错误信息：" + e.getMessage(), e);
        }
    }

    public static void main(String[] args){
        // 测试用例1：同一天
        // 测试用例3：跨月（12月20日 - 11月30日 = 20天）
        String date5 = "2025-11-30 11:11:11";
        String date6 = "2025-11-30 12:12:12";
        System.out.println(date5 + " 和 " + date6 + " 的分钟差：" + calculateMinutesDifference(date5, date6));


    }
}
