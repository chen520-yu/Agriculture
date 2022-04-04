package com.huawei.controller;

import com.alibaba.fastjson.JSONObject;

import com.huawei.domain.Device;
import com.huawei.domain.TempDatabase;
import com.huawei.service.CreateDeviceCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeviceDataController {

    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @GetMapping("/get-data")
    public ResponseEntity<Device> getDeviceData() {
        return new ResponseEntity<>(TempDatabase.devices.get(TempDatabase.deviceId), HttpStatus.OK);
    }

//    @PostMapping("/set-command-threshold")
//    public ResponseEntity<Void> setDeviceCommandTh(@RequestBody String jsonString) throws Exception {
//        JSONObject command = JSONObject.parseObject(jsonString);
//        if ("on".equals(command.getString("power"))) {
//            createDeviceCommand.setCommand("ON");
//        } else if ("off".equals(command.getString("power"))) {
//            createDeviceCommand.setCommand("OFF");
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @PostMapping("/set-command")
    public ResponseEntity<Void> setDeviceCommand(@RequestBody String status) throws Exception {

        createDeviceCommand.setCommand(status);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
