package com.huawei.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Data
public class  ApiError {

    private String status;

    private String errCode;

    private String msg;

}
