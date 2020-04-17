package fcm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

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
import communication.CallBack;
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void execute()
    {

        startConnection();

    }

    public void startConnection(){
        mGoogleApiClient.connect();
    }


    public   void trackLocation(Location location) {
// To replace with updating database
        if (location!=null) {
            Log.i("TRACKING LAT", String.valueOf(location.getLatitude()));
            Log.i("TRACKING LON", String.valueOf(location.getLongitude()));
            onlocationupdate(location.getLatitude(),location.getLongitude());
        }
        else
        {
            Log.i("TRACKING LAT", "Null location received");

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Create new location request
        // The permission should be granted previously
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        m.put("mobile","7777777777");
        m.put("lat",lat+"");
        m.put("lng",lng+"");
        Toast.makeText(context,"lt lng=="+lat+"=="+lng,Toast.LENGTH_LONG).show();
        new ServerHandler().sendToServer(context, "location_update", m, 1, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons)
            {
             System.out.println("response====>"+dta);
            }
        });

    }
}
