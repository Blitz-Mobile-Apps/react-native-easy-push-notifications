
#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif
#import <React/RCTEventEmitter.h>
#import <UserNotifications/UserNotifications.h>
#import <FirebaseMessaging/FIRMessaging.h>
@interface RNEasyPushNotificationsModule : RCTEventEmitter  <UIApplicationDelegate, RCTBridgeModule, UNUserNotificationCenterDelegate>

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler;
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
       willPresentNotification:(UNNotification *)notification
         withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler;
- (void)userNotificationCenter:(UNUserNotificationCenter *)center
didReceiveNotificationResponse:(UNNotificationResponse *)notification
         withCompletionHandler:(void(^)(void))completionHandler;
- (void)messaging:(FIRMessaging *)messaging didReceiveMessage:(FIRMessagingRemoteMessage *)notification;
@end
  
