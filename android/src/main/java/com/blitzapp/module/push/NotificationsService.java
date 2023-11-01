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
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
public class NotificationsService extends FirebaseMessagingService {
    public static String EXTRA_PAYLOAD = null;
    public static Map notificationData;
    public static String summaryText = "This is summary";
    private Map<String, String> map = new HashMap<String, String>();
    private static Class mainActivity;
    public static void setMainActivity(Class activity){
        mainActivity = activity;
    }
    public void processMessage(RemoteMessage remoteMessage){}
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null){
            showNotification(remoteMessage);
        } else{
            if (remoteMessage.getNotification() == null && remoteMessage.getData() != null) {
                processMessage(remoteMessage);
            }
        }
    }
    public void showNotification(RemoteMessage remoteMessage){
        String data = null;
        if(remoteMessage.getData() != null){
          data =  new JSONObject(remoteMessage.getData()).toString();
        }
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = activityManager.getAppTasks().get(0).getTaskInfo().baseActivity;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "blitz_default_chanel")
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if(remoteMessage.getNotification().getChannelId() != null){
            builder.setChannelId(remoteMessage.getNotification().getChannelId());
        }
        if(remoteMessage.getNotification() != null){
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
        }
        builder.setAutoCancel(true);

        try {
            PackageManager manager = getPackageManager();
            Resources resources = manager.getResourcesForApplication(getApplicationContext().getPackageName());
            int smallIconId = resources.getIdentifier("notifications_icon", "drawable", getApplicationContext().getPackageName());
            builder.setSmallIcon(smallIconId);
            if(remoteMessage.getNotification().getImageUrl() != null){
                URL url = new URL(remoteMessage.getNotification().getImageUrl().toString());
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                builder.setLargeIcon(image);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("BLITZ_NOTIFICATIONS__ icon not applied",e.getMessage());
        } catch (MalformedURLException e) {
            Log.w("BLITZ_NOTIFICATIONS__ iconMalformedURLException",e.getMessage());
        } catch (IOException e) {
            Log.w("BLITZ_NOTIFICATIONS__ iconIOException",e.getMessage());
        }
        Intent intent = new Intent(getApplicationContext(), cn.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(data != null){
            intent.putExtra("data",data);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(remoteMessage.getNotification().getBody());
        bigText.setBigContentTitle(remoteMessage.getNotification().getTitle());
        bigText.setSummaryText(remoteMessage.getNotification().getBody());
        if(remoteMessage.getNotification().getColor() != null){
            String colorString = remoteMessage.getNotification().getColor();
            builder.setColor(Color.parseColor(colorString));
        }
        builder.setStyle(bigText);
        NotificationManager mNotificationManager =
                (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "blitz_default_chanel";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    getPackageManager().getApplicationLabel(getApplicationInfo()),
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
        }
        mNotificationManager.notify(0,builder.build());
    }
}
