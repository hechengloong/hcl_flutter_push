package com.yuanmipush.ym_flutter_push.oppo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.yuanmipush.ym_flutter_push.base.BaseMixPushProvider;
import com.yuanmipush.ym_flutter_push.base.RegisterType;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

public class OppoPushProvider extends BaseMixPushProvider {
    public static final String OPPO = "oppo";

    @Override
    public void register(Context context, RegisterType type) {
        String appKey = getMetaData(context, "OPPO_APP_KEY");
        String appSecret = getMetaData(context, "OPPO_APP_SECRET");

        HeytapPushManager.register(context, appKey, appSecret, new ICallBackResultService() {
            @Override
            public void onRegister(int responseCode, String registerID, String packageName, String miniPackageName) {
            }

            @Override
            public void onUnRegister(int i, String s, String s1) {

            }

            @Override
            public void onSetPushTime(int i, String s) {

            }

            @Override
            public void onGetPushStatus(int i, int i1) {

            }

            @Override
            public void onGetNotificationStatus(int i, int i1) {

            }

            @Override
            public void onError(int errorCode, String message, String packageName, String miniProgramPkg) {
                Log.e(YmPushClient.TAG, "onError: "+message );
            }
        });
        HeytapPushManager.init(context,true);
        Log.i(YmPushClient.TAG, "register: end");
    }

    @Override
    public void unRegister(Context context) {
    }

    @Override
    public String getRegisterId(final Context context) {
        return HeytapPushManager.getRegisterID();
    }

    @Override
    public boolean isSupport(Context context) {
        if (Build.VERSION.SDK_INT < 19) {
            return false;
        }
        String brand = Build.BRAND.toLowerCase();
        String manufacturer = Build.MANUFACTURER.toLowerCase();
//        if (manufacturer.equals("oneplus") || manufacturer.equals("oppo") || brand.equals("oppo") || brand.equals("realme")) {
            HeytapPushManager.init(context, true);
            return HeytapPushManager.isSupportPush(context);
//        }
//        return false;
    }

    @Override
    public String getPlatformName() {
        return OppoPushProvider.OPPO;
    }
}
