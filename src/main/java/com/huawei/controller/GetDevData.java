package com.huawei.controller;

import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;
import com.huawei.service.CreateDeviceCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetDevData {

    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @GetMapping("/get-device-data")
    public ResponseEntity<AgricultureMsg> getDeviceData() throws Exception {
        AgricultureMsg slMsg = TempDatabase.slMsg;
//        createDeviceCommand.runSetCommand(slMsg);
        return new ResponseEntity<>(TempDatabase.slMsg, HttpStatus.OK);
    }

}
