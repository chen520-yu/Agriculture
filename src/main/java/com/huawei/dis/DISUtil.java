package com.huawei.dis;

import com.huaweicloud.dis.DIS;
import com.huaweicloud.dis.DISClientBuilder;

import com.huawei.domain.TempDatabase;

public class DISUtil {
    private static DIS dic;

    private static String APP_NAME = "dis-consumer-example";

    public static DIS getInstance() {
        if (dic == null) {
            synchronized (DISUtil.class) {
                if (dic == null) {
                    dic = createDISClient();
                }
            }
        }
        return dic;
    }

    public static String getStreamName() {
        return TempDatabase.paras.getStreamName();
    }

    public static String getAppName() {
        return APP_NAME;
    }

    private static DIS createDISClient() {
        System.out.println(TempDatabase.paras.getAk());
        return DISClientBuilder.standard()
            .withEndpoint("https://dis.cn-north-4.myhuaweicloud.com")
            .withAk(TempDatabase.paras.getAk())
            .withSk(TempDatabase.paras.getSk())
            .withProjectId(TempDatabase.paras.getProjectId())
            .withRegion("cn-north-4")
            .build();
    }
}
