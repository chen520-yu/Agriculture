package com.huawei.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.huawei.domain.AgricultureMsg;
import com.huawei.domain.TempDatabase;
import com.huawei.smn.SMNUtil;
import com.huawei.utils.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import org.springframework.stereotype.Service;

/**
 * Create Device Command :
 * This interface is used to send command to device.
 * If a device is not online,
 * the IoT platform buffers the command and delivers the message to the device after the device is online.
 * The NA can set the maximum buffering time.
 */
@Service
public class CreateDeviceCommand {

    public void setCommand(String command) throws Exception {

//        确定url命令，格式确定
        String urlCreateDeviceCommand = Constant.BASE_URL + "/v5/iot/" + TempDatabase.paras.getProjectId() + "/devices/"
            + TempDatabase.deviceId + "/commands";

//        获取token
        String accessToken = LoginService.login();

//        设置map命令，转换为json对象
        Map<String, Object> paramCreateDeviceCommand = new HashMap<>();

        paramCreateDeviceCommand.put("service_id", "MOTOR");
        paramCreateDeviceCommand.put("command_name", "Set_Motor");

        Map<String, String> paras = new HashMap<>();
        paras.put("motor", command);
        paramCreateDeviceCommand.put("paras", paras);
        String jsonRequest = JsonUtil.jsonObj2Sting(paramCreateDeviceCommand);

        System.out.println("Try to create device command.");
        try (CloseableHttpClient client = HttpClients.custom()
            .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                (x509CertChain, authType) -> true).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .build()) {
//            创建发送时间，并确定格式
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String sj = sdf.format(d);
            System.out.println("Sent time: " + sj);

//            传送命令数据，调用CreateDeviceCommand
            HttpResponse responseCmdOrg = client.execute(RequestBuilder.create("POST")//请求方法POST
                .setUri(urlCreateDeviceCommand)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf8")
                .addHeader("X-Auth-Token", accessToken)
                .setEntity(new StringEntity(jsonRequest)).build());

//            发送命令，然后用responseCmd接受返回数据
            StreamClosedHttpResponse responseCmd = new StreamClosedHttpResponse(responseCmdOrg);
            if (String.valueOf(responseCmd.getStatusLine().getStatusCode()).substring(0, 1).equals("2")) {
                System.out.println("Send command success");
            } else {
                System.out.println("Send command fail: ");
                System.out.println("Response:");
                System.out.println(responseCmd.toString());
                System.out.println(responseCmd.getContent());
                SMNUtil.sendMsg("【命令下发失败】","错误信息：" + responseCmd.getContent());
            }
        } catch (Exception e) {
            System.out.println("set command failed " + e.getMessage());
            SMNUtil.sendMsg("【命令下发失败】","错误信息：" + e.getMessage());
        }
    }

    public void runSetCommand(AgricultureMsg agricultureMsg) throws Exception {

        boolean isOn = agricultureMsg.getLightState().equals("ON");

//        判断是哪种选择模型

//        选择固定时间，时间固定，但是浇水的时间有范围，即endtime是在starttime上面加上一定时间
        if (TempDatabase.ctrlMode.equals("timeRange")) {
            boolean isInTime;
            isInTime = Time.isInTimeMain(TempDatabase.timeRange.getStartTime(), TempDatabase.timeRange.getEndTime(),
                agricultureMsg.getEventime());
            if (isOn && isInTime) {
                System.out.println("In OFF time and is ON, send OFF");
                setCommand("OFF");
            }
            if (!isOn && !isInTime) {
                System.out.println("In ON time and is OFF, send ON");
                setCommand("ON");
            }
        }

//        if (TempDatabase.ctrlMode.equals("autoLux")) {
//            int threshold = TempDatabase.threshold;
//
//            if (threshold <= 0) {
//                System.out.println("threshold <= 0");
//            } else if (Integer.parseInt(agricultureMsg.getTempture()) < threshold && !isOn) {
//                System.out.println("Luminance < threshold and is OFF, send ON");
//                setCommand("ON");
//            } else if (Integer.parseInt(agricultureMsg.getTempture()) > threshold && isOn) {
//                System.out.println("Luminance > threshold and is ON, send OFF");
//                setCommand("OFF");
//            }
//        }
//        选择开关
        if (TempDatabase.ctrlMode.equals("terminal")) {
            if (TempDatabase.onOffSetter && !isOn) {
                System.out.println("Send ON");
                setCommand("ON");
            }
            if (!TempDatabase.onOffSetter && isOn) {
                System.out.println("Send OFF");
                setCommand("OFF");
            }
        }
    }
}
