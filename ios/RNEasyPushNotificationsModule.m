#import "RNEasyPushNotificationsModule.h"
#import <React/RCTConvert.h>
#import "FirebaseMessaging.h"
#import "Firebase.h"
@import UserNotifications;
extern NSString *device_id = NULL;
extern NSDictionary *remoteNotification = @"NULL";
 
@implementation RNEasyPushNotificationsModule
 
RCT_EXPORT_MODULE(BlitzNotifications);
 
+ (id)allocWithZone:(NSZone *)zone {
  static RNEasyPushNotificationsModule *sharedInstance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    sharedInstance = [super allocWithZone:zone];
  });
  return sharedInstance;
}
 
- (NSArray<NSString *> *)supportedEvents {
  return @[@"deviceRegistered",@"notificationReceived",@"onNotificationTap"];
}

RCT_EXPORT_METHOD(removeAllDeliveredNotifications) {
    if (@available(iOS 10.0, *)) {
        UNUserNotificationCenter *notificationCenter = [UNUserNotificationCenter currentNotificationCenter];
        if (notificationCenter != nil) {
            [[UNUserNotificationCenter currentNotificationCenter] removeAllDeliveredNotifications];
        }
    }
}

RCT_EXPORT_METHOD(getLastNotificationData:(RCTResponseSenderBlock)callback)
{
  NSLog(@"notificationReceived getLastNotificationData %@",remoteNotification);
    if(remoteNotification != @"NULL"){

                  callback(@[remoteNotification]);
            remoteNotification = @"NULL";
    }
  
}
RCT_EXPORT_METHOD(registerForToken)
{
 
  dispatch_async(dispatch_get_main_queue(), ^{
    
      if(device_id == NULL){
      FIRApp *firApp = [FIRApp defaultApp];
      if(firApp ==  nil){
    [FIRApp configure];
      }
     UIApplication *application = UIApplication.sharedApplication;
    [FIRMessaging messaging].delegate = self;
    [FIRMessaging messaging].shouldEstablishDirectChannel = YES;
    
   if ([UNUserNotificationCenter class] != nil) {
     [UNUserNotificationCenter currentNotificationCenter].delegate = self;
     UNAuthorizationOptions authOptions = UNAuthorizationOptionAlert |
         UNAuthorizationOptionSound | UNAuthorizationOptionBadge;
     [[UNUserNotificationCenter currentNotificationCenter]
         requestAuthorizationWithOptions:authOptions
         completionHandler:^(BOOL granted, NSError * _Nullable error) {
          
         }];
   } else {
     // iOS 10 notifications aren't available; fall back to iOS 8-9 notifications.
   
     UIUserNotificationType allNotificationTypes =
     (UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge);
     UIUserNotificationSettings *settings =
     [UIUserNotificationSettings settingsForTypes:allNotificationTypes categories:nil];
     [application registerUserNotificationSettings:settings];
   }
 
   [application registerForRemoteNotifications];
      } else {
          [self sendEventWithName:@"deviceRegistered" body:device_id];
      }
     });
  
}
 
// [START receive_message]
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification {
        remoteNotification = notification;
    NSLog(@"notificationReceived didReceiveRemoteNotification : %@", remoteNotification);
  [self sendEventWithName:@"notificationReceived" body: remoteNotification];
}
 
- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
      remoteNotification = notification;
  NSLog(@"notificationReceived didReceiveRemoteNotification with completionhandler: %@", remoteNotification);
  [self sendEventWithName:@"onNotificationTap" body: remoteNotification];
  completionHandler(UIBackgroundFetchResultNewData);
}
// [END receive_message]
 
// [START ios_10_message_handling]
// Receive displayed notifications for iOS 10 devices.
// Handle incoming notification messages while app is in the foreground.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
 
  completionHandler(UNAuthorizationOptionSound | UNAuthorizationOptionAlert | UNAuthorizationOptionBadge);
  NSLog(@"notificationReceived userNotificationCenter with UNNotificationPresentationOptions: %@", remoteNotification);
  // when we reveive it in foreground
    remoteNotification = notification.request.content.userInfo;
  [self sendEventWithName:@"notificationReceived" body: remoteNotification];
  completionHandler(UNNotificationPresentationOptionNone);
}
 
// Handle notification messages after display notification is tapped by the user.
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)notification
         withCompletionHandler:(void(^)(void))completionHandler {
  /// when we tap on notif and app is in foreground
    NSLog(@"notificationReceived didReceiveRemoteNotification with completionhandler: %@", notification);
  remoteNotification = notification.notification.request.content.userInfo;
  [self sendEventWithName:@"onNotificationTap" body: remoteNotification];
  completionHandler();
}
 
// [END ios_10_message_handling]
 
// [START refresh_token]
- (void)messaging:(FIRMessaging *)messaging didReceiveRegistrationToken:(NSString *)fcmToken {
  device_id = fcmToken;
    NSLog(@"notificationReceived didReceiveMessage with device_id: %@", device_id);
  [self sendEventWithName:@"deviceRegistered" body:fcmToken];
  
}
// [END refresh_token]
 
// [START ios_10_data_message]
// Receive data messages on iOS 10+ directly from FCM (bypassing APNs) when the app is in the foreground.
// To enable direct data messages, you can set [Messaging messaging].shouldEstablishDirectChannel to YES.
- (void)messaging:(FIRMessaging *)messaging didReceiveMessage:(FIRMessagingRemoteMessage *)notification {
  remoteNotification = notification;
  NSLog(@"notificationReceived didReceiveMessage with didReceiveMessage: %@", remoteNotification);
  [self sendEventWithName:@"notificationReceived" body: remoteNotification];
}

 
// [END ios_10_data_message]
//- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
//{
//    if ( application.applicationState == UIApplicationStateInactive || application.applicationState == UIApplicationStateBackground  )
//    {
//         //opened from a push notification when the app was on background
//        NSLog(@"userInfo->%@", [userInfo objectForKey:@"aps"]);
//
//    }
//}

-(void) setRemoteNotification:(NSDictionary *) notification
{   NSLog(@"setRemoteNotification %@",notification);
    remoteNotification = notification;
      [self sendEventWithName:@"onNotificationTap" body: remoteNotification];
  
//  [self sendEventWithName:@"recievedFDL" body:did];
//  dynamicLink = did;
} 

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
  NSLog(@"Unable to register for remote notifications: %@", error);
}
 
- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
  NSLog(@"APNs device token retrieved: %@", deviceToken);
    [self sendEventWithName:@"deviceRegistered" body:deviceToken];
}
@end
