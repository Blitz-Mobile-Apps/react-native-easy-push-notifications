package com.blitzapp.module.push;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
public class RNEasyPushNotificationsModule extends ReactContextBaseJavaModule implements ActivityEventListener{
  static ReactApplicationContext reactApplicationContext;
  public RNEasyPushNotificationsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    reactApplicationContext = reactContext;
    initializeApp();
    reactContext.addActivityEventListener(this);
  }
  public void initializeApp(){
    if(FirebaseApp.getInstance() != null) {
      FirebaseApp.initializeApp(getReactApplicationContext().getApplicationContext());
    }
  }
  @Override
  public String getName() {
    return "BlitzNotifications";
  }
  public Boolean shouldAskForPermission(){
    if (Build.VERSION.SDK_INT >= 33) {
      int permissionState = ContextCompat.checkSelfPermission(reactApplicationContext, android.Manifest.permission.POST_NOTIFICATIONS);
      return permissionState == PackageManager.PERMISSION_DENIED;
    }
    return false;
  }

  @ReactMethod
  public void requestPermission(){
    if (shouldAskForPermission()) {
      ActivityCompat.requestPermissions(reactApplicationContext.getCurrentActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
    }
  }
  @ReactMethod
  public void registerForToken(Promise promise){
    initializeApp();
    if(shouldAskForPermission()){
        promise.reject("Fetching FCM registration token failed", "Please allow permission for notifications");
        return;
    }
    FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
              @Override
              public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                  promise.reject("Fetching FCM registration token failed", task.getException());
                  return;
                }
                String token = task.getResult();
                promise.resolve(token);
              }
            });
  }
  @Override
  public void onActivityResult(Activity activity, int i, int i1, @Nullable Intent intent) {

  }
  @Override
  public void onNewIntent(Intent intent) {

  }
}
