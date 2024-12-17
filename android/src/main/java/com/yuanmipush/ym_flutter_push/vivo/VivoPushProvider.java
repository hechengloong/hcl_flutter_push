package com.yuanmipush.ym_flutter_push.vivo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.PushConfig;
import com.vivo.push.listener.IPushQueryActionListener;
import com.vivo.push.util.VivoPushException;
import com.yuanmipush.ym_flutter_push.base.BaseMixPushProvider;
import com.yuanmipush.ym_flutter_push.base.MixPushPlatform;
import com.yuanmipush.ym_flutter_push.base.RegisterType;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

public class VivoPushProvider  extends BaseMixPushProvider implements IPushActionListener{
    public static final String VIVO = "vivo";

    private String registerId="";

    @Override
    public void register(Context context, RegisterType type) {

        try {
            PushClient.getInstance(context).initialize(
                    new PushConfig
                            .Builder()
                            .agreePrivacyStatement(true)
                            .build());
        } catch (VivoPushException e) {
            e.printStackTrace();
        }

        // 打开push开关, 关闭为turnOffPush，详见api接入文档
        PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
            @Override
            public void onStateChanged(int state) {
                // TODO: 开关状态处理， 0代表成功，获取regid建议在state=0后获取；
            }
        });
    }

    @Override
    public void unRegister(Context context) {
        PushClient.getInstance(context).turnOffPush(this);
    }

    @Override
    public String getRegisterId(Context context) {
        if(registerId!=null && !registerId.isEmpty()){
            return registerId;
        }
        //订阅成功后再获取regid
        PushClient.getInstance(context).getRegId(new IPushQueryActionListener() {
            @Override
            public void onSuccess(String s) {
                Log.i(YmPushClient.TAG, "--------get  regId= " + s);
                registerId = s;
            }

            @Override
            public void onFail(Integer integer) {
                String errorCode = " 查询regid失败code= " + integer;
                Log.i(YmPushClient.TAG, "-------get  regId= " + errorCode);
            }
        });
        return registerId;
    }

    @Override
    public boolean isSupport(Context context) {
        String brand = Build.BRAND.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
//        if (manufacturer.equals("vivo") || brand.contains("vivo") || brand.contains("iqoo")) {
            return PushClient.getInstance(context).isSupport();
//        }
//        return false;
    }

    @Override
    public String getPlatformName() {
        return VivoPushProvider.VIVO;
    }

    @Override
    public void onStateChanged(int i) {
        Log.i(YmPushClient.TAG, "onStateChanged: "+i);
    }
}
