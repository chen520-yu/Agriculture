package com.huawei.smn;

import com.huawei.domain.TempDatabase;
import com.smn.client.DefaultSmnClient;
import com.smn.client.SmnClient;
import com.smn.request.publish.PublishRequest;
import com.smn.response.publish.PublishResponse;

public class SMNUtil {
    private static SmnClient smnClient;

    public static void sendMsg(String subject, String message){
        if (smnClient == null) {
            smnClient = new DefaultSmnClient(TempDatabase.paras.getName(), TempDatabase.paras.getDomain(),
                    TempDatabase.paras.getPassword(), "cn-north-4");
        }

        PublishRequest smnRequest = new PublishRequest();
        smnRequest.setSubject(subject).setMessage(message).setTopicUrn(TempDatabase.paras.getTopicUrn());

        try {
            PublishResponse res = smnClient.sendRequest(smnRequest);
            System.out.println("httpCode:" + res.getHttpCode()
                    + ",message_id:" + res.getMessageId()
                    + ", request_id:" + res.getRequestId()
                    + ", errormessage:" + res.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
