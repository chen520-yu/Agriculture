package com.huawei.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Time {
    public static boolean isInTimeMain(String startTime, String endTime, String dateStr) throws ParseException {

//        根据固定格式产生时间
        String timeStr = dateStr.split("T")[1].replace("Z", "");
        SimpleDateFormat FORMAT = new SimpleDateFormat("hhmmss");
        FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat NEW_FORMAT = new SimpleDateFormat("hh:mm");
        NEW_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+:08:00"));
        Date d = FORMAT.parse(timeStr);
        String newDateString = NEW_FORMAT.format(d);

        return isInTime(startTime + "-" + endTime, newDateString);
    }

    public static boolean isInTime(String sourceTime, String curTime) {
        if (sourceTime == null || !sourceTime.contains("-") || !sourceTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }
        if (curTime == null || !curTime.contains(":")) {
            throw new IllegalArgumentException("Illegal Argument arg:" + curTime);
        }
        String[] args = sourceTime.split("-");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            long now = sdf.parse(curTime).getTime();
            long start = sdf.parse(args[0]).getTime();
            long end = sdf.parse(args[1]).getTime();
            if (args[1].equals("00:00")) {
                args[1] = "24:00";
            }
            if (end < start) {
                if (now >= end && now < start) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (now >= start && now < end) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            throw new IllegalArgumentException("Illegal Argument arg:" + sourceTime);
        }

    }

}


