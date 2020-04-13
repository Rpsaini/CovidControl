package fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;
import java.util.Map;
import androidx.core.app.NotificationCompat;
import communication.SaveImpPrefrences;

import apps.envision.musicvibes.R;
import apps.envision.musicvibes.SplashScreen;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private SaveImpPrefrences imp;
    private int notifyID = 0;
    public static MediaPlayer mediaPlayer;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        System.out.println("remote msg========"+remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        JSONObject object = new JSONObject(data);
        imp = new SaveImpPrefrences();

        Intent intent = new Intent(this, SplashScreen.class);
        callToNotification(object,intent);
    }


    private  void callToNotification(JSONObject obj,Intent intent)
    {
        try {
             String msg=obj.getString("message");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
             Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.eventually);
           // Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/");
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String CHANNEL_ID = "icy_chanel";// The id of the channel.
                CharSequence name = "icy";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
                mChannel.setSound(defaultSoundUri, attributes); // This is IMPORTANT

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                Notification notification = mBuilder.setSmallIcon(R.drawable.notification_logo).setTicker(this.getResources().getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentIntent(pendingIntent)
                        .setChannelId(CHANNEL_ID)
                        .setSound(defaultSoundUri)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();

                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(490, notification);
            } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        this);
                Notification notification = mBuilder.setSmallIcon(R.drawable.notification_logo).setTicker(getResources().getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setSound(defaultSoundUri)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();
                mNotificationManager.notify(401, notification);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

//        Intent intent1 = new Intent(this, GeofenceBroadcastReceiver.class);
//        intent1.putExtra("booking_id", booking_id);
//        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), Integer.parseInt(booking_id), intent1, 0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (60 * 1000), pendingIntent1);


    }
}
