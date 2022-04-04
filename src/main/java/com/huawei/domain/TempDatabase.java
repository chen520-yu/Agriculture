package com.huawei.domain;

import java.util.concurrent.ConcurrentHashMap;

public class TempDatabase {

    public static int threshold=0;

//    token密钥
    public static String accessToken;

    public static String tokenExpire;

    public static ParameterConfiguration paras=new ParameterConfiguration();

    public static HistoryData hisData = new HistoryData();

    public static ConcurrentHashMap<String, Device> devices=new ConcurrentHashMap<>();

    public static boolean onOffSetter;

    public static TimeRange timeRange = new TimeRange();

    public static String ctrlMode = "null";

    public static AgricultureMsg slMsg = new AgricultureMsg();

    public static String deviceId;
}
