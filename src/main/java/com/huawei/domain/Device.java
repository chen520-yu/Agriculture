package com.huawei.domain;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Device {

    private int switchFrequency;
    private int toggle;
    private int luminance;
}
