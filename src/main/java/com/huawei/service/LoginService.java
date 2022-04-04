package com.huawei.service;

import com.alibaba.fastjson.JSONObject;

import com.huawei.domain.TempDatabase;
import com.huawei.utils.Constant;
import com.huawei.utils.JsonUtil;
import com.huawei.utils.StreamClosedHttpResponse;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.apache.http.ssl.SSLContextBuilder;

import java.time.*;

import java.util.HashMap;
import java.util.Map;

public class LoginService {

    /**
     * Authentication，get token
     */
    @SuppressWarnings("unchecked")
    public static String login() throws Exception {

        if (TempDatabase.tokenExpire != null) {
            // 判断token是否过期，过期了就清除
            OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
            String MinNow = now.toString().substring(0, 16);
            System.out.println(MinNow);
            if (TempDatabase.tokenExpire.compareTo(MinNow) < 1) {
                TempDatabase.accessToken = null;
            }
        }

        if (TempDatabase.accessToken == null) {

            String domainName = TempDatabase.paras.getDomain();
            String name = TempDatabase.paras.getName();
            String password = TempDatabase.paras.getPassword();
            String urlLogin = "https://iam.cn-north-4.myhuaweicloud.com/v3/auth/tokens?nocatalog=true";

            Map<String, String> paramDomain = new HashMap<>();
            paramDomain.put("name", domainName);
            Map<String, Object> paramUser = new HashMap<>();
            paramUser.put("domain", paramDomain);
            paramUser.put("name", name);
            paramUser.put("password", password);
            Map<String, Object> paramPassword = new HashMap<>();
            paramPassword.put("user", paramUser);
            Map<String, Object> paramIdentity = new HashMap<>();
            String[] paramMethods = {"password"};
            paramIdentity.put("methods", paramMethods);
            paramIdentity.put("password", paramPassword);
            Map<String, String> paramProject = new HashMap<>();
            paramProject.put("name", Constant.PROJECTNAME);
            Map<String, Object> paramScope = new HashMap<>();
            paramScope.put("project", paramProject);
            Map<String, Object> paramAuth = new HashMap<>();
            paramAuth.put("identity", paramIdentity);
            paramAuth.put("scope", paramScope);
            Map<String, Object> paramLogin = new HashMap<>();
            paramLogin.put("auth", paramAuth);
            String jsonRequest = JsonUtil.jsonObj2Sting(paramLogin);

            System.out.println("Try to login");

            try (CloseableHttpClient client = HttpClients.custom()
                .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                    (x509CertChain, authType) -> true).build())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build()) {

                HttpResponse responseLoginOrg = client.execute(RequestBuilder.create("POST")//请求方法POST
                    .setUri(urlLogin)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf8")
                    .setEntity(new StringEntity(jsonRequest)).build());
                StreamClosedHttpResponse responseLogin = new StreamClosedHttpResponse(responseLoginOrg);
                System.out.println("login status: " + responseLogin.getStatusLine());
                System.out.println();

                JSONObject loginResponse = JSONObject.parseObject(responseLogin.getContent());
                String expireDate = loginResponse.getJSONObject("token").getString("expires_at");
                TempDatabase.tokenExpire = expireDate.substring(0, 16);
                System.out.println(TempDatabase.tokenExpire);

                TempDatabase.accessToken = responseLogin.getFirstHeader("X-Subject-Token").getValue();
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        }

        return TempDatabase.accessToken;
    }

}
