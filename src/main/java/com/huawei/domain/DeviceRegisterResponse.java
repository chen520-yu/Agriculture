package com.huawei.domain;


import lombok.Data;

@Data
public class DeviceRegisterResponse {

    private String device_id;

    private String secret;
}
