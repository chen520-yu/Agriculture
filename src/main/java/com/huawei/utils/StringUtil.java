/*
 * Copyright Notice:
 *      Copyright  1998-2008, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */

package com.huawei.utils;

import com.huawei.domain.AgricultureMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class StringUtil {

    public static boolean strIsNullOrEmpty(String s) {
        return (null == s || s.trim().length() < 1);
    }

    public static AgricultureMsg getAgricultureMsg(String recordResp) {
        JSONObject jsonObject = JSONObject.fromObject(recordResp);
        JSONArray jsonArr = jsonObject.getJSONArray("services");
        JSONObject srvInfo = jsonArr.getJSONObject(0);
        JSONObject srvData = srvInfo.getJSONObject("data");

        String eventTime = srvInfo.getString("eventTime");

        String luminance = srvData.getString("luminance");
        String tempture = srvData.getString("tempture");
        String humidity = srvData.getString("humidity");


        String lightSatate = srvData.getString("light_state");


        AgricultureMsg slMsg = new AgricultureMsg();

        slMsg.setTempture(luminance);
        slMsg.setHumidity(humidity);
        slMsg.setTempture(tempture);

        slMsg.setEventime(eventTime);
        slMsg.setLightState(lightSatate);
        return slMsg;
    }
}
