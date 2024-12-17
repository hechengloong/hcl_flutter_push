import Flutter
import UIKit
import UserNotifications

public class YmFlutterPushPlugin: NSObject, FlutterPlugin {

  private var pushChannel: FlutterMethodChannel?
  private var eventChannel: FlutterEventChannel?
  private var regId: String?
  private var regIdRetryTimer: Timer?
  private var retryCount: Int = 0
  private let maxRetries: Int = 150 // 30s / 200ms = 150次重试

  // 注册插件
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym_flutter_push", binaryMessenger: registrar.messenger())
    let instance = YmFlutterPushPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)

    // 创建 EventChannel，用于推送消息的广播
    let eventChannel = FlutterEventChannel(name: "ym_flutter_push.event", binaryMessenger: registrar.messenger())
    eventChannel.setStreamHandler(instance)
    
    // 初始化 pushChannel
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
      return
    }

    // 启动重试逻辑，200ms 重试一次，最多尝试 30s
    retryCount = 0
    regIdRetryTimer = Timer.scheduledTimer(withTimeInterval: 0.2, repeats: true) { timer in
      if let token = self.regId {
        // 成功获取到 regId
        result(token)
        timer.invalidate() // 停止重试
      } else {
        print("----geting token ...")
        self.retryCount += 1
        if self.retryCount > self.maxRetries {
          print("----geting token error max retries")
          // 超过最大重试次数，认为超时
          result("")
          timer.invalidate() // 停止重试
        }
      }
    }
  }

  // 设备成功注册并获取到 deviceToken
  public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
    // 将 deviceToken 转换为字符串并保存
    let tokenString = deviceToken.map { String(format: "%02x", $0) }.joined()
    print("---get token success : \(tokenString)")

    // 保存 deviceToken 到 regId
    regId = tokenString
  }
    

  // 处理接收到的推送通知
  public func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
    if let message = userInfo["message"] as? String {
      // 通过 EventChannel 将推送消息发送给 Flutter
      if let eventSink = eventSink {
        eventSink(message) // 使用 eventSink 推送消息
      }
    }
    completionHandler(.newData)
  }

  // EventStream 用于推送消息
  private var eventSink: FlutterEventSink?

}

extension YmFlutterPushPlugin: FlutterStreamHandler {
  // Start listening to the stream.
  public func onListen(withArguments arguments: Any?, eventSink sink: @escaping FlutterEventSink) -> FlutterError? {
    self.eventSink = sink
    return nil
  }

  // Stop listening to the stream.
  public func onCancel(withArguments arguments: Any?) -> FlutterError? {
    self.eventSink = nil
    return nil
  }
}
