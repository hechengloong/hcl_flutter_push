import Flutter
import UIKit
import UserNotifications
import Foundation

public class YmFlutterPushPlugin: NSObject, FlutterPlugin,UNUserNotificationCenterDelegate {

  private var regId: String?
  let center: UNUserNotificationCenter
  let channel: FlutterMethodChannel

   init(channel: FlutterMethodChannel) {
     self.channel = channel
      self.center = UNUserNotificationCenter.current()
  }
    
  // 注册插件
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "ym_flutter_push", binaryMessenger: registrar.messenger())
    let instance = YmFlutterPushPlugin(channel: channel)
    UNUserNotificationCenter.current().delegate = instance

    registrar.addMethodCallDelegate(instance,channel:channel)
    registrar.addApplicationDelegate(instance)


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
        // 创建 EventChannel 用于发送推送通知事件
    let eventChannel = FlutterEventChannel(name: "ym_flutter_push_event", binaryMessenger: registrar.messenger())
    eventChannel.setStreamHandler(instance)
  }

  // 处理方法调用
  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getRegisterInfo":
        getRegisterInfo(result: result)
        return;
    case "deleteNotifications": 
            center.removeDeliveredNotifications(withIdentifiers: call.arguments as! [String])
        result(true)
        return;
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
       // 发送推送通知的内容到 Flutter 端
    if let eventSink = self.eventSink {
      eventSink([
        "id": notification.request.identifier,
        "body": notification.request.content.body,
        "title": notification.request.content.title,
        "subtitle": notification.request.content.subtitle,
        "userInfo": notification.request.content.userInfo
      ])
    }

        completionHandler()
    }
}
// MARK: - EventChannel StreamHandler
extension YmFlutterPushPlugin: FlutterStreamHandler {

  // 监听推送消息
  public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) {
    self.eventSink = events
  }

  // 取消监听
  public func onCancel(withArguments arguments: Any?) {
    self.eventSink = nil
  }
}