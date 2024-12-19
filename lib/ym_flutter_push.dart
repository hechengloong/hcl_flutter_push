import 'package:flutter/services.dart';

import 'ym_flutter_push_platform_interface.dart';

class YmFlutterPush {
  YmFlutterPush._internal();
  static final YmFlutterPush instance = YmFlutterPush._internal();
  factory YmFlutterPush() => instance;

  EventChannel methodChannel = const EventChannel('ym_flutter_push');

  Future<String?> getPlatformVersion() {
    return YmFlutterPushPlatform.instance.getPlatformVersion();
  }

  Future<String?> getRegisterInfo() {
    return YmFlutterPushPlatform.instance.getRegisterInfo();
  }
}
