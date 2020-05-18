package fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apps.envision.musicvibes.MainActivityNew;
import apps.envision.musicvibes.R;
import communication.CallBack;
import communication.ServerHandler;
import fcm.FusedLocationNew;

public class DashBoardFragment extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private View view;


    private GeofencingClient geofencingClient;
    List<Geofence> geofenceList = new ArrayList<>();
    public static boolean isSelfieUploaded = false;
    private TextView upload_register;
    private static final long GEOFENCE_EXPIRATION_TIME = 1800000;//mill
    private Map<String, String> blueToothMap = new HashMap<>();
    private String isQuarantine, Nu_mobile;
    public GoogleMap mMap;
    boolean isDownloading;

    public DashBoardFragment() {
        // Required empty public constructor
    }


    public static DashBoardFragment newInstance() {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        getStats();


    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {

        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (FusedLocationNew.mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(FusedLocationNew.mCurrentLocation.getLatitude(), FusedLocationNew.mCurrentLocation.getLongitude())));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(FusedLocationNew.mCurrentLocation.getLatitude(), FusedLocationNew.mCurrentLocation.getLongitude()), 12.0f));
        }
        ((MainActivityNew) getActivity()).getMapData();

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    private void getStats() {

        final TextView stateTextView = view.findViewById(R.id.stateTextView);
        final TextView crona_state_tested = view.findViewById(R.id.crona_state_tested);
        final TextView crona_state_negative = view.findViewById(R.id.crona_state_negative);
        final TextView crona_confirmed = view.findViewById(R.id.crona_confirmed);
        final TextView crona_recovered = view.findViewById(R.id.crona_recovered);
        final TextView crona_active = view.findViewById(R.id.crona_active);
        final TextView crona_death = view.findViewById(R.id.crona_death);
        final TextView crona_homequarantine = view.findViewById(R.id.crona_homequarantine);
        final TextView crona_hospitalquarantine = view.findViewById(R.id.crona_hospitalquarantine);
        final TextView crona_confiremd_india = view.findViewById(R.id.crona_confiremd_india);
        final TextView crona_total_death_india = view.findViewById(R.id.crona_total_death_india);
        final TextView crona_confiremd_world = view.findViewById(R.id.crona_confiremd_world);
        final TextView crona_total_death_world = view.findViewById(R.id.crona_total_death_world);


        Map<String, String> m = new HashMap<>();
        m.put("lat",FusedLocationNew.mCurrentLocation.getLatitude()+"");
        m.put("lng",FusedLocationNew.mCurrentLocation.getLongitude()+"");
        new ServerHandler().sendToServer(getActivity(), "stats", m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {

                try {
                    JSONObject data = new JSONObject(dta);
                    System.out.println("getStats data=-=" + data);
                    if (data.getString("status").equalsIgnoreCase("true")) {


                        stateTextView.setText(data.getString("state"));
                         crona_state_tested.setText(data.getString("tested"));
                        crona_state_negative.setText(data.getString("result_negetive"));
                        crona_confirmed.setText(data.getString("confirmed"));
                        crona_recovered.setText(data.getString("recovered"));


                         crona_death.setText(data.getString("deaths"));
                         crona_homequarantine.setText(data.getString("home_quarantine"));
                        crona_hospitalquarantine.setText(data.getString("hospital_quarantine"));
                         crona_confiremd_india.setText(data.getString("india_total_case"));
                         crona_total_death_india.setText(data.getString("india_total_death"));
                         crona_confiremd_world.setText(data.getString("world_total_case"));
                         crona_total_death_world.setText(data.getString("world_total_death"));


                        int active=Integer.parseInt(data.getString("confirmed"))-Integer.parseInt(data.getString("recovered"));
                        crona_active.setText(active+"");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
