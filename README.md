# react-native-easy-push-notifications
 
React-Native-Easy-Push-Notifications is developed to help the react-native developers in speeding-up their development process. This package leverages the developer from implementing notificaitons clasically. The implementation is kept point-to-point to provide easy usage to the developer. Follow the guide below to get you up and running with the notifications:

### Installation
If you prefer **npm**,
```sh
$ npm install react-native-easy-push-notifications --save
```

If you prefer **yarn**,
```sh
$ yarn add react-native-easy-push-notifications
```
## Setup FireBase Project

  - Go to firebase console https://firebase.google.com/
  - Log-in to your google account and add a project with any name you like.
  - After successfull creation of the project you will be shown homepage of your project.

### Android
Android setup is a two step process. The first step can be done in two ways:
#### First Step:
##### Way 1:
  - Go to settings -> Project Settings -> scroll to bottom and add an android application.
  - Add android package name. ( You can find package name in your Manifest.xml )
  - Click on the Register button.
  - Download the google-services.json and place it in the android -> app .
  - Click on the Next button and go to your Project level build.gradle.
  - Inside your <Project>/build.gradle:
      - Add  ```firebase_messaging = '20.1.0'``` to buildscript -> ext.
      - Add  ```google()  // Google's Maven repository``` to buildscript -> repositories.
      - Add ```classpath 'com.google.gms:google-services:4.3.3' ``` to buildscript -> dependencies.
      - Add ```google()  // Google's Maven repository ``` to allprojects -> repositories.
  - Go to App level build.gradle and add following lines:
      - Add ```apply plugin: 'com.google.gms.google-services' ``` after ``` apply plugin: "com.android.application"``` .
      - Add ```implementation 'com.google.firebase:firebase-messaging:20.1.0' ``` to dependencies.
      - Click on the Next button and then continue to the console.

##### Way 2:
  - Open project in Android studio.
  -  login to your google account.
  -  Go to Tools -> Firebase -> Cloud Messaging -> Setup Firebase Cloud Messaging from Android studio options.
  -  Click on **Connect To Firebase** button and from existing FireBase project dropdown, select the project we created in setup firebase section. Click on connect
  -  Click on **Add FCM to your app** button and then click on **Accept changes** button shown in the dialog.
  -  In your Project level build.gradle, Add  ```firebase_messaging = '20.1.0'``` to buildscript -> ext.
#### Second Step:
  - Open Manifest.xml and following line of code to it:
      - Add ```android:launchMode="singleTop"``` to Activity tag with name: .MainActivity.
      - Add following service tag to Manifest.xml:
      ```sh
      <service android:name="com.blitzapp.module.push.NotificationsService"
                android:exported="false">
        <intent-filter>
            <action android:name="com.google.firebase.MESSAGING_EVENT" />
        </intent-filter>
    </service>
      ```
     - Add following meta-data tag to Manifest.xml:
     ```sh
     <meta-data android:name="com.google.firebase.messaging.default_notification_icon"
                android:resource="@drawable/notifications_icon" />
    ```
  - **notifications_icon** is required. You can generate this icon from here: [Icon generate link](https://romannurik.github.io/AndroidAssetStudio/icons-notification.html#source.type=clipart&source.clipart=attach_money&source.space.trim=1&source.space.pad=0.05&name=ic_stat_attach_money)
  - Place the icon generated in the drawable folder.
  - Go to the MainActivity.java and add following lines of code:
    - Add this to imports: ```import com.blitzapp.module.push.RNEasyPushNotificationsModule;```
    - Add this segment to onCreate method:
    ```sh
    if(RNEasyPushNotificationsModule.activityToOpen == null){
      RNEasyPushNotificationsModule.activityToOpen = this;
    }

    Intent i = getIntent();
    Bundle extras = i.getExtras();
    if (extras != null) {
      for (String key : extras.keySet()) {
        Object value = extras.get(key);
        Log.d("MainActivity", "Extras received at onCreate:  Key: " + key + " Value: " + value);
      }

      String title = extras.getString("title");
      String message = extras.getString("body");
      if (message != null && message.length() > 0) {
        RNEasyPushNotificationsModule.setExtras(extras);
      }
    }
    ```

### ios
IOS setup is a two step process.
#### First Step:
  - Go to settings -> Project Settings -> scroll to bottom and add an ios applicaton.
  - Add your ios app bundle identifer and click on the Next button.
  - Download the GoogleService-Info.plist and drag it into your project in Xcode.
  - Click on the Next, then Next and then Continue to the console.
#### Second Step:
  - Go to developer.apple.com and then go to certificates,Ids and profiles -> identifiers -> your app identifier -> Push Notifications and enable it.
  - Open your project in Xcode and go to Signing & capabilities -> Capability -> enable Push Notifications.
  - Open your podfile and Add ```   pod 'RNEasyPushNotificationsModule', :path => '../node_modules/react-native-easy-push-notifications' ```.
  - Open terminal at root of your project and run following commands:
  ```sh
  $ cd ios
  $ pod install
  ```
  - Add ``` #import "RNEasyPushNotificationsModule.h" ``` to the imports section.
  - Open your app's Appdelegate.h and add the following code to the interface section:
  ```sh
  {
  RNEasyPushNotificationsModule *nModule;
  NSDictionary *dic;
  }
  -(NSDictionary *)getLaunchOptions;
+(RCTBridge *) getSharedBridge;
  ```
  - Open your app's Appdelegate.m and add the following code to the didFinishLaunchingWithOptions method:
  ```sh
if (launchOptions!=nil) {
NSMutableDictionary *userInfo = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
NSLog(@"userInfo===%@", userInfo);
if (userInfo.count>=1) {
NSDictionary *apsInfo = [userInfo objectForKey:@"aps"];
NSLog(@"Received apsInfo Badge: %@", userInfo);
RNEasyPushNotificationsModule *nModule = [RNEasyPushNotificationsModule allocWithZone: nil];
[nModule setRemoteNotification:userInfo];
    }
}
  ```

### React Native
After setting up our project on both android and ios, we are all set to consume this module in react native. Following are the usage of functions exposed by this package:

```sh
import reactNativeEasyPushNotifications from 'react-native-easy-push-notifications';

componentDidMount(){
    reactNativeEasyPushNotifications.getDeviceId( deviceId => {
            console.log("My device id ", deviceId);
        // This method gives the device id which is returned by the firebase
        })
       
    reactNativeEasyPushNotifications.onMessageReceived(notif => {
            console.log("onMessageReceived:", notif);
        // This method is triggered whenever the app is in foreground and we receive the notification
        })
       
    reactNativeEasyPushNotifications.getLastNotificationData(notif => {
            console.log("getLastNotificationData:", notif);
        // This method is triggered whenever the user taps on the notification
        })
}
```




### Todos
We aim to make this package even more robust and powerful by adding following features in the upcoming releases:
 - Reply from notification
 - Add support to view image in notification
 - Add an example project
 - Notification testing dashboard

License
----

MIT