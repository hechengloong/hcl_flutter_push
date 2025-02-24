package com.yuanmipush.ym_flutter_push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.gson.Gson;
import com.heytap.msp.push.HeytapPushManager;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.yuanmipush.ym_flutter_push.base.BaseMixPushProvider;
import com.yuanmipush.ym_flutter_push.base.GetRegisterIdCallback;
import com.yuanmipush.ym_flutter_push.base.MixPushMessage;
import com.yuanmipush.ym_flutter_push.base.MixPushPlatform;
import com.yuanmipush.ym_flutter_push.base.YmPushClient;
import com.yuanmipush.ym_flutter_push.mi.MiPushProvider;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** YmFlutterPushPlugin */
public class YmFlutterPushPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private EventChannel eventChannel; // 添加 EventChannel

  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "ym_flutter_push");
    channel.setMethodCallHandler(this);

     eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "ym_flutter_push_event");
        eventChannel.setStreamHandler(this);
    context = flutterPluginBinding.getApplicationContext();

    //初始化client
    YmPushClient.getInstance().init(context);
    }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else if (call.method.equals("init")){
      result.success("success");
    } else if (call.method.equals("getRegisterInfo")){
      YmPushClient.getInstance().getRegisterId(context, new GetRegisterIdCallback() {
        @Override
        public void callback(@Nullable MixPushPlatform platform) {
          if(platform==null){
            Log.i(YmPushClient.TAG,"--------getRegisterId is empty");
            result.success("");
            return;
          }
          Log.i(YmPushClient.TAG,"--------getRegisterId success:"+platform.toString());

          Map<String,Object> res =new HashMap<>();
          res.put("platform",platform.getPlatformName());
          res.put("regId",platform.getRegId());
          result.success(new Gson().toJson(res));
        }
      },true);
    }else {
      result.notImplemented();
    }
  }
 // 当监听 EventChannel 时调用
    @Override
    public void onListen(Object arguments, EventSink events) {
        this.eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        this.eventSink = null;
    }

    // 推送消息到 Flutter 端
    public void sendPushEvent(String message) {
        if (eventSink != null) {
            eventSink.success(message);
        }
    }

    // 当收到推送消息时调用
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e("MiPush", "onNotificationMessageArrived: " + message.toString());
        sendPushEvent("Notification arrived: " + message.getContent());
    }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

}
