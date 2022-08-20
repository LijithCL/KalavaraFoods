//package com.ei.kalavarafoods.service;
//
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.support.v4.app.NotificationCompat;
//
//import com.ei.kalavarafoods.DBHelperDisplayItems;
//import com.ei.kalavarafoods.NotificationActivity;
//import com.ei.kalavarafoods.R;
//import com.ei.kalavarafoods.db.notificaiton.NotificationDbContract;
//import com.ei.kalavarafoods.db.notificaiton.model.NotificationItem;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class MMFirebaseMessagingService extends FirebaseMessagingService implements NotificationDbContract.TableConstants {
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        if (null != remoteMessage.getData()) {
//            saveNotification(remoteMessage);
//        }
//
//        showLocalNotification(remoteMessage);
//    }
//
//    private void showLocalNotification(RemoteMessage remoteMessage) {
//        String title = remoteMessage.getNotification().getTitle();
//        String message = remoteMessage.getNotification().getBody();
//        String imageUrl = null;
//        if (null != remoteMessage.getData() && null != remoteMessage.getData().get("image"))
//            imageUrl = remoteMessage.getData().get("image");
//        if (null != imageUrl)
//            sendLocalImageNotification(getNotificationBuilder(title, message), imageUrl);
//        else
//            sendLocalNotification(getNotificationBuilder(title, message));
//    }
//
//    private NotificationCompat.Builder getNotificationBuilder(String title, String message) {
//        return new NotificationCompat.Builder(this).setSmallIcon(R.drawable.kf_logo)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(true);
//    }
//
//    private void sendLocalNotification(NotificationCompat.Builder notificationBuilder) {
//        triggerLocalNotification(notificationBuilder);
//    }
//
//    private void sendLocalImageNotification(NotificationCompat.Builder notificationBuilder, String imageUrl) {
//        Bitmap bitmap = getBitmapfromUrl(imageUrl);
//        notificationBuilder.setLargeIcon(bitmap)/*Notification icon image*/
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(bitmap));/*Notification with Image*/
//        triggerLocalNotification(notificationBuilder);
//    }
//
//    private void triggerLocalNotification(NotificationCompat.Builder notificationBuilder) {
//        Intent intent = new Intent(this, NotificationActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//        notificationBuilder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//
//    private Bitmap getBitmapfromUrl(String imageUrl) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(input);
//            return bitmap;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private void saveNotification(RemoteMessage remoteMessage) {
//        DBHelperDisplayItems dbHelperDisplayItems = new DBHelperDisplayItems(getApplicationContext());
//        NotificationItem notificationItem = new NotificationItem();
//        notificationItem.setTitle(remoteMessage.getNotification().getTitle());
//        notificationItem.setMessage(remoteMessage.getNotification().getBody());
//        notificationItem.setImage(remoteMessage.getData().get(COLUMN_IMAGE));
//        dbHelperDisplayItems.insertNotification(notificationItem);
//    }
//}
