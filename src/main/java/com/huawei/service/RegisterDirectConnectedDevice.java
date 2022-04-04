package com.huawei.service;

import java.util.HashMap;
import java.util.Map;

import com.huawei.domain.DeviceRegisterResponse;
import com.huawei.domain.DeviceRegisterVerifyCode;
import com.huawei.domain.TempDatabase;
import com.huawei.utils.Constant;
import com.huawei.utils.HttpsUtil;
import com.huawei.utils.JsonUtil;
import com.huawei.utils.StreamClosedHttpResponse;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Register Directly Connected Device :
 * This interface is used to register devices on the IoT platform.
 * After the registration is successful,
 * the IoT platform allocates a device ID for the device,which is used as the unique identifier of the device.
 * Unregistered devices are not allowed to access the IoT platform.
 */
@Service
public class RegisterDirectConnectedDevice {

    public DeviceRegisterResponse registerDirectDevice(DeviceRegisterVerifyCode deviceInfo) throws Exception {

        //Please make sure that the following parameter values have been modified in the Constant file.
        String urlRegisterDevice = Constant.BASE_URL + "/v5/iot/" + TempDatabase.paras.getProjectId() + "/devices";

        String accessToken = LoginService.login();
        //Map<String, String> header = new HashMap<>();
        //header.put("X-Auth-Token", accessToken);

        Map<String, Object> paramReg = new HashMap<>();
        paramReg.put("product_id", TempDatabase.paras.getProduct_id());
        paramReg.put("node_id", deviceInfo.getVerifyCode());
        paramReg.put("app_id", TempDatabase.paras.getApp_id());

        String jsonRequest = JsonUtil.jsonObj2Sting(paramReg);

        System.out.println("Try to register device.");
        try (CloseableHttpClient client = HttpClients.custom()
            .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                (x509CertChain, authType) -> true).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .build()) {

            HttpResponse responseRegOrg = client.execute(RequestBuilder.create("POST")//请求方法POST
                .setUri(urlRegisterDevice)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf8")
                .addHeader("X-Auth-Token", accessToken)
                .setEntity(new StringEntity(jsonRequest)).build());
            StreamClosedHttpResponse responseReg = new StreamClosedHttpResponse(responseRegOrg);
            JSONObject responseResponse = JSONObject.parseObject(responseReg.getContent());
            String device_id = responseResponse.getString("device_id");
            String secret = responseResponse.getJSONObject("auth_info").getString("secret");
            DeviceRegisterResponse result = new DeviceRegisterResponse();
            result.setDevice_id(device_id);
            result.setSecret(secret);
            System.out.println("RegisterDirectConnectedDevice, response content:");
            //打印deviceID和密钥
            System.out.println(responseReg.getContent());
            System.out.println();
            return result;

        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

}
