import Flutter
import UIKit
import UserNotifications

public class YmFlutterPushPlugin: NSObject, FlutterPlugin, UIApplicationDelegate {

  private var pushChannel: FlutterMethodChannel?
  private var eventChannel: FlutterEventChannel?
  private var regId: String?
  private var eventSink: FlutterEventSink?

  // 注册插件
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym_flutter_push", binaryMessenger: registrar.messenger())
    let instance = YmFlutterPushPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)

    // 创建 EventChannel，用于推送消息的广播
    let eventChannel = FlutterEventChannel(name: "ym_flutter_push.event", binaryMessenger: registrar.messenger())
    eventChannel.setStreamHandler(instance)
    
    // 初始化 pushChannel 和 eventChannel
    instance.pushChannel = channel
    instance.eventChannel = eventChannel

    // 请求推送权限
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
      if granted {
        print("通知权限已授予")
        // 权限授予成功后，再注册推送通知
        DispatchQueue.main.async {
          UIApplication.shared.registerForRemoteNotifications()
        }
      } else {
        print("通知权限被拒绝")
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
    if let token = regId {
      print("----get token success: \(token)")
      result(token)
    } else {
      // 如果 regId 不存在，返回空字符串或错误信息
      result("")
    }
  }

  // 设备成功注册并获取到 deviceToken
  public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    // 将 deviceToken 转换为字符串并保存
    let tokenString = deviceToken.map { String(format: "%02x", $0) }.joined()
    print("---get token success : \(tokenString)")

    // 保存 deviceToken 到 regId
    regId = tokenString

    // 通知 Flutter 设备令牌已注册
    pushChannel?.invokeMethod("onDeviceToken", arguments: tokenString)
  }

  // 设备注册失败时回调
  public func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
    print("---push registration failed: \(error.localizedDescription)")
  }

  // 处理接收到的推送通知
  public func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
    if let message = userInfo["message"] as? String {
      // 通过 EventChannel 将推送消息发送给 Flutter
      eventSink?(message) // 使用 eventSink 推送消息
    }
    completionHandler(.newData)
  }
}

// MARK: - FlutterEventStreamHandler
extension YmFlutterPushPlugin: FlutterStreamHandler {
  public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
    eventSink = events
    return nil
  }

  public func onCancel(withArguments arguments: Any?) -> FlutterError? {
    eventSink = nil
    return nil
  }
}
