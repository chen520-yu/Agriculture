package com.huawei.domain;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Data
public class AgricultureMsg {

    private String eventime;

//    温度，湿度，光强
    private String tempture;
    private String humidity;
    private String luminance;

//    灯光和电机状态
    private String lightState;
    private String motorState;

}
