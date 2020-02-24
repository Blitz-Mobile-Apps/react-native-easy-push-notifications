using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Easy.Push.Notifications.RNEasyPushNotifications
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNEasyPushNotificationsModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNEasyPushNotificationsModule"/>.
        /// </summary>
        internal RNEasyPushNotificationsModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNEasyPushNotifications";
            }
        }
    }
}
