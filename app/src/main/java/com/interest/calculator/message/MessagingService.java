package com.interest.calculator.message;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.interest.calculator.R;
import com.interest.calculator.ad.WebAdActivity;
import com.interest.calculator.util.JsonUtil;
import com.interest.calculator.util.LocalDataStorageUtil;

import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/2/5
 * @description ELD消息推送服务
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "ELDMessagingService";

    public MessagingService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();
        if (!data.isEmpty()) {
            PushMessage pushMessage = JsonUtil.parseObject(JsonUtil.toJsJSONString(remoteMessage.getData()), PushMessage.class);
            assert pushMessage != null;
            createNotification(getBaseContext(), pushMessage);
        }

        // Check if message contains a notification payload.
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        if (notification != null) {
//            showNotification(getBaseContext(), notification);
//        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        LocalDataStorageUtil.putString("fcmToken", token);
    }

    private void createNotification(Context context, PushMessage pushMessageModel) {
        String channelId = context.getString(R.string.app_name);
        String notificationTitle = pushMessageModel.getPushTopic();
        if (TextUtils.isEmpty(notificationTitle)) {
            notificationTitle = channelId;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setContentTitle(notificationTitle);
        builder.setContentText(pushMessageModel.getPushContent());
        builder.setAutoCancel(true);
        long createTime = pushMessageModel.getCreateTime();
        builder.setWhen(createTime);
        String brand = Build.BRAND;
        PendingIntent intent = setPendingIntent(context, pushMessageModel);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (!TextUtils.isEmpty(brand) && brand.equalsIgnoreCase("samsung")) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            builder.setLargeIcon(bitmap);
        }
        builder.setContentIntent(intent);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            int notificationId = (int) System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(channelId, notificationManager);
            }
            notificationManager.notify(notificationId, builder.build());
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private NotificationChannel createNotificationChannel(
            String channelId,
            NotificationManager notificationManager) {
        NotificationChannel notificationChannel =
                new NotificationChannel(
                        channelId,
                        channelId,
                        NotificationManager.IMPORTANCE_DEFAULT
                );
        notificationChannel.enableLights(true); //开启指示灯，如果设备有的话。
        notificationChannel.enableVibration(true); //开启震动
        notificationChannel.setLightColor(Color.RED); // 设置指示灯颜色
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//设置是否应在锁定屏幕上显示此频道的通知
        notificationChannel.setShowBadge(true); //设置是否显示角标
        notificationChannel.setBypassDnd(true); // 设置绕过免打扰模式
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400}); //设置震动频率
        notificationChannel.setDescription(channelId);
        notificationManager.createNotificationChannel(notificationChannel);
        return notificationChannel;
    }

    private PendingIntent setPendingIntent(Context context, PushMessage data) {
        String url = data.getUrl();
        if (TextUtils.isEmpty(url)) {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
            return PendingIntent.getActivity(
                    context,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
        } else {
            // WebView打开这个Url
            return PendingIntent.getActivity(
                    context,
                    (int) System.currentTimeMillis(),
                    WebAdActivity.newIntent(context, url),
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
        }
    }
}
