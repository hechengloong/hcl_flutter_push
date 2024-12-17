package com.yuanmipush.ym_flutter_push.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yuanmipush.ym_flutter_push.huawei.HuaweiPushProvider;
import com.yuanmipush.ym_flutter_push.mi.MiPushProvider;
import com.yuanmipush.ym_flutter_push.oppo.OppoPushProvider;
import com.yuanmipush.ym_flutter_push.vivo.VivoPushProvider;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class YmPushClient {
    public static final String TAG="YmPushClient";

    private Map<String,BaseMixPushProvider> mProviderMap=new HashMap<>();
    Context context;
    private EventChannel.EventSink eventSink;

    private BaseMixPushProvider defaultProvider;

    public BaseMixPushProvider getDefaultProvider() {
        return defaultProvider;
    }

    public MixPushReceiver getReceiver() {
        return receiver;
    }

    MixPushReceiver receiver=new MixPushReceiver() {
        @Override
        public void onRegisterSucceed(Context context, MixPushPlatform platform) {
            Gson gson = new Gson();
            Map data = new HashMap();
            data.put("type","registerId");
            data.put("value",platform.getRegId());
            if(eventSink!=null){
                eventSink.success(gson.toJson(data));
            }
        }
    };

    public void openApp(Context context) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            context.startActivity(intent);
        }
    }

    /**
     * 60 秒超时
     */
    public void getRegisterId(Context context, final GetRegisterIdCallback callback, boolean isPassThrough) {
        final Context appContext = context.getApplicationContext();
        Handler handler = new Handler(Looper.getMainLooper());
        long startTime = System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (Math.abs(System.currentTimeMillis() - startTime) > 60000) {
                    callback.callback(null);
                    Log.e(TAG, "getRegisterId超时");
                    return;
                }
                BaseMixPushProvider pushProvider = defaultProvider;
                if (pushProvider == null) {
                    handler.postDelayed(this, 2000);
                    Log.e(TAG, "getRegisterId pushProvider == null");
                    return;
                }
                String regId = pushProvider.getRegisterId(appContext);
                if (regId != null && !regId.isEmpty()) {
                    Log.i(TAG, "getRegisterId regId=" + regId);
                    callback.callback(new MixPushPlatform(pushProvider.getPlatformName(), regId));
                    return;
                }
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);
    }
//
//    public MixPushReceiver getReceiver() {
//        return receiver;
//    }

    private YmPushClient(){
    }

    private static YmPushClient instance;
    public static YmPushClient getInstance(){
        if(instance == null){
            instance = new YmPushClient();
        }
        return instance;
    }

    public void bind(EventChannel.EventSink eventSink){
        this.eventSink = eventSink;
    }
    public void unBind(){
        this.eventSink = null;
    }

    public void init(Context context){
        this.context = context;

        BaseMixPushProvider oppoPushProvider= new OppoPushProvider();
        mProviderMap.put(oppoPushProvider.getPlatformName(),oppoPushProvider);

        BaseMixPushProvider vivoPushProvider= new VivoPushProvider();
        mProviderMap.put(vivoPushProvider.getPlatformName(),vivoPushProvider);

        BaseMixPushProvider hwPushProvider= new HuaweiPushProvider();
        mProviderMap.put(hwPushProvider.getPlatformName(),hwPushProvider);

        BaseMixPushProvider miPushProvider= new MiPushProvider();
        mProviderMap.put(miPushProvider.getPlatformName(),miPushProvider);

        for (Map.Entry<String,BaseMixPushProvider> entry:mProviderMap.entrySet()){
            BaseMixPushProvider item = entry.getValue();
            if(!item.isSupport(context)){
                continue;
            }
            Log.i(TAG, "-----found push provider : "+item.getPlatformName());
            defaultProvider=item;
            break;
        }

        if(defaultProvider==null){
            Log.i(TAG, "-----no push provider found");
            return;
        }

        defaultProvider.register(context, RegisterType.all);
    }


}
