package com.huawei.controller;

import com.huawei.domain.*;
import com.huawei.service.CreateDeviceCommand;
import com.huawei.service.SubscribeDataChg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SetCustomData {
    @Autowired
    private SubscribeDataChg subscribeDataChange;
    @Autowired
    private CreateDeviceCommand createDeviceCommand;


    @PostMapping("/set-paras")
    public ResponseEntity<Void> setPara(@RequestBody ParameterConfiguration value) throws Exception {

        TempDatabase.paras.setApp_id(value.getApp_id());
        TempDatabase.paras.setDomain(value.getDomain());
        TempDatabase.paras.setName(value.getName());
        TempDatabase.paras.setPassword(value.getPassword());
        TempDatabase.paras.setProduct_id(value.getProduct_id());
        TempDatabase.paras.setAk(value.getAk());
        TempDatabase.paras.setSk(value.getSk());
        TempDatabase.paras.setProjectId(value.getProjectId());
        TempDatabase.paras.setStreamName(value.getStreamName());
        TempDatabase.paras.setCallbackIp(value.getCallbackIp());
        TempDatabase.paras.setDataMode(value.getDataMode());
        TempDatabase.paras.setTopicUrn(value.getTopicUrn());

        if (TempDatabase.paras.getDataMode().equals("subscribe")) {
            subscribeDataChange.subscribeDataChange();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-paras")
    public ResponseEntity<ParameterConfiguration> getPara() {
        return new ResponseEntity<>(TempDatabase.paras, HttpStatus.OK);
    }

    @PostMapping("/set-history-data")
    public ResponseEntity<Void> setHisData(@RequestBody HistoryData value) throws Exception {

//        三个参数
        TempDatabase.hisData.setTempture(value.getTempture());
        TempDatabase.hisData.setHumidity(value.getHumidity());
        TempDatabase.hisData.setLuminance(value.getLuminance());

        TempDatabase.hisData.setMotorState(value.getMotorState());

        TempDatabase.hisData.setYdata(value.getYdata());
        TempDatabase.hisData.setLineData(value.getLineData());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get-history-data")
    public ResponseEntity<HistoryData> getHisData() {
        return new ResponseEntity<>(TempDatabase.hisData, HttpStatus.OK);
    }

    @GetMapping("/get-device-id")
    public ResponseEntity<String> getDeviceId() {
        return new ResponseEntity<>(TempDatabase.deviceId, HttpStatus.OK);
    }
}
