package com.huawei.controller;

import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;
import com.huawei.domain.TimeRange;

import com.huawei.service.CreateDeviceCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeRangeCtrl {

    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @PostMapping("/set-time-range")
    public ResponseEntity<Void> setPara(@RequestBody TimeRange value) throws Exception {
        if (value != null) {
            TempDatabase.timeRange.setStartTime(value.getStartTime());
            TempDatabase.timeRange.setEndTime(value.getEndTime());
            //System.out.println(TempDatabase.timeRange.getStartTime()+"-"+TempDatabase.timeRange.getEndTime());
            TempDatabase.ctrlMode = "timeRange";
            AgricultureMsg slMsg = TempDatabase.slMsg;
            createDeviceCommand.runSetCommand(slMsg);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
