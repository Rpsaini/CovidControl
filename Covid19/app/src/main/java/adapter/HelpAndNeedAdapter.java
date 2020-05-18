package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import apps.envision.musicvibes.CConstant;
import apps.envision.musicvibes.CheckorderStatusActivity;
import apps.envision.musicvibes.R;


public class HelpAndNeedAdapter extends RecyclerView.Adapter<HelpAndNeedAdapter.MyViewHolder> {

    private ArrayList<JSONObject> datAr;
    private CheckorderStatusActivity pActivity;
    public HelpAndNeedAdapter(ArrayList<JSONObject> ar, CheckorderStatusActivity paActiviity)
    {
        datAr=ar;
        pActivity=paActiviity;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView servicename, status, remarks,cancel,approve;
        public ImageView imageView;
        LinearLayout ll_receivedlinear;


        public MyViewHolder(View view) {
            super(view);

            servicename=view.findViewById(R.id.servicename);
            status=view.findViewById(R.id.status);
            remarks=view.findViewById(R.id.remarks);
            cancel=view.findViewById(R.id.cancel);
            approve=view.findViewById(R.id.approve);
            imageView=view.findViewById(R.id.imageView);
            ll_receivedlinear=view.findViewById(R.id.ll_receivedlinear);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.helpandneed_listrow, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        try
        {
            JSONObject dataObj=datAr.get(position);

            System.out.println("data===="+dataObj);
            holder.servicename.setText(dataObj.getString("type"));
            holder.status.setText(dataObj.getString("status"));
            holder.remarks.setText(dataObj.getString("remarks"));
            holder.cancel.setTag(position);
            holder.approve.setTag(position);
            holder.ll_receivedlinear.setVisibility(View.GONE);
            if(dataObj.getString("status").equalsIgnoreCase("0"))
            {
                holder.status.setText("Pending");
                holder.status.setTextColor(pActivity.getResources().getColor(R.color.button_blue_color));
            }
            else  if(dataObj.getString("status").equalsIgnoreCase("1"))
            {
                holder.status.setText("Delivered");
                holder.ll_receivedlinear.setVisibility(View.VISIBLE);
                holder.status.setTextColor(pActivity.getResources().getColor(R.color.green_color));
            }
            else  if(dataObj.getString("status").equalsIgnoreCase("2"))
            {
                holder.status.setText("Received");
                holder.status.setTextColor(pActivity.getResources().getColor(R.color.green_color));
            }

            else  if(dataObj.getString("status").equalsIgnoreCase("3"))
            {
                holder.status.setText("Not Delivered");
                holder.status.setTextColor(pActivity.getResources().getColor(R.color.red_color));
            }
            else if(dataObj.getString("status").equalsIgnoreCase("4"))
            {
                holder.status.setText("Declined by admin");
                holder.status.setTextColor(pActivity.getResources().getColor(R.color.red_color));
            }




//            0 = Pending, 1 = delivered admin, 2 = received by user, 3 = not delivered , 4 declined by admin

            showImage(dataObj.getString("image"),holder.imageView);



            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        int pos = Integer.parseInt(v.getTag() + "");
                        JSONObject dataObj = datAr.get(pos);

                        pActivity.checancelRequest(dataObj.getString("id"),"3",pos);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });

            holder.approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        int pos = Integer.parseInt(v.getTag() + "");
                        JSONObject dataObj = datAr.get(pos);

                        pActivity.checancelRequest(dataObj.getString("id"),"2",pos);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });



//            "status":true,"requests":[{"id":"1","type":"ESSENTIAL","image":"https:\/\/webcomclients.in\/covid\/uploads\/requets\/62b3c0fea53a042ebae9f1dc98e82332.jpg","remarks":"need money","admin_remarks":"","status":"Reject By User"},{"id":"2","type":"MEDICINE","image":"https:\/\/webcomclients.in\/covid\/uploads\/requets\/a0bebb7cd1d8b72c24ba855213e7b7a7.jpg","remarks":"need combiflam medicine","admin_remarks":"","status":"Pending"},{"id":"3","type":"OTHER","image":"https:\/\/webcomclients.in\/covid\/uploads\/requets\/bd3a76411c6a2def4ed55476a4f19df9.jpg","remarks":"teting done","admin_remarks":"","status":"Pending"}]}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return datAr.size();
    }


    private void showImage(String url,final ImageView userimage) {

        try {

            System.out.println("url===="+CConstant.imgbaseUrl +url);
            Picasso.with(pActivity)
                    .load(CConstant.imgbaseUrl +url)
                    .into(userimage, new Callback()
                    {

                        @Override
                        public void onSuccess() {
                            userimage.setTag("1");
                        }

                        @Override
                        public void onError() {
                            Picasso.with(pActivity)
                                    .load(R.mipmap.ic_launcher)
                                    .into(userimage);
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}