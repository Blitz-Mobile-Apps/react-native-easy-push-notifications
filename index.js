import {
    NativeModules,
    NativeEventEmitter,
    Platform
} from "react-native"
const notificationModule = NativeModules.BlitzNotifications
const eventEmitter = new NativeEventEmitter(notificationModule);


export default {
    getDeviceId: (callback) => {
        if (Platform.OS === 'android') {
            if (notificationModule) {
                if (notificationModule.registerForToken) {
                    notificationModule.registerForToken(deviceId => {
                        callback(deviceId)
                    })
                } 
            }
        } else {
            let event = eventEmitter.addListener("deviceRegisteredIOS", deviceId => {
                callback(deviceId)
                eventEmitter.removeSubscription(event)
            })
            notificationModule.registerForToken()
        }
    },
    getLastNotificationData: (callback, errorCallback) => {
        if (Platform.OS === 'android') {
            notificationModule.getLastNotificationData(notification => {
                console.log(notification)
                try {
                    if (typeof notification === 'string') {
                        let data = JSON.parse(notification)
                        callback(data)
                    } else {
                        throw "Invalid data provided";
                    }
                } catch (e) {
                    errorCallback(e)
                }
            })
        } else {
            notificationModule.getLastNotificationData(notification => {
                console.log('bno ',notification)
                // try {
                //     if (typeof notification === 'string') {
                //         let data = JSON.parse(notification)
                //         callback(data)
                //     } else {
                //         throw "Invalid data provided";
                //     }
                // } catch (e) {
                //     errorCallback(e)
                // }
            })
        }
    },
    onMessageReceived: (callback) => {
            eventEmitter.addListener('notificationReceived', (event) => {
                console.log(event)
                if (event) {
                    let data = event
                    callback(JSON.parse(data))
                }
            })

    },
    onNotificationTapped: (callback) => {
        if (Platform.OS === 'android') {
            eventEmitter.addListener('onNotificationTapped', (event) => {
                callback(event)
            })
        } else {
            console.warn('getLastNotificationData is only available on android platform')
        }
    }

}