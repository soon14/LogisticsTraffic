package ali.pay;

/**
 * 用于支付结果的通知
 * Created by ZhangZy on 2016-3-25.
 */
public interface AliPayResultCallback {
    void paySuccess();

    void payFailed(String msg);

    void payOther(String url);
}
