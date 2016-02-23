package com.bt.zhangzy.logisticstraffic.data;

/**
 * Created by ZhangZy on 2016-2-23.
 */
public enum OrderType {
    /*订单类型 车队订单=1 公共订单=2 指派司机订单	3*/
    Empty, MotorcadesType, PublicType
    ;

    //解析从服务器返回的数据
    public static OrderType parseOrderType(int type){
        return type >= 0 && type < values().length ? values()[type] : Empty;
    }
}
