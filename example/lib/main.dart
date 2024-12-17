import 'package:app_links/app_links.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:logger/logger.dart';
import 'package:ym_flutter_push/ym_flutter_push.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _registerId = 'Unknown';
  String _data = "";
  Logger logger = Logger();

  Future<void> init() async {
    YmFlutterPush().eventChannel.receiveBroadcastStream().listen((event) {
      logger.i('-------------recv push message from remote---: $event');
      setState(() {
        _data = event.toString();
      });
    });

    var registerId = await YmFlutterPush().getRegisterInfo();
    logger.i('-------------registerId: $registerId');
    setState(() {
      _registerId = registerId.toString();
    });

    // Subscribe to all events (initial link and further)
    AppLinks().uriLinkStream.listen((uri) {
      // Do something (navigation, ...)
      logger.i('-------------uri: $uri');
      setState(() {
        _data = uri.toString();
      });
    });
  }

  @override
  void initState() {
    init();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text('Running on: $_registerId\n'),
              SizedBox(
                height: 10,
              ),
              Text('Running on: $_data\n')
            ],
          ),
        ),
      ),
    );
  }
}
