package com.mind.links.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * description : TODO
 *
 * @author : qiDing
 * date: 2020-10-29 11:22
 * @version v1.0.0
 */
public class LinksDateUtils {

    /*
     * java Add and subtract dates in
     * gc.add(1,-1) Represents the year minus one.
     * gc.add(2,-1)Represents the month minus minus one..
     * gc.add(3.-1)Means week minus one.
     * gc.add(5,-1)Means one day minus one.
     * ...
     * And so on, it should be accurate in milliseconds. No try again. You can try.
     * <p>
     * GregorianCalendar class add(int field,int amount)The method represents the year, month, and day addition and subtraction..
     * field The parameters represent year, month, day, etc.
     * amount The parameter indicates the amount to be added or subtracted.
     */

    /**
     * @param date   2020-10-01
     * @param field  1-7  year-month-week-day...
     * @param number add or lower
     */
    public static String dateOperation(String date, Integer field, Integer number) throws Exception {
        GregorianCalendar gc = new GregorianCalendar();
        try {
            gc.setTime(new SimpleDateFormat("yyyyMMdd").parse(date));
            gc.add(field, number);
        } catch (ParseException e) {
            throw new Exception("错误的日期格式");
        }
        return new SimpleDateFormat("yyyyMMdd").format(gc.getTime());
    }

    public static String dateFormat() throws Exception {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            throw new Exception("错误的日期格式");
        }
    }
    public static String dateFormat(Date date) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            throw new Exception("错误的日期格式");
        }
    }

}
