package com.blitzapp.module.push;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class NotificationsService extends FirebaseMessagingService {
    public static RemoteMessage message;
    public static String EXTRA_PAYLOAD = null;
    public static Map notificationData;
    public static String summaryText = "This is summary";
    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        message = remoteMessage;

        showNotification();
        sendMessage();
    }
    private void sendMessage() {
        try {
            Intent intent = new Intent("notificationReceived");
            if(message.getData() != null){
                intent.putExtra("data",new JSONObject(message.getData()).toString());
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Firebase service", e.getMessage());
        }
    }
    @SuppressLint("NewApi")
    public void showNotification(){
        if(message.getData() != null){
            EXTRA_PAYLOAD = message.getData().toString();
        }
        ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent i = new Intent(getApplicationContext(),cn.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message.getNotification().getBody());
        bigText.setBigContentTitle(message.getNotification().getTitle());
        bigText.setSummaryText(message.getNotification().getTitle());
        if(message.getNotification().getColor() != null){
            String colorString = message.getNotification().getColor();
            mBuilder.setColor(Color.parseColor(colorString));
        }
        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setSmallIcon(R.mipmap.notifications_icon);
        mBuilder.setContentTitle(message.getNotification().getTitle());
        mBuilder.setContentText(message.getNotification().getBody());
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        if(message.getNotification().getImageUrl() != null){
            try {
                URL url = new URL(message.getNotification().getImageUrl().toString());
                Log.d("IMAGE_URL",url.toString());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                mBuilder.setLargeIcon(image);
            } catch(IOException e) {

            }
        }


        mNotificationManager =
                (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getPackageManager().getApplicationLabel(getApplicationInfo()),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        mBuilder.setPriority(10);

        mNotificationManager.notify(0, mBuilder.build());


    }
}
