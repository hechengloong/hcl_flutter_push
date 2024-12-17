package com.yuanmipush.ym_flutter_push.mi;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yuanmipush.ym_flutter_push.base.BaseMixPushProvider;
import com.yuanmipush.ym_flutter_push.base.RegisterType;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

public class MiPushProvider extends BaseMixPushProvider {
    public static final String MI = "mi";
    static RegisterType registerType;
    private String regId;

    @Override
    public void register(Context context, RegisterType type) {
        registerType = type;
        String appId = getMetaData(context, "MI_APP_ID");
        String appKey = getMetaData(context, "MI_APP_KEY");

        MiPushClient.registerPush(context, appId, appKey);
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(YmPushClient.TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(YmPushClient.TAG, content);
            }
        };
        Logger.setLogger(context, newLogger);
    }

    @Override
    public void unRegister(Context context) {
        MiPushClient.unregisterPush(context.getApplicationContext());
    }

    @Override
    public String getRegisterId(Context context) {
        if(!TextUtils.isEmpty(regId)){
            return regId;
        }
        return MiPushClient.getRegId(context);
    }

    @Override
    public void setRegisterId(String id) {
        super.setRegisterId(id);
    }

    @Override
    public boolean isSupport(Context context) {
        return true;
    }

    @Override
    public String getPlatformName() {
        return MiPushProvider.MI;
    }
}
