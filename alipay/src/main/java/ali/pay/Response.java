package ali.pay;

import com.zhangzy.base.http.BaseEntity;

/**
 * Created by ZhangZy on 2016-3-25.
 */
public class Response extends BaseEntity{
    JsonAliPay alipay;

    public JsonAliPay getAlipay() {
        return alipay;
    }

    public void setAlipay(JsonAliPay alipay) {
        this.alipay = alipay;
    }
}
