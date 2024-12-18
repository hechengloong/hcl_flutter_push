import Flutter
import UIKit
import UserNotifications
import Foundation

public class YmFlutterPushPlugin: NSObject, FlutterPlugin,UNUserNotificationCenterDelegate {

  private var regId: String?
  let center: UNUserNotificationCenter
  private var pushChannel: FlutterMethodChannel?
  private var eventChannel: FlutterEventChannel?
  private var eventSink: FlutterEventSink?

  override init() {
      self.center = UNUserNotificationCenter.current()
  }
    
  // 注册插件
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym_flutter_push", binaryMessenger: registrar.messenger())
    let instance = YmFlutterPushPlugin()
    UNUserNotificationCenter.current().delegate = instance

    registrar.addMethodCallDelegate(instance,channel:channel)
    registrar.addApplicationDelegate(instance)


    // 创建 EventChannel，用于推送消息的广播
    let eventChannel = FlutterEventChannel(name: "ym_flutter_push.event", binaryMessenger: registrar.messenger())
    eventChannel.setStreamHandler(instance)
    
    // 初始化 pushChannel
    instance.pushChannel = channel
    instance.eventChannel = eventChannel

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
    print("----callback token : \(regId)")
      var map = [String:String]()
      map["regId"] = regId
      map["platform"] = "apple"
      do {
          let jsonData = try JSONSerialization.data(withJSONObject: map, options: .prettyPrinted)
          if let jsonString = String(data: jsonData, encoding: .utf8) {
              print(jsonString)
              result(jsonString)
          }
      } catch {
          print("Error converting dictionary to JSON string: \(error)")
          result("")
      }
  }

   public func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data -> String in
            return String(format: "%02.2hhx", data)
        }
        let token = tokenParts.joined()
        print("---------------Device Token: \(token)")
        regId = token
    }


    public func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        let notification = response.notification
        print("----------点击通知: \(notification)")
          // 通过 EventChannel 将推送消息发送给 Flutter
        if let eventSink = eventSink {
          eventSink([
            "id": notification.request.identifier,
            "body": notification.request.content.body,
            "title": notification.request.content.title,
            "subtitle": notification.request.content.subtitle,
            "userInfo": notification.request.content.userInfo
        ]) // 使用 eventSink 推送消息
        }

        completionHandler()
    }

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
