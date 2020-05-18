package apps.envision.musicvibes;

import adapter.HelpAndNeedAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;
import fcm.FusedLocationNew;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckorderStatusActivity extends AppCompatActivity {

    int page=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkorder_status);
        getSupportActionBar().hide();
        getHelpAndNeeddata();

        findViewById(R.id.loadmore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page=page+1;
                getHelpAndNeeddata();
            }
        });
    }

    HelpAndNeedAdapter mAdapter;
    private void initRecycler(ArrayList<JSONObject> ar)
    {
        RecyclerView recyclerBookingDetails = findViewById(R.id.helpandneed_recycler);
        try {
             mAdapter = new HelpAndNeedAdapter(ar, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerBookingDetails.setLayoutManager(mLayoutManager);
            recyclerBookingDetails.setItemAnimator(new DefaultItemAnimator());
            recyclerBookingDetails.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<JSONObject> datAr=new ArrayList<>();
    private void getHelpAndNeeddata()
    {
        Map<String,String> object=new HashMap<>();
        object.put("mobile",new SaveImpPrefrences().reterivePrefrence(this,"Nu_mobile")+"");
        object.put("page",page+"");
        object.put("lat", FusedLocationNew.mCurrentLocation.getLatitude()+"");
        object.put("lng",FusedLocationNew.mCurrentLocation.getLongitude()+"");
        new ServerHandler().sendToServer(CheckorderStatusActivity.this, "get_service_requests", object, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons)
            {
                try {
                    JSONObject obj = new JSONObject(dta);
                    String status = obj.getString("status");
                    if (status.equalsIgnoreCase("true"))
                    {
                        JSONArray requests = obj.getJSONArray("requests");
                        for(int x = 0; x < requests.length(); x++)
                        {
                            datAr.add(requests.getJSONObject(x));
                        }
                        System.out.println("data ar size==="+datAr.size());
                        if (datAr.size() <= 10) {
                            initRecycler(datAr);
                        }
                        else {
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Toast.makeText(CheckorderStatusActivity.this,"No data found !",Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();


                }
            }
        });
    }

    public void checancelRequest(String id, final String status, final int pos)
    {

        Map<String,String> m=new HashMap<>();
        m.put("id",id);
        m.put("mobile",new SaveImpPrefrences().reterivePrefrence(this,"Nu_mobile")+"");
        m.put("status",status);

        new ServerHandler().sendToServer(CheckorderStatusActivity.this, "update_service_request",m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {

                try {

                    JSONObject obj=new JSONObject(dta);
                    if(obj.getString("status").equalsIgnoreCase("true"))
                    {

                        JSONObject dataObj = datAr.get(pos);
                        dataObj.remove("status");
                        dataObj.put("status",status);

                        datAr.set(pos, dataObj);
                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(CheckorderStatusActivity.this,obj.getString("message"),Toast.LENGTH_LONG).show();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

    }
}
