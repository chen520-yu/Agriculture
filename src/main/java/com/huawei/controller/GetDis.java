package com.huawei.controller;

import com.huawei.dis.DISUtil;
import com.huawei.dis.DescribeStream;
import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;
import com.huawei.service.CreateDeviceCommand;
import com.huawei.utils.ApiError;
import com.huawei.utils.StringUtil;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.iface.data.request.GetPartitionCursorRequest;
import com.huaweicloud.dis.iface.data.request.GetRecordsRequest;
import com.huaweicloud.dis.iface.data.response.GetPartitionCursorResult;
import com.huaweicloud.dis.iface.data.response.GetRecordsResult;
import com.huaweicloud.dis.iface.data.response.Record;
import com.huaweicloud.dis.util.PartitionCursorTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Level;


/**
 * Get records from DIS Example
 */

@RestController
public class GetDis {

    @Autowired
    private CreateDeviceCommand createDeviceCommand;

    @GetMapping("/get-dis")
    public ResponseEntity<Object> getDIsData() throws Exception {
        final Logger LOGGER = LoggerFactory.getLogger(GetDis.class);
        java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(Level.FINEST);
        java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(Level.FINEST);
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "ERROR");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.headers", "ERROR");
        // 创建DIS客户端实例
        DIS dic = DISUtil.getInstance();

        // 配置流名称
        String streamName = DISUtil.getStreamName();

        // 配置数据下载分区ID
        String partitionId = "0";

        // 配置下载数据序列号
        //获取最大的序列号-1的数据
        String startingSequenceNumber = String.valueOf(DescribeStream.getMaxIndex());

        // 配置下载数据方式
        // AT_SEQUENCE_NUMBER 从指定的sequenceNumber开始获取，需要设置StartingSequenceNumber
        // AFTER_SEQUENCE_NUMBER 从指定的sequenceNumber之后开始获取，需要设置StartingSequenceNumber
        // TRIM_HORIZON 从最旧的记录开始获取
        // LATEST 从最新的记录开始获取
        // AT_TIMESTAMP 从指定的时间戳(13位)开始获取，需要设置Timestamp
        String cursorType = PartitionCursorTypeEnum.AT_SEQUENCE_NUMBER.name();

        if (Integer.parseInt(startingSequenceNumber) < 0) {
            ApiError errResponse = new ApiError("500", "520", "No message in DIS.");
            System.out.println(errResponse);
            return new ResponseEntity<>(errResponse, HttpStatus.OK);
        }

        // 获取数据游标
        GetPartitionCursorRequest request = new GetPartitionCursorRequest();
        request.setStreamName(streamName);
        request.setPartitionId(partitionId);
        request.setStartingSequenceNumber(startingSequenceNumber);
        request.setCursorType(cursorType);

        GetPartitionCursorResult response = dic.getPartitionCursor(request);
        String cursor = response.getPartitionCursor();

        LOGGER.info("Get stream {}[partitionId={}] cursor success : {}", streamName, partitionId, cursor);

        GetRecordsRequest recordsRequest = new GetRecordsRequest();
        GetRecordsResult recordResponse = null;

        recordsRequest.setPartitionCursor(cursor);
        recordResponse = dic.getRecords(recordsRequest);


        Record rec = recordResponse.getRecords().get(0);

//        获取数据
        String recStr = new String(rec.getData().array());

        System.out.println("DIS message: " + recStr);
        LOGGER.info("Get record [{}], partitionKey [{}], sequenceNumber [{}].",
            new String(rec.getData().array()),
            rec.getPartitionKey(),
            rec.getSequenceNumber());
        AgricultureMsg slMsg = StringUtil.getAgricultureMsg(recStr);
        TempDatabase.slMsg = slMsg;

//        createDeviceCommand.runSetCommand(slMsg);

/*            // 测试用
            Random random = new Random();
            Integer lux = random.nextInt(500);
            System.out.println(lux);
            String lightState = "ON";
            for(int i=0;i<10;i++){
                if(i%3==0){
                    lightState = "OFF";
                }
                else{
                    lightState = "ON";
                }
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
            String dateNowStr = sdf.format(date);
            System.out.println(dateNowStr);
            StreetLightMsg slMsg = new StreetLightMsg(lux.toString(),lightState,dateNowStr);*/

        return new ResponseEntity<>(slMsg, HttpStatus.OK);
    }

}


