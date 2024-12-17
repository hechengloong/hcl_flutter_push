import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ym_flutter_push_method_channel.dart';

abstract class YmFlutterPushPlatform extends PlatformInterface {
  /// Constructs a YmFlutterPushPlatform.
  YmFlutterPushPlatform() : super(token: _token);

  static final Object _token = Object();

  static YmFlutterPushPlatform _instance = MethodChannelYmFlutterPush();

  /// The default instance of [YmFlutterPushPlatform] to use.
  ///
  /// Defaults to [MethodChannelYmFlutterPush].
  static YmFlutterPushPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [YmFlutterPushPlatform] when
  /// they register themselves.
  static set instance(YmFlutterPushPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getRegisterInfo() {
    throw UnimplementedError('getRegisterInfo() has not been implemented.');
  }
}
