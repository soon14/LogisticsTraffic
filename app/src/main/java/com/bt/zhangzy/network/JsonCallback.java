package com.bt.zhangzy.network;

import com.bt.zhangzy.tools.Json;

/**f
 * 网络回调接口  处理返回的json字符串
 * Created by ZhangZy on 2016-1-15.
 */
public abstract class JsonCallback extends NetCallback {
    public abstract void onSuccess(String msg,Json json);

    @Override
    public void onSuccess(String str) {
        Json json = Json.ToJson(str);
        if(json != null){
            if(json.has("code")){
                int code = json.getInt("code");
                String message = json.getString("message");
                if(code == 200){
                    onSuccess(message,json.getJson("result"));
                }else{
                    onFailed(message);
                }
                return;
            }
        }
        onFailed(str);
    }
}
