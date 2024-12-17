package com.yuanmipush.ym_flutter_push.huawei;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.adapter.internal.AvailableCode;
import com.huawei.hms.api.HuaweiMobileServicesUtil;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;
import com.yuanmipush.ym_flutter_push.base.BaseMixPushProvider;
import com.yuanmipush.ym_flutter_push.base.MixPushPlatform;
import com.yuanmipush.ym_flutter_push.base.RegisterType;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;

import java.lang.reflect.Method;
import java.util.logging.Logger;


public class HuaweiPushProvider extends BaseMixPushProvider {
    public static final String HUAWEI = "huawei";
    public static String regId;
    private boolean checking=false;

    @Override
    public void register(Context context, RegisterType type) {
        HmsMessaging.getInstance(context).setAutoInitEnabled(true);
    }

    @Override
    public void unRegister(Context context) {
    }

    @Override
    public boolean isSupport(Context context) {
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        }
//        String manufacturer = Build.MANUFACTURER.toLowerCase();
//        if (!manufacturer.equals("huawei")) {
//            return false;
//        }
        int available = HuaweiMobileServicesUtil.isHuaweiMobileServicesAvailable(context);
        if (available != AvailableCode.SUCCESS) {
            Log.e(YmPushClient.TAG, "华为推送不可用 ErrorCode = " + available);
            return false;
        }
        return true;
//        return canHuaWeiPush();
    }

    /**
     * 判断是否可以使用华为推送
     */
    public static Boolean canHuaWeiPush() {
        int emuiApiLevel = 0;
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getDeclaredMethod("get", new Class[]{String.class});
            emuiApiLevel = Integer.parseInt((String) method.invoke(cls, new Object[]{"ro.build.hw_emui_api_level"}));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return emuiApiLevel >= 5.0;
    }

    @Override
    public String getPlatformName() {
        return HuaweiPushProvider.HUAWEI;
    }

    @Override
    public void setRegisterId(String id) {
        regId = id;
    }

    @Override
    public String getRegisterId(Context context) {
        return regId;
    }


//    private void syncGetToken(final Context context) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Log.e("TAG", "--------syncGetToken start ");
//                    String id = HmsInstanceId.getInstance(context).getToken("112093657", "HCM");
//                    Log.e("TAG", "--------syncGetToken end "+id);
//                    if(!TextUtils.isEmpty(id)){
//                        Log.e("TAG", "--get token:" + id);
//                        regId = id;
//                    }
//                } catch (ApiException e) {
//                    Log.e("TAG", "hms get token failed " + e.toString() + " https://developer.huawei.com/consumer/cn/doc/development/HMSCore-References-V5/error-code-0000001050255690-V5", e);
//                    e.printStackTrace();
//                }finally {
//                    checking = false;
//                }
//            }
//        }.start();
//    }
}
