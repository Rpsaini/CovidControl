package fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;

import apps.envision.musicvibes.R;
import apps.envision.musicvibes.SplashScreen;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(final Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            // String errorMessage = GeofenceStatusCodes.getErrorString(geofencingEvent.getErrorCode());
            //Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
            return;
        }
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String quarantinId = triggeringGeofences.get(0).getRequestId();
            enterOrExitQuarantine(context, quarantinId, "enter");


        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String quarantinId = triggeringGeofences.get(0).getRequestId();
            enterOrExitQuarantine(context, quarantinId, "exit");

        }
    }

    private void enterOrExitQuarantine(final Context ct, String requestid, String type) {
        String mobile = "0";
        String userdata = new SaveImpPrefrences().reterivePrefrence(ct, "user_data").toString();
        System.out.println("user data===" + userdata);
        if (userdata.equalsIgnoreCase("0")) {
            mobile = "0";
        } else {
            try {
                JSONObject obj = new JSONObject(userdata);
                mobile = obj.getString("mobile");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        playSond(ct, requestid, mobile, type);
        final Map<String, String> m = new HashMap<>();
        m.put("request_id", requestid + "");
        m.put("mobile", mobile);
        m.put("type", type);
        if (FusedLocationNew.mCurrentLocation != null) {
            m.put("lat", FusedLocationNew.mCurrentLocation.getLatitude() + "");
            m.put("lng", FusedLocationNew.mCurrentLocation.getLongitude() + "");

        } else {
            m.put("lat", "0");
            m.put("lng", "0");

        }

        System.out.println("quarantine data===" + m);
        new ServerHandler().sendToServer(ct, "enter_qurantine_zone", m, 1, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {
            }
        });
    }

    static MediaPlayer mPlayer2;

    private void playSond(Context ct, String quarantineId, String mobile, String type) {

        stopSound();

        mPlayer2 = MediaPlayer.create(ct, R.raw.buzzer);
        mPlayer2.start();

        Vibrator v = (Vibrator) ct.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(1500);
        }


        String isQuarantine = new SaveImpPrefrences().reterivePrefrence(ct, "isQuarantine").toString();
        if (type.equalsIgnoreCase("enter"))
        {
            if(isQuarantine.equalsIgnoreCase("yes"))
            {
                if (quarantineId.equalsIgnoreCase(mobile)) //quarantine user
                {
                    Intent intent = new Intent(ct, SplashScreen.class);
                    stopSound();
                  //  callToNotification("Dear User, You are under your quarantined area thanks for the support.", intent, ct);
                }

            }
            else // normal user
                {
                Intent intent = new Intent(ct, SplashScreen.class);
                intent.putExtra("isaction", "no");
                callToNotification("Dear User, You are entered in quarantined area. There must be some covid19 suspected person around your 50 meter area. Please be carefull!!!", intent, ct);

            }


        } else //exit fro quarantine
        {

            if (isQuarantine.equalsIgnoreCase("yes"))
            {
                if (quarantineId.equalsIgnoreCase(mobile)) //Quarantine user
                {
                    Intent intent = new Intent(ct, SplashScreen.class);
                    callToNotification("Dear User, You are not allowed to leave the quarantined area marked by officials. Please go under the marked area!!!", intent, ct);
                }

            } else  //normal user
                {
                Intent intent = new Intent(ct, SplashScreen.class);
                intent.putExtra("isaction", "no");
                callToNotification("Dear User, You are exited from quarantined area. You are safe now!!!", intent, ct);

            }


        }


    }

    private void stopSound() {
        if (mPlayer2 != null) {
            if (mPlayer2.isPlaying()) {
                mPlayer2.stop();
            }
        }
    }



    private void callToNotification(String msg, Intent intent, Context ct) {
        try {

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(ct, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
                        .setAutoCancel(true)
                        .setContentTitle(ct.getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentIntent(pendingIntent)
                        .setChannelId(CHANNEL_ID)
//                        .setSound(defaultSoundUri)
                        .setSmallIcon(R.drawable.notification_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(ct.getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();

                mNotificationManager.createNotificationChannel(mChannel);
                mNotificationManager.notify(490, notification);
            } else {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                        ct);
                Notification notification = mBuilder.setSmallIcon(R.drawable.notification_logo).setTicker(ct.getResources().getString(R.string.app_name)).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(ct.getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.notification_logo)
//                        .setSound(defaultSoundUri)
                        .setLargeIcon(BitmapFactory.decodeResource(ct.getResources(), R.mipmap.ic_launcher))
                        .setContentText(msg).build();
                mNotificationManager.notify(401, notification);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}