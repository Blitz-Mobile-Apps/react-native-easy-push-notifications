
package com.blitzapp.module.push;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNEasyPushNotificationsModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNEasyPushNotificationsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "BlitzEasyPush";
  }
}