package com.huawei.controller;

import com.huawei.domain.DeviceRegisterResponse;
import com.huawei.domain.DeviceRegisterVerifyCode;
import com.huawei.domain.TempDatabase;
import com.huawei.service.RegisterDirectConnectedDevice;
import com.huawei.utils.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegisterDevice {

    @Autowired
    private RegisterDirectConnectedDevice registerDirectConnectedDevice;

    @PostMapping("/register-device")
    public ResponseEntity<DeviceRegisterResponse> registerDevice(
        @RequestBody DeviceRegisterVerifyCode deviceRegisterVerifyCode) throws Exception {

        DeviceRegisterResponse response = registerDirectConnectedDevice.registerDirectDevice(deviceRegisterVerifyCode);

/*        //测试用
        DeviceRegisterResponse response = new DeviceRegisterResponse();
        response.setDevice_id("123456");
        response.setSecret("123456");*/

        if (StringUtil.strIsNullOrEmpty(response.getDevice_id())) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        TempDatabase.deviceId = response.getDevice_id();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
