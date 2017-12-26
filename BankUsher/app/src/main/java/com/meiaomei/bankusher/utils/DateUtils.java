package com.meiaomei.bankusher.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by meiaomei on 2017/3/13.
 */
public class DateUtils {

    public static final int MONTH_DAY = 30;
    public static final double MONTH_DAY_D = 30.4D;
    public static final long HOUR_SECOND = 3600L;
    public static final long MINUTE_MILLI = 60L;
    public static final String DATE_FORMAT_DATEONLY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SLASH_DATEONLY = "yyyy/MM/dd";
    public static final String DATE_FORMAT_SLASH_DATETIME = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_FORMAT_DATEHOURMIN = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_SLASH_DATEHOURMIN = "yyyy/MM/dd HH:mm";
    public static final String DATE_FORMAT_DATEONLY_NOCHAR = "yyyyMMdd";
    public static final String DATE_FORMAT_MONTH = "yyyyMM";

    public DateUtils() {
    }

    public static int daysBetween(Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String startstr = sdf.format(startTime);
        String endstr = sdf.format(endTime);
        Date start = null;
        Date end = null;

        try {
            start = sdf.parse(startstr);
            end = sdf.parse(endstr);
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return (new Long((end.getTime() - start.getTime()) / 86400000L)).intValue();
    }

    public static int daysBetween(long startTime, long endTime) {
        Date start = new Date(startTime);
        Date end = new Date(endTime);
        return daysBetween(start, end);
    }

    public static int daysBetween(String startTime, long endTime) {
        return daysBetween(string2Date(startTime).getTime(), endTime);
    }

    public static int daysBetween(String startTime, String endTime) {
        return daysBetween(string2Date(startTime).getTime(), string2Date(endTime).getTime());
    }

    public static int daysBetween(long startTime, String endTime) {
        return daysBetween(startTime, string2Date(endTime).getTime());
    }

    public static long string2Long(String time) {
        return string2Date(time).getTime();
    }

    public static int[] getTimeNameValue(Date date) {
        int[] arr = new int[5];
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        } else {
            calendar.setTime(date);
        }

        arr[0] = calendar.get(1);
        arr[1] = calendar.get(2);
        arr[2] = calendar.get(5);
        arr[3] = calendar.get(11);
        arr[4] = calendar.get(12);
        return arr;
    }

    public static int getYearRange(int year) {
        return Calendar.getInstance().get(1) + year;
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(7) - 1;
        if (w < 0) {
            w = 0;
        }

        return weekDays[w];
    }

    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(5, day);
        return calendar.getTime();
    }

    public static Date addTime(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(13, second);
        return calendar.getTime();
    }

    public static Date string2Date(String sDate) {
        Date dt = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sDate = sDate.replace("T", " ");
        if (sDate.contains("+")) {
            sDate = sDate.substring(0, sDate.lastIndexOf("+"));
        }
        try {
            if (sDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}")) {
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")) {
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{2}:\\d{2}")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{2}:\\d{2}:\\d{2}")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}/\\d{1,2}/\\d{1,2}")) {
                sdf = new SimpleDateFormat("yyyy/MM/dd");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}\\d{1,2}")) {
                sdf = new SimpleDateFormat("yyyyMM");
                dt = sdf.parse(sDate);
            } else if (sDate.matches("\\d{4}\\d{1,2}\\d{1,2}")) {
                sdf = new SimpleDateFormat("yyyyMMdd");
                dt = sdf.parse(sDate);
            }
        } catch (ParseException var4) {
            var4.printStackTrace();
            dt = null;
        }

        return dt;
    }

    public static String formatDate(Date date, String formatter) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        if (date == null) {
            return "";
        } else {
            String dt = null;

            try {
                dt = sdf.format(date);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

            return dt;
        }
    }

    public static String longFromatDate(long date, String formatter) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatter);
        if (date == 0L) {
            return "";
        } else {
            String dt = null;

            try {
                dt = sdf.format(Long.valueOf(date));
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            return dt;
        }
    }


    public static long longOfDateWithoutTime(Date d) {
        return d == null ? -1L : getDateWithOutTime(d).getTime();
    }

    public static long longOfDateWithoutTime(long d) {
        return d <= 0L ? -1L : longOfDateWithoutTime(new Date(d));
    }

    public static long longOfDateWithoutTime() {
        return getDateWithOutTime(new Date()).getTime();
    }

    private static Date getDateWithOutTime(Date dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String str = sdf.format(dt);
        Date date = null;

        try {
            date = sdf.parse(str);
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        return date;
    }

    public static Date getFirtDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, -1);
        calendar.set(5, 1);
        String day_first = formatDate(calendar.getTime(), "yyyy-MM-dd");
        StringBuffer str = (new StringBuffer()).append(day_first).append(" 00:00:00");
        return string2Date(str.toString());
    }

    public static Date getEndDayOfLastMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(5, 1);
        calendar.add(5, -1);
        String day_last = formatDate(calendar.getTime(), "yyyy-MM-dd");
        StringBuffer str = (new StringBuffer()).append(day_last).append(" 23:59:59");
        return string2Date(str.toString());
    }

    public static Date getFirtDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(5, 1);
        String day_first = formatDate(calendar.getTime(), "yyyy-MM-dd");
        StringBuffer str = (new StringBuffer()).append(day_first).append(" 00:00:00");
        return string2Date(str.toString());
    }

    public static Date getEndDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 1);
        calendar.set(5, 1);
        calendar.add(5, -1);
        String day_last = formatDate(calendar.getTime(), "yyyy-MM-dd");
        StringBuffer str = (new StringBuffer()).append(day_last).append(" 23:59:59");
        return string2Date(str.toString());
    }

    public static boolean isToday(Date d) {
        return longOfDateWithoutTime() == longOfDateWithoutTime(d);
    }

    /**
     * 根据只有时，分，秒的时间返回一个当前日期的Calendar对象
     *      * @param time 时间       08:00:00
     *      
     */
    public static Calendar getCalendarByTime(String time) {
        Calendar calendar = Calendar.getInstance();

        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));


        //设置日历的时间，主要是让日历的年月日和当前同步
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }

    /**
     * 根据只有时，分，秒的时间返回一个当前日期的Date对象
     *      * @param time 时间       08:00:00
     *      * @throws ParseException 
     *      
     */
    public static Date getDateByTime(String time) throws ParseException {
        Date nowDate = new Date();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strNowDate = sd.format(nowDate);
        String strDate = strNowDate.substring(0, 11) + time;
        Date date = sd.parse(strDate);
        return date;
    }


    public static boolean isIntime(String beginInTime, String endTime) {
        boolean flag = false;
        if ("".equals(beginInTime) && "".equals(endTime)) {
            return false;
        } else {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strDate = sd.format(date);

            String strDay = strDate.substring(0, 11);
            String strBeginDate = strDay + beginInTime;
            String strEndDate = strDay + endTime;

            try {
                Date beginDate = sd.parse(strBeginDate);
                Date endDate = sd.parse(strEndDate);
                //首先判断开始时间与结束时间是否跨天
                //当然，这里假设开始时间与结束时间的这个时间段是不超过24小时的
                if (beginDate.before(endDate)) {//不跨天

                    if (date.before(beginDate) || date.after(endDate)) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                } else {//跨天
                    //根据今天的日期获取明天的日期
                    int nowDate = calendar.get(Calendar.DAY_OF_YEAR);
                    calendar.set(Calendar.DAY_OF_YEAR, nowDate + 1);
                    Date nextDate = calendar.getTime();
                    String strNextDate = sd.format(nextDate);

                    //对结束日期进行处理
                    String strDayNextDate = strNextDate.substring(0, 11);
                    String strEndDateNew = strDayNextDate + endTime;
                    Date endDateNew = sd.parse(strEndDateNew);
                    if (date.before(beginDate) || date.after(endDateNew)) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return flag;
    }

    public static boolean isInsertPlayIntime(String beginInTime, String endTime) {
        boolean flag = false;
        if ("".equals(beginInTime) && "".equals(endTime)) {
            return true;
        } else {
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String strDate = sd.format(date);

            String strDay = strDate.substring(0, 11);
            String strBeginDate = strDay + beginInTime;
            String strEndDate = strDay + endTime;

            try {
                Date beginDate = sd.parse(strBeginDate);
                Date endDate = sd.parse(strEndDate);
                //首先判断开始时间与结束时间是否跨天
                //当然，这里假设开始时间与结束时间的这个时间段是不超过24小时的
                if (beginDate.before(endDate)) {//不跨天

                    if (date.before(beginDate) || date.after(endDate)) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                } else {//跨天
                    //根据今天的日期获取明天的日期
                    int nowDate = calendar.get(Calendar.DAY_OF_YEAR);
                    calendar.set(Calendar.DAY_OF_YEAR, nowDate + 1);
                    Date nextDate = calendar.getTime();
                    String strNextDate = sd.format(nextDate);

                    //对结束日期进行处理
                    String strDayNextDate = strNextDate.substring(0, 11);
                    String strEndDateNew = strDayNextDate + endTime;
                    Date endDateNew = sd.parse(strEndDateNew);
                    if (date.before(beginDate) || date.after(endDateNew)) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return flag;
    }

}
