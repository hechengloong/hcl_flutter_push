package com.yuanmipush.ym_flutter_push.base;

import android.content.Context;
import android.content.Intent;

public abstract class MixPushReceiver {
    /**
     * 通知栏推送SDK注册成功回调
     */
    public abstract void onRegisterSucceed(Context context, MixPushPlatform platform);

    /**
     * 通知栏消息消息到达回调
     */
    public void onNotificationMessageArrived(Context context, MixPushMessage message) {

    }

    /**
     * 打开APP回调,可以在这里增加统计功能
     */
    public void openAppCallback(Context context) {

    }
}
