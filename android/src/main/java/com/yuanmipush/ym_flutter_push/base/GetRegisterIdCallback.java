package com.yuanmipush.ym_flutter_push.base;

import androidx.annotation.Nullable;

public abstract class GetRegisterIdCallback {
    public abstract void callback(@Nullable MixPushPlatform platform);
}