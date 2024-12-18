import Flutter
import UIKit
import UserNotifications

public class YmFlutterPushPlugin: NSObject, FlutterPlugin {

  // private var pushChannel: FlutterMethodChannel?
  // private var eventChannel: FlutterEventChannel?
  private var regId: String?
  // private var eventSink: FlutterEventSink?

  // 注册插件
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym_flutter_push", binaryMessenger: registrar.messenger())
    let instance = YmFlutterPushPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)

    // // 创建 EventChannel，用于推送消息的广播
    // let eventChannel = FlutterEventChannel(name: "ym_flutter_push.event", binaryMessenger: registrar.messenger())
    // eventChannel.setStreamHandler(instance)
    
    // // 初始化 pushChannel 和 eventChannel
    // instance.pushChannel = channel
    // instance.eventChannel = eventChannel

    // 请求推送权限
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
      if granted {
        print("-------通知权限已授予")
        // 权限授予成功后，再注册推送通知
        DispatchQueue.main.async {
          print("----------注册推送通知 start")
          UIApplication.shared.registerForRemoteNotifications()
          print("----------注册推送通知 end")
        }
      } else {
        print("---------通知权限被拒绝")
      }
    }
  }

  // 处理方法调用
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getRegisterInfo":
        getRegisterInfo(result: result)
    default:
      result(FlutterMethodNotImplemented)
    }
  }

  // 获取设备的 regId（设备令牌）
  private func getRegisterInfo(result: @escaping FlutterResult) {
    // 如果 regId 已经存在，直接返回
    print("----get token success: \(regId)")
    result(regId)
  }

  // 获取设备的推送通知 token
    public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let token = deviceToken.map { String(format: "%02x", $0) }.joined()
        print("---------------Device Token: \(token)")
        regId = token
    }

    // 推送点击回调
    public func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        print("---------------Received Push Notification: \(userInfo)")
        
        // 通过 FlutterMethodChannel 将点击事件传递给 Flutter 层
        // pushChannel?.invokeMethod("onPushNotificationClicked", arguments: userInfo)
        
        completionHandler(.newData)
    }
}

