import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ym_flutter_push_platform_interface.dart';

/// An implementation of [YmFlutterPushPlatform] that uses method channels.
class MethodChannelYmFlutterPush extends YmFlutterPushPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('ym_flutter_push');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> getRegisterInfo() async {
    final registerId =
        await methodChannel.invokeMethod<String>('getRegisterInfo');
    return registerId;
  }
}
