package com.huawei.service;

import com.huawei.domain.TempDatabase;
import com.huawei.utils.Constant;
import com.huawei.utils.HttpsUtil;
import com.huawei.utils.JsonUtil;
import com.huawei.utils.StreamClosedHttpResponse;

import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscribeDataChg {

    public void subscribeDataChange() throws Exception {

        String callbackUrl = "http://" + TempDatabase.paras.getCallbackIp() + ":8080/v1.0.0/messageReceiver";
        String urlSubDataCh = Constant.BASE_URL + "/v5/iot/" + TempDatabase.paras.getProjectId() + "/subscriptions";

        String accessToken = LoginService.login();

        Map<String, String> paramSubject = new HashMap<>();
        paramSubject.put("resource", "device.data");
        paramSubject.put("event", "update");
        Map<String, Object> subInfo = new HashMap<>();
        subInfo.put("subject", paramSubject);
        subInfo.put("callbackurl", callbackUrl);
        subInfo.put("app_id", TempDatabase.paras.getApp_id());
        subInfo.put("channel", "http");
        String jsonRequest = JsonUtil.jsonObj2Sting(subInfo);

        System.out.println("Try to subscribe.");
        try (CloseableHttpClient client = HttpClients.custom()
            .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                (x509CertChain, authType) -> true).build())
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .build()) {

            HttpResponse responseSubOrg = client.execute(RequestBuilder.create("POST")//请求方法POST
                .setUri(urlSubDataCh)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf8")
                .addHeader("X-Auth-Token", accessToken)
                .setEntity(new StringEntity(jsonRequest)).build());
            StreamClosedHttpResponse responseSub = new StreamClosedHttpResponse(responseSubOrg);
            if (String.valueOf(responseSub.getStatusLine().getStatusCode()).substring(0, 1).equals("2")) {
                System.out.println("subscribe success");
            } else {
                System.out.println("subscribe failed: ");
                System.out.println(urlSubDataCh);
                System.out.println(subInfo);
                System.out.println(responseSub.getStatusLine());
                System.out.println(responseSub.getContent());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
