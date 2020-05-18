package fcm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import apps.envision.musicvibes.R;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;


public class LocationTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Context context;

    private static final String LOG_TAG="LOCATION TRACKER";

    public LocationTracker(Context context){
        this.context = context;
        buildGoogleApiClient();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //Disconnect
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        try {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        catch (Exception e)
        {
            onlocationupdate(2,2);//Gps Connnection issue
            e.printStackTrace();
        }
    }

    public void execute()
    {

        try {
            LocationManager service = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!enabled) {
                onlocationupdate(-1, -1);
                playSond(context, "gps");
            } else {
                startConnection();

                System.out.println("insdide else==="+checkInternetState(context));
                if (!checkInternetState(context))
                {
                    playSond(context, "internet");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void startConnection(){
        mGoogleApiClient.connect();
    }


    public   void trackLocation(Location location) {
        if (location!=null)
        {
            Log.i("TRACKING LAT", String.valueOf(location.getLatitude()));
            Log.i("TRACKING LON", String.valueOf(location.getLongitude()));
            onlocationupdate(location.getLatitude(),location.getLongitude());
        }
        else
        {
            Log.i("TRACKING LAT", "Null location received");
            onlocationupdate(0,0);//location not receives

        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Create new location request
        // The permission should be granted previously
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            onlocationupdate(1,1);//permission not grndeted
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        trackLocation(mLastLocation);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG,"Connection suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG,"Connection failed");
    }


    private void onlocationupdate(double lat,double lng)
    {
        Map<String,String> m=new HashMap<>();
        m.put("mobile",new SaveImpPrefrences().reterivePrefrence(context,"Nu_mobile")+"");
        m.put("lat",lat+"");
        m.put("lng",lng+"");

        System.out.println("before----"+m);
//        Toast.makeText(context,"lt lng=="+lat+"=="+lng,Toast.LENGTH_LONG).show();
        new ServerHandler().sendToServer(context, "location_update", m, 1, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons)
            {
             System.out.println("response====>"+dta);
            }
        });

    }
    static MediaPlayer mPlayer2;

    private void playSond(Context ct,String type)
    {
        System.out.println("inside playsound===="+type+"==="+new SaveImpPrefrences().reterivePrefrence(ct,"isQuarantine"));
        if(new SaveImpPrefrences().reterivePrefrence(ct,"isQuarantine").toString().equalsIgnoreCase("1")) {
            if (mPlayer2 != null) {
                mPlayer2.stop();
            }
            if (type.equalsIgnoreCase("gps"))
            {
                mPlayer2 = MediaPlayer.create(ct, R.raw.gpssoundmale);
            } else {
                mPlayer2 = MediaPlayer.create(ct, R.raw.internetsoundmale);
            }
            mPlayer2.start();

            Vibrator v = (Vibrator) ct.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(1500);
            }
        }
    }


    public boolean checkInternetState(Context ct)
    {
        try {
            ConnectivityManager cm = (ConnectivityManager) ct. getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null)
            {
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
