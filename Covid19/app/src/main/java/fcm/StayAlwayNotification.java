package fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import apps.envision.musicvibes.R;
import apps.envision.musicvibes.SplashScreen;

public class StayAlwayNotification
{

    public StayAlwayNotification(Context ct)
    {
        Intent i=new Intent(ct, SplashScreen.class);

        callToNotification("Stay home,Stay Safe from Covid-19",i,ct);
    }
    private void callToNotification(String msg, Intent intent1, Context ct) {
        try {


            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // PendingIntent pendingIntent = PendingIntent.getActivity(ct, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            Uri defaultSoundUri = Uri.parse("android.resource://" + ct.getPackageName() + "/" + R.raw.eventually);
            // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/");
            NotificationManager mNotificationManager = (NotificationManager) ct.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "icy_chanel";// The id of the channel.
                CharSequence name = "icy";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
                // mChannel.setSound(defaultSoundUri, attributes); // This is IMPORTANT

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ct);
                Notification notification = mBuilder.setSmallIcon(R.drawable.notification_logo).setTicker(ct.getResources().getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(false)
                        .setContentTitle(ct.getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                       // .setContentIntent(pendingIntent)
                        .setChannelId(CHANNEL_ID)
//                        .setSound(defaultSoundUri)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(ct.getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();

                notification.flags |= Notification.FLAG_NO_CLEAR
                        | Notification.FLAG_ONGOING_EVENT;
                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(590, notification);
            } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        ct);
                Notification notification = mBuilder.setSmallIcon(R.drawable.notification_logo).setTicker(ct.getResources().getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(false)
                        .setContentTitle(ct.getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        //.setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.notification_logo)
//                        .setSound(defaultSoundUri)
                        .setLargeIcon(BitmapFactory.decodeResource(ct.getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();
                mNotificationManager.notify(501, notification);

                notification.flags |= Notification.FLAG_NO_CLEAR
                        | Notification.FLAG_ONGOING_EVENT;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}



