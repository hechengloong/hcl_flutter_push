package com.yuanmipush.ym_flutter_push.vivo;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.model.UnvarnishedMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;
import com.yuanmipush.ym_flutter_push.base.MixPushMessage;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

public class PushMessageReceiverImpl extends OpenClientPushMessageReceiver {
    @Override
    public void onReceiveRegId(Context context, String s) {
        Log.d(YmPushClient.TAG, " onReceiveRegId= " + s);
    }

    @Override
    public void onTransmissionMessage(Context context, UnvarnishedMessage unvarnishedMessage) {
        Log.i(YmPushClient.TAG, " onTransmissionMessage= " + unvarnishedMessage.getMessage());
    }


    @Override
    public void onForegroundMessageArrived(UPSNotificationMessage msg) {
        Log.i(YmPushClient.TAG, " 收到前台不展示消息 onForegroundMessageArrived= " + msg);

    }


}
