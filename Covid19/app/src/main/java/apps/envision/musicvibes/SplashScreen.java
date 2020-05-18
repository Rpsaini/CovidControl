package apps.envision.musicvibes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import communication.SaveImpPrefrences;
import fcm.FusedLocationNew;
import fcm.StayAlwayNotification;
import apps.envision.musicvibes.R;


import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;
import java.util.Random;

public class SplashScreen extends AppCompatActivity {

    private SaveImpPrefrences imp;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
//        FirebaseApp.initializeApp(this);
        imp = new SaveImpPrefrences();
        new StayAlwayNotification(this);

        try {


            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i = 0; i < recentTasks.size(); i++)
            {

                System.out.println("executed apps==="+recentTasks.get(i).id);

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    public native String stringFromJNI();


    private Runnable runnable;
    private   Handler hnd;
    private  int x=0;
    private void initiate() {
         x=0;
        final FusedLocationNew fusedLocationNew = new FusedLocationNew(this);
        fusedLocationNew.startLocationUpdates();
        hnd = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if(FusedLocationNew.mCurrentLocation == null) {
                    hnd.postDelayed(this, 1000);

                }
                else {
                    if(x==0)
                    {
                        x=1;

                        SaveImpPrefrences imp=new SaveImpPrefrences();
                        if(imp.reterivePrefrence(SplashScreen.this,"isQuarantine").toString().equalsIgnoreCase("0"))
                        {
                            Intent i = new Intent(SplashScreen.this, MobileNumberActivity.class);
                            startActivity(i);

                        }
                        else
                        {
                            Intent i = new Intent(SplashScreen.this, MainActivityNew.class);
                            startActivity(i);

                        }
                        finish();

                    }

                }
            }
        };

        hnd.postDelayed(runnable, 0);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    private int counter = 0;

    private void generateDeviceToken() {
        final String deviceToken = imp.reterivePrefrence(SplashScreen.this, "device_token") + "";

        System.out.println("device token=="+deviceToken);
        if (deviceToken.equalsIgnoreCase("") || deviceToken.equalsIgnoreCase("0")) {
            counter++;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (counter <= 15) {
                        String CurrentToken = FirebaseInstanceId.getInstance().getToken();
                        if (CurrentToken == null) {
                            generateDeviceToken();
                        }
                        else
                        {
                            imp.savePrefrencesData(SplashScreen.this, CurrentToken, "device_token");
                            initiate();
                        }

                    }
                    else
                        {
                        counter = 20;
                        if (deviceToken.equalsIgnoreCase("") || deviceToken.equalsIgnoreCase("0")) {
                            imp.savePrefrencesData(SplashScreen.this, generateRandomString(), "device_token");
                        }
                        initiate();
                    }


                }
            }, 1000);
        } else {
            initiate();
        }
    }


    private String generateRandomString() {
        char[] chars = "ICUdefghijklmnopqrstuvwxyzramjiqwerty".toCharArray();
        StringBuilder sb = new StringBuilder(20);
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return "custom-token" + output;
    }


    private void showlocationEnablePop(int requestcode) {
        // Here, thisActivity is the current activity

        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashScreen.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {

            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    111);


        } else {
            generateDeviceToken();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkInternetState()) {
            showlocationEnablePop(111);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 111: {
                // If request is cancelled, the result arrays are empty.
                if (ContextCompat.checkSelfPermission(SplashScreen.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    generateDeviceToken();
                } else {


                    showlocationEnablePop(111);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public boolean checkInternetState() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                return true;
            } else {

                Toast.makeText(SplashScreen.this,"Please connect to working internet.",Toast.LENGTH_LONG).show();

                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




}



