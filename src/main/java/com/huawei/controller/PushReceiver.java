//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.huawei.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;
import com.huawei.service.CreateDeviceCommand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PushReceiver {
    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @PostMapping("/v1.0.0/messageReceiver")
    public ResponseEntity pushrReceiver(@RequestBody String jsonString) throws Exception {

        System.out.println("Received message:" + jsonString);
        JSONObject deviceData = JSONObject.parseObject(jsonString);
        if ("device.data".equals(deviceData.getString("resource"))) {
            if (TempDatabase.paras.getDataMode().equals("subscribe")) {
                JSONObject notify_data = deviceData.getJSONObject("notify_data");
                System.out.println("Get notify_data");
                //if(TempDatabase.deviceId.equals(notify_data.getString("device_id"))){
                JSONArray services = notify_data.getJSONArray("services");
                for (int i = 0; i < services.size(); i++) {
                    JSONObject service = services.getJSONObject(i);
                    if ("Sensor".equals(service.getString("service_id"))) {

//                        获取消息体中的上报数据
                        JSONObject data = service.getJSONObject("data");
                        System.out.println("data:" + data.toString());
                        String luminance = data.getString("luminance");
                        String light_state = data.getString("light_state");
                        String motor_state = data.getString("motor_state");
                        String tempture = data.getString("tempture");
                        String humidity = data.getString("humidity");


                        String eventTime = service.getString("event_time");


                        if (data.containsKey("luminance")) {
                            AgricultureMsg slMsg = new AgricultureMsg(eventTime,tempture,humidity,luminance,light_state,motor_state);
                            TempDatabase.slMsg = slMsg;
                        }
                    }
                }
                //}
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/get-message")
    public void getslMsg() throws Exception {
        AgricultureMsg slMsg = TempDatabase.slMsg;

        createDeviceCommand.runSetCommand(slMsg);
    }

}

