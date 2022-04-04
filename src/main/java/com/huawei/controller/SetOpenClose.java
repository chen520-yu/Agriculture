package com.huawei.controller;

import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;

import com.huawei.service.CreateDeviceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SetOpenClose {

    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @PostMapping("/set-open-close")
    public ResponseEntity<Object> setPara(@RequestBody String onOff) throws Exception {
        if (onOff != null) {
            TempDatabase.ctrlMode = "terminal";

            TempDatabase.onOffSetter = onOff.equals("ON");

            AgricultureMsg slMsg = TempDatabase.slMsg;
            createDeviceCommand.runSetCommand(slMsg);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
