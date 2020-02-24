package com.blitzapp.module.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RNEasyPushNotificationsModule extends ReactContextBaseJavaModule {
  public static String deviceId = null;
  public static Boolean isInit = false;
  public RNEasyPushNotificationsModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.init();
    BroadcastReceiver geoLocationReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra("data");


        sendMessage("notificationReceived",json);
      }
    };
    LocalBroadcastManager.getInstance(getReactApplicationContext()).registerReceiver(geoLocationReceiver, new IntentFilter("notificationReceived"));
  }
  @ReactMethod
  public void init(){
    try{
      Log.d("init_init",isInit.toString());
      FirebaseApp.initializeApp(getReactApplicationContext().getApplicationContext());

      isInit = true;
      Log.d("init_init",isInit.toString());
    }catch (Exception e){
      Log.d("err_noti",e.getMessage());
    }
  }
  @Override
  public String getName() {
    return "BlitzNotifications";
  }

  public void sendMessage(String name, String data) {
    try {

      Log.d("NotEvent","name");
      getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(name, data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  public static Map<String, String> bundleToMap(Bundle extras) {
    Map<String, String> map = new HashMap<String, String>();

    Set<String> ks = extras.keySet();
    Iterator<String> iterator = ks.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      map.put(key, extras.getString(key));
    }/*from   w ww .j  a  v  a 2s .c  o m*/
    return map;
  }
  @ReactMethod
  public void getIntent(Callback callback){
    if(isInit == false){
      this.init();
    }
      Intent intent = getCurrentActivity().getIntent();
      Bundle extras = intent.getExtras();
      if(extras != null){
        Map dataMap;
        dataMap = bundleToMap(extras);

        if(dataMap != null){
          JSONObject json = new JSONObject(dataMap);

          String str = json.toString();
          callback.invoke(str);
        }

      }


  }
  @ReactMethod
  public void registerForToken(final Callback onRegister){
    if(isInit == false){
      this.init();
    }
    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( getCurrentActivity(),  new OnSuccessListener<InstanceIdResult>() {
      @Override
      public void onSuccess(InstanceIdResult instanceIdResult) {
        deviceId = instanceIdResult.getToken();
        onRegister.invoke(deviceId);
      }
    });

  }
}
