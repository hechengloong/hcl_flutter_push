# 项目说明
## 起因
   目前github上面搜索发现没有合适的flutter和android 推送原生插件，所以自己参考和借鉴了(https://github.com/taoweiji/MixPush)项目，这个mixpush太老了，我这边更新了不少，去除了不需要的部分,更新了最新的jar和aar。
   大家有什么问题可以提issues。

## 功能
    1、支持vivo、oppo、华为、小米
    2、目前支持获取到regId
    3、支持服务端发送推送打开应用首页和子页面（可以附加不同的schema或者intent参数）

## 集成方法
### 1、库不需要修改，直接使用项目下的example

### 2、修改example下如下地方    
    a、在android目录下的gradle.properties 各个值即可：


        EXAMPLE_APPLICATION_ID=com.xiaogongqiu.app
        EXAMPLE_VIVO_APP_ID=XX
        EXAMPLE_VIVO_APP_KEY=XX
        EXAMPLE_MI_APP_ID=XX
        EXAMPLE_MI_APP_KEY=XX
        EXAMPLE_OPPO_APP_KEY=XX
        EXAMPLE_OPPO_APP_SECRET=XX
        EXAMPLE_MEIZU_APP_ID=XX
        EXAMPLE_MEIZU_APP_KEY=XX

    b、修改android app目录下build.gradle android.jks是自己的apk签名，改成自己的就行:
         signingConfigs {
            release {
                keyAlias 'key0'
                keyPassword "xxxx"
                storePassword "xxxx"
                storeFile file('android.jks')
            }
        }

    c、替换改android app 目录下华为推送配置文件 arconnect-services.json 文件，替换成自己的。

    d、修改app/src/AndroidManifest.xml 包名改成自己的（这个是属于小米的部分配置）:
         <permission
            android:name="com.xiaogongqiu.app.permission.MIPUSH_RECEIVE"
            android:protectionLevel="signature" />
        <uses-permission android:name="com.xiaogongqiu.app.permission.MIPUSH_RECEIVE" />

    e、 这个属于定义打开的activity的 scheme，可以自己定义，不过小米、vivi、oppo、华为有的使用时intent参数有的是使用scheme。
       <intent-filter>
                <action android:name="android.intent.action.VIEW"/>
                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>
                <data
                    android:scheme="ymappscheme"
                    android:host="com.xiaogongqiu.app"
                    android:path="/message"
                    />
            </intent-filter>

