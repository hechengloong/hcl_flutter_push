package com.yuanmipush.ym_flutter_push.mi;

import android.content.Context;
import android.util.Log;

import com.xiaomi.mipush.sdk.*;
import com.yuanmipush.ym_flutter_push.base.MixPushMessage;
import com.yuanmipush.ym_flutter_push.base.MixPushPlatform;
import com.yuanmipush.ym_flutter_push.base.RegisterType;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

import java.util.Arrays;
import java.util.List;

public class MiPushMessageReceiver extends PushMessageReceiver {
//    private String mRegId;

    //    private long mResultCode = -1;
//    private String mReason;
//    private String mCommand;
//    private String mMessage;
//    private String mTopic;
//    private String mAlias;
//    private String mUserAccount;
//    private String mStartTime;
//    private String mEndTime;
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.e(YmPushClient.TAG,
                "--------onReceivePassThroughMessage is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.e(YmPushClient.TAG,
                "--------onNotificationMessageClicked is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(YmPushClient.TAG,
                "--------onNotificationMessageArrived is called. " + message.toString());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e(YmPushClient.TAG,
                "------------onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                String  mRegId = cmdArg1;
                YmPushClient.getInstance().getDefaultProvider().setRegisterId(mRegId);
            } else {
            }
        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e(YmPushClient.TAG,
                "---------onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                String mRegId = cmdArg1;
                Log.e(YmPushClient.TAG,
                        "---------onCommandResult reg success. " + mRegId);
                YmPushClient.getInstance().getDefaultProvider().setRegisterId(mRegId);
            } else {
                Log.e(YmPushClient.TAG,
                        "--------onCommandResult reg fail. " + message.toString());
            }
        }
    }

    @Override
    public void onRequirePermissions(Context context, String[] strings) {
        super.onRequirePermissions(context, strings);
//        handler.getLogger().log(MiPushProvider.MI, "onRequirePermissions 缺少权限: " + Arrays.toString(strings));
    }
}