package apps.envision.musicvibes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;
import fcm.BaseActivity;
import fcm.FusedLocationNew;
import fcm.GeofenceBroadcastReceiver;
import fcm.MyJobService;
import fragments.CheckUpFragment;
import fragments.DashBoardFragment;
import fragments.ProtectionFragment;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivityNew extends BaseActivity {


    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList=new ArrayList<>();
    public static boolean isSelfieUploaded=false;
    private static final long GEOFENCE_EXPIRATION_TIME =1800000;//mill
    private Map<String,String> blueToothMap=new HashMap<>();
    private String isQuarantine,Nu_mobile;
    private DashBoardFragment dashBoardFragment;
    private TextView upload_selfie;


    boolean isDownloading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        getSupportActionBar().hide();
        dashBoardFragment=DashBoardFragment.newInstance();
        setFragment(dashBoardFragment);
        init();


        geofencingClient = LocationServices.getGeofencingClient(this);
        checkAndRequestPermissions();
        isDownloading=isDownloading(this);



        if(!isDownloading)
        {
            //now apk is not downloading
        }

        SaveImpPrefrences imp=new SaveImpPrefrences();
        isQuarantine= imp.reterivePrefrence(MainActivityNew.this,"isQuarantine").toString();// 1 mean yes 2 mean no
        Nu_mobile = imp.reterivePrefrence(MainActivityNew.this,"Nu_mobile").toString();
        jobispatcher();
        upload_selfie=findViewById(R.id.upload_selfie);

        if(isQuarantine.equalsIgnoreCase("1"))
        {
            upload_selfie.setVisibility(View.VISIBLE);
            //findViewById(R.id.showoption).setVisibility(View.GONE);

            upload_selfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(MainActivityNew.this, UloadSelfieActivity.class);
                    i.putExtra("mobile",Nu_mobile);
                    startActivity(i);
                }
            });
        }
        else
        {
            upload_selfie.setVisibility(View.GONE);
        }
        findViewById(R.id.showoption).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ShowOptionDialog(MainActivityNew.this,Nu_mobile,isQuarantine);
            }
        });

    }

    private void init()
    {
       final View view_dashboard=findViewById(R.id.view_dashboard);
       final View view_checkup=findViewById(R.id.view_checkup);
       final View view_protection=findViewById(R.id.view_protection);

        findViewById(R.id.ll_dashboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view_dashboard.setVisibility(View.VISIBLE);
                view_checkup.setVisibility(View.INVISIBLE);
                view_protection.setVisibility(View.INVISIBLE);
                dashBoardFragment=DashBoardFragment.newInstance();
                setFragment(dashBoardFragment);


            }
        });

        findViewById(R.id.ll_checkup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_dashboard.setVisibility(View.INVISIBLE);
                view_protection.setVisibility(View.INVISIBLE);
                view_checkup.setVisibility(View.VISIBLE);
                setFragment(CheckUpFragment.newInstance());

            }
        });


        findViewById(R.id.ll_protection).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                view_dashboard.setVisibility(View.INVISIBLE);
                view_checkup.setVisibility(View.INVISIBLE);
                view_protection.setVisibility(View.VISIBLE);
                setFragment(ProtectionFragment.newInstance());

            }
        });

        ;


    }

    protected void setFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }





    private void jobispatcher()
    {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

        Job job = dispatcher.newJobBuilder()
                .setTag("locationjob")
                .setService(MyJobService.class)
                .setReplaceCurrent(true)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(0,1))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .build();




        dispatcher.mustSchedule(job);
    }




    @NonNull
    private void getGeofence(double lat,double lng,String geofenceId,double geofencearea)
    {



        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(geofenceId)
                .setCircularRegion(
                        lat,
                        lng,
                        (int)geofencearea


                )
                .setExpirationDuration(GEOFENCE_EXPIRATION_TIME)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        System.out.println("expiration time===="+GEOFENCE_EXPIRATION_TIME+"====geosendSize=="+geofenceList.size()+"=="+geofenceId+"===");

    }



    private void drawCircle(double lat,double lng)
    {
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat,lng))
                .strokeColor(Color.argb(40, 255,0,0))
                .fillColor( Color.argb(100, 255,0,0) )
                .radius(100);
        dashBoardFragment.mMap.addCircle(circleOptions);
    }






    public void getMapData()
    {
        final Map<String,String> m=new HashMap<>();
        if(FusedLocationNew.mCurrentLocation!=null)
        {
            m.put("lat",FusedLocationNew.mCurrentLocation.getLatitude()+"");
            m.put("lng",FusedLocationNew.mCurrentLocation.getLongitude()+"");
            m.put("mobile",Nu_mobile);




        }
        else
        {
            FusedLocationNew fusedLocationNew = new FusedLocationNew(MainActivityNew.this);
            fusedLocationNew.startLocationUpdates();


        }

        if(FusedLocationNew.mCurrentLocation!=null) {
            new ServerHandler().sendToServer(MainActivityNew.this, "map_data", m, 0, new CallBack() {
                @Override
                public void getRespone(String dta, ArrayList<Object> respons) {
                    try {
                        dashBoardFragment. mMap.clear();
                        final JSONObject obj = new JSONObject(dta);
                        if (obj.getString("status").equalsIgnoreCase("true")) {
                            final JSONArray reuestIdarray = new JSONArray();
                            final JSONArray data = obj.getJSONArray("data");
                            System.out.println("data length==" + data.length());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < data.length(); i++) {
                                            JSONObject jData = data.getJSONObject(i);

                                            User offsetItem = new User(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")));
                                            //Todo temp mClusterManager.addItem(offsetItem);

                                            String geosenseId = jData.getString("mobile");




                                            drawCircle(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")));
                                            MarkerOptions usermarker = new MarkerOptions().position(offsetItem.getPosition()).
                                                    title(jData.getString("distance") + "Km");
                                            usermarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.cronaimg));
                                            dashBoardFragment.mMap.addMarker(usermarker);


                                            if(isQuarantine.equalsIgnoreCase("1")) {
                                                if (Nu_mobile.equalsIgnoreCase(geosenseId)) {
                                                    JSONObject reuestDataObj = new JSONObject();
                                                    reuestDataObj.put("mobile", jData.getString("mobile"));
                                                    reuestDataObj.put("request_id", geosenseId);
                                                    reuestIdarray.put(reuestDataObj);

                                                    getGeofence(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")), geosenseId, Double.parseDouble(jData.getString("geofense_area")));
                                                }
                                            }
                                            else
                                            {
                                                JSONObject reuestDataObj = new JSONObject();
                                                reuestDataObj.put("mobile", jData.getString("mobile"));
                                                reuestDataObj.put("request_id", geosenseId);
                                                reuestIdarray.put(reuestDataObj);

                                                getGeofence(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")), geosenseId, Double.parseDouble(jData.getString("geofense_area")));

                                            }

                                        }
                                        if (obj.has("appversion"))
                                        {
                                            if(!isDownloading)
                                            {

                                                if(getVersionCode(MainActivityNew.this) < Integer.parseInt(obj.getString("appversion")))
                                                {
                                                    alertDialogForUpdate(obj.getString("url"));
                                                }
                                            }

                                        }

                                        getGeofencingRequest();
                                        registerGeoFence(reuestIdarray);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(MainActivityNew.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(changeMarkerImageHandler!=null){
            changeMarkerImageHandler.removeCallbacks(markerChangeRunnable);
        }
    }

    private Marker removeMarker;
    Runnable markerChangeRunnable;
    Handler changeMarkerImageHandler;
    private int counter = 0,count=0;
    private void getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);

        geofencingClient.addGeofences(builder.build(),getGeofencePendingIntent());
        geofencingClient.addGeofences(builder.build(), getGeofencePendingIntent())
                .addOnSuccessListener(this, new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid) {

//                      drawCircle(FusedLocationNew.mCurrentLocation.);
                        // Toast.makeText(MapsActivity.this,"Geofenceing added",Toast.LENGTH_LONG).show();


                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //  Toast.makeText(MapsActivity.this,"Goe fence not added",Toast.LENGTH_LONG).show();
                    }
                });

    }


    private PendingIntent getGeofencePendingIntent() {


        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);

    }


    @Override
    protected void onResume()
      {
        super.onResume();
        if(isSelfieUploaded)
        {
            isSelfieUploaded=false;
            getMapData();
        }
      }



    String mobile="";
    private void registerGeoFence(JSONArray array)
    {
        final Map<String,String> m=new HashMap<>();
        m.put("request_array",array+"");

        System.out.println("before===="+m);

        new ServerHandler().sendToServer(MainActivityNew.this, "register_geofense", m, 1, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {
                try {
                    // JSONObject obj=new JSONObject(dta);

                    System.out.println("geofence registered===="+dta);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }





            }
        });
    }

    private int checkAndRequestPermissions() {

        int ACCESS_BACKGROUND_LOCATION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ACCESS_BACKGROUND_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return 1;
        }

        return 0;
    }




    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


    }
