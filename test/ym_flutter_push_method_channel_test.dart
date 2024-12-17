import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:ym_flutter_push/ym_flutter_push_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelYmFlutterPush platform = MethodChannelYmFlutterPush();
  const MethodChannel channel = MethodChannel('ym_flutter_push');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
