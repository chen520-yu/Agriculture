package com.huawei.domain;

import lombok.Data;

@Data
public class HistoryData {

    private String humidity;
    private String tempture;
    private String luminance;
    private String MotorState;

//    ??
    private String ydata;
    private String lineData;
}
