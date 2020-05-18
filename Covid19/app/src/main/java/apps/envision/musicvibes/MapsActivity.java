package apps.envision.musicvibes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;
import fcm.BaseActivity;
import fcm.FusedLocationNew;
import fcm.GeofenceBroadcastReceiver;

import apps.envision.musicvibes.R;
import fcm.MyJobService;
import fcm.SimpleJobIntentService;


import android.app.AlertDialog;
import android.app.DownloadManager;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapsActivity extends BaseActivity implements OnMapReadyCallback {
    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList=new ArrayList<>();
//    public static boolean isSelfieUploaded=false;
    private  TextView upload_register;
    private static final long GEOFENCE_EXPIRATION_TIME =1800000;//mill
    private Map<String,String> blueToothMap=new HashMap<>();

    private String isQuarantine,Nu_mobile;

    private GoogleMap mMap;
    boolean isDownloading;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_maps);

         upload_register =findViewById(R.id.upload_register);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        checkAndRequestPermissions();
        isDownloading=isDownloading(MapsActivity.this);

        init();
        if(!isDownloading)
        {
            new ActiveCasesDialog(this);
            }
        SaveImpPrefrences imp=new SaveImpPrefrences();
        isQuarantine= imp.reterivePrefrence(MapsActivity.this,"isQuarantine").toString();// 1 mean yes 2 mean no
        Nu_mobile = imp.reterivePrefrence(MapsActivity.this,"Nu_mobile").toString();


        jobispatcher();
//        Intent mIntent = new Intent(this, SimpleJobIntentService.class);
//        SimpleJobIntentService.enqueueWork(this, mIntent);
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


       public  void init()
    {
       // getListOfBluetooth();
        final ImageView uparrow =findViewById(R.id.uparrow);
        uparrow.setTag("1");
        final LinearLayout ll_uploadselfiewview =findViewById(R.id.ll_uploadselfiewview);
        uparrow.setRotation(-180);
        uparrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(uparrow.getTag().toString().equalsIgnoreCase("0"))
                {


                    uparrow.setRotation(-180);
                    uparrow.setTag("1");
                    showOptions(1,ll_uploadselfiewview);
                }
                else
                {


                    uparrow.setRotation(0);
                    uparrow.setTag("0");
                    showOptions(0,ll_uploadselfiewview);
                }

            }
        });




//        findViewById(R.id.uploadyorselfie).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String isQuarantine=new SaveImpPrefrences().reterivePrefrence(MapsActivity.this,"isQuarantine").toString();
//                if(isQuarantine.equalsIgnoreCase("yes"))
//                {
//                    Intent i=new Intent(MapsActivity.this, UloadSelfieActivity.class);
//                    startActivity(i);
//                }
//                else
//                {
////                    Intent i=new Intent(MapsActivity.this,TakeMobileScreen.class);
//                    Intent i=new Intent(MapsActivity.this, MobileNumberActivity.class);
//                    startActivity(i);
//                }
//
//
//            }
//        });


        findViewById(R.id.showoption).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               // new ShowOptionDialog(MapsActivity.this,Nu_mobile,isQuarantine);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;








//        MarkerOptions usermarker = new MarkerOptions().position(new LatLng(FusedLocationNew.mCurrentLocation.getLatitude(), FusedLocationNew.mCurrentLocation.getLongitude())).
//                title("");
//        usermarker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pinblue));
      //  Marker marker = googleMap.addMarker(usermarker);
     //   changeMarkerImageAnimation(usermarker, marker);



  if(FusedLocationNew.mCurrentLocation!=null) {
      mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(FusedLocationNew.mCurrentLocation.getLatitude(), FusedLocationNew.mCurrentLocation.getLongitude())));
      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(FusedLocationNew.mCurrentLocation.getLatitude(), FusedLocationNew.mCurrentLocation.getLongitude()), 12.0f));


      // changeMarkerImageAnimation(usermarker,marker);
      setUpClusterer();
      getMapData();
  }

    }





   // private ClusterManager<User> mClusterManager;

    private void setUpClusterer() {
        // Position the map.
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 10));

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)

        /*
        mClusterManager = new ClusterManager<User>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
*/
        // Add cluster items (markers) to the cluster manager.
        // addItems();
    }




    public void getMapData()
    {
        final Map<String,String> m=new HashMap<>();
        if(FusedLocationNew.mCurrentLocation!=null)
        {
            m.put("lat",FusedLocationNew.mCurrentLocation.getLatitude()+"");
            m.put("lng",FusedLocationNew.mCurrentLocation.getLongitude()+"");


        }
        else
        {
            FusedLocationNew fusedLocationNew = new FusedLocationNew(MapsActivity.this);
            fusedLocationNew.startLocationUpdates();


        }

        if(FusedLocationNew.mCurrentLocation!=null) {
            new ServerHandler().sendToServer(MapsActivity.this, "map_data", m, 0, new CallBack() {
                @Override
                public void getRespone(String dta, ArrayList<Object> respons) {
                    try {
                        mMap.clear();
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


                                            JSONObject reuestDataObj = new JSONObject();
                                            reuestDataObj.put("mobile", jData.getString("mobile"));
                                            reuestDataObj.put("request_id", geosenseId);
                                            reuestIdarray.put(reuestDataObj);

                                            drawCircle(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")));
                                            MarkerOptions usermarker = new MarkerOptions().position(offsetItem.getPosition()).
                                                    title(jData.getString("distance") + "Km");
                                            usermarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.cronaimg));
                                            mMap.addMarker(usermarker);


                                            getGeofence(Double.parseDouble(jData.getString("lat")), Double.parseDouble(jData.getString("lng")), geosenseId, Double.parseDouble(jData.getString("geofense_area")));


                                        }
                                        if (obj.has("appversion"))
                                        {
                                            if(!isDownloading)
                                            {

                                                if(getVersionCode(MapsActivity.this) < Integer.parseInt(obj.getString("appversion")))
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
                            Toast.makeText(MapsActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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


//    private void changeMarkerImageAnimation(MarkerOptions marker, Marker removeMarker) {
//        count = 0;
//        if (changeMarkerImageHandler != null) {
//            changeMarkerImageHandler.removeCallbacks(markerChangeRunnable);
//        }
//        startAnimation(marker, removeMarker);
//        changeMarkerImageHandler = new Handler();
//        changeMarkerImageHandler.postDelayed(markerChangeRunnable, 200);
//    }
    private void startAnimation(final MarkerOptions markerOption, final Marker marker) {
        counter = 0;

        removeMarker = marker;
        markerChangeRunnable = new Runnable() {

            @Override
            public void run() {

                counter++;

                if (count == 0) {
                    marker.setAlpha(.5f);
                    count=1;
                } else if (count == 1) {
                    marker.setAlpha(1f);
                    count=0;
                }


                changeMarkerImageHandler.postDelayed(this, 300);
            }
        };
    }



    private void showOptions(int option,View linearview)
    {

        final LinearLayout view = (LinearLayout)findViewById(R.id.ll_uploadselfiewview);

        WindowManager wm =
                (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int deviceWidth;

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);

        int height= view.getMeasuredHeight();
        System.out.println("height==="+view.getMeasuredHeight());


        if(option==1) {
            linearview.setTranslationY(0);
        }
        else
        {
            linearview.setTranslationY(height-70);
        }

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


        System.out.println("expiration time===="+GEOFENCE_EXPIRATION_TIME);

    }



    private void drawCircle(double lat,double lng)
    {
        CircleOptions circleOptions = new CircleOptions()
                .center( new LatLng(lat,lng))
                .strokeColor(Color.argb(40, 255,0,0))
                .fillColor( Color.argb(100, 255,0,0) )
                .radius(100);
         mMap.addCircle(circleOptions);
    }

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
    protected void onResume() {
        super.onResume();
//        if(MapsActivity.isSelfieUploaded)
//        {
//            MapsActivity.isSelfieUploaded=false;
//            getMapData();
//        }

        String isQuarantine=new SaveImpPrefrences().reterivePrefrence(MapsActivity.this,"isQuarantine").toString();
        if(isQuarantine.equalsIgnoreCase("yes"))
        {
            upload_register.setText("Upload your selfie");
        }
        else
        {
            upload_register.setText("Register");
        }
    }

    String mobile="";
    private void registerGeoFence(JSONArray array)
    {
        final Map<String,String> m=new HashMap<>();
        m.put("request_array",array+"");

        new ServerHandler().sendToServer(MapsActivity.this, "register_geofense", m, 1, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {
                try {
                   // JSONObject obj=new JSONObject(dta);
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

    private void getListOfBluetooth() {

        Handler hnd=new Handler();
        hnd.postDelayed(new Runnable() {
            @Override
            public void run() {

                getListOfBluetooth();
            }
        },30000);

        blueToothMap.clear();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

        }
        else
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, CConstant.REQUEST_ENABLE_BT);
            }
            else
            {

                System.out.println("Scan For blutooth===");
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
                if(mBluetoothAdapter.isDiscovering()) {
                    // Bluetooth is already in modo discovery mode, we cancel to restart it again
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
            }
        }
    }
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(blueToothMap.containsKey(device.getAddress()))
                {
                }
                else
                {
                    blueToothMap.put(device.getAddress(),device.getName());
                    int  rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);




                    Toast.makeText(getApplicationContext(),device.getName()+"=="+calculateDistance(rssi), Toast.LENGTH_SHORT).show();


                }

            }
        }
    };

    double calculateDistance(int rssi) {

        int txPower = -59; //hard coded power value. Usually ranges between -59 to -65

        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double distance =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return distance;
        }
    }






}
