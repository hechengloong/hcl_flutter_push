import 'package:flutter_test/flutter_test.dart';
import 'package:ym_flutter_push/ym_flutter_push.dart';
import 'package:ym_flutter_push/ym_flutter_push_platform_interface.dart';
import 'package:ym_flutter_push/ym_flutter_push_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockYmFlutterPushPlatform
    with MockPlatformInterfaceMixin
    implements YmFlutterPushPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final YmFlutterPushPlatform initialPlatform = YmFlutterPushPlatform.instance;

  test('$MethodChannelYmFlutterPush is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelYmFlutterPush>());
  });

  test('getPlatformVersion', () async {
    YmFlutterPush ymFlutterPushPlugin = YmFlutterPush();
    MockYmFlutterPushPlatform fakePlatform = MockYmFlutterPushPlatform();
    YmFlutterPushPlatform.instance = fakePlatform;

    expect(await ymFlutterPushPlugin.getPlatformVersion(), '42');
  });
}
