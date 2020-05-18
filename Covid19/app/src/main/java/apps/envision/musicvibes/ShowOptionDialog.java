package apps.envision.musicvibes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import communication.CallBack;
import communication.SaveImpPrefrences;
import communication.ServerHandler;


public class ShowOptionDialog
{

    private Dialog appUpdatedDialog;

    public ShowOptionDialog(final MainActivityNew appCompatActivity, String mobile, String isQuarantine)
    {

        final SaveImpPrefrences imp=new SaveImpPrefrences();
        if(appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
            appUpdatedDialog.dismiss();
        }
        appUpdatedDialog = new Dialog(appCompatActivity);
        appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appUpdatedDialog.setContentView(R.layout.show_optiondialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = appUpdatedDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        appUpdatedDialog.setCancelable(false);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        appUpdatedDialog.show();

        appUpdatedDialog.findViewById(R.id.tv_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdatedDialog.dismiss();
                appCompatActivity.getMapData();
            }
        });

        appUpdatedDialog.findViewById(R.id.tv_activecases);



//        appUpdatedDialog.findViewById(R.id.tv_activecases).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appUpdatedDialog.dismiss();
////                new ActiveCasesDialog(appCompatActivity);
//            }
//        });

        appUpdatedDialog.findViewById(R.id.rr_outer_activepopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdatedDialog.dismiss();
            }
        });

//        appUpdatedDialog.findViewById(R.id.tv_reportQuarantine).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appUpdatedDialog.dismiss();
//                Intent i=new Intent(appCompatActivity, ReortaCaseActivity.class);
//                appCompatActivity.startActivity(i);
//            }
//        });


//        appUpdatedDialog.findViewById(R.id.tv_chatbot).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                appUpdatedDialog.dismiss();
//                Intent i=new Intent(appCompatActivity, ChatBot.class);
//                appCompatActivity.startActivity(i);
//            }
//        });

        TextView tv_applyforpass =appUpdatedDialog.findViewById(R.id.tv_applyforpass);
        TextView tv_uploadselfie =appUpdatedDialog.findViewById(R.id.tv_uploadselfie);
        TextView tv_requestaservice =appUpdatedDialog.findViewById(R.id.tv_requestaservice);


        if(isQuarantine.equalsIgnoreCase("1"))
        {
            tv_uploadselfie.setVisibility(View.GONE);
            tv_applyforpass.setVisibility(View.GONE);
            tv_requestaservice.setVisibility(View.VISIBLE);

            tv_uploadselfie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(appCompatActivity, UloadSelfieActivity.class);
                    i.putExtra("mobile", imp.reterivePrefrence(appCompatActivity, "Nu_mobile") + "");
                    appCompatActivity.startActivity(i);
                    appUpdatedDialog.dismiss();
                }
            });


            tv_requestaservice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(appCompatActivity, RequestActivity.class);
                    i.putExtra("mobile", imp.reterivePrefrence(appCompatActivity, "Nu_mobile") + "");
                    appCompatActivity.startActivity(i);
                    appUpdatedDialog.dismiss();
                }
            });


        }

        else
        {
            tv_requestaservice.setVisibility(View.GONE);
            tv_uploadselfie.setVisibility(View.GONE);
            tv_applyforpass.setVisibility(View.VISIBLE);

            appUpdatedDialog.findViewById(R.id.tv_applyforpass).setOnClickListener(
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                        // appUpdatedDialog.dismiss();


               passStatusApi(appCompatActivity,imp.reterivePrefrence(appCompatActivity, "Nu_mobile") + "");
               appUpdatedDialog.dismiss();

//                String passStatus=imp.reterivePrefrence(appCompatActivity,CConstant.key_PassStatus).toString();
//
//                if(passStatus.equalsIgnoreCase("0"))
//                {
//                 }
//                 else
//                 {
//                     new CheckPassStatusDialog(appCompatActivity);
//                 }
//
                    }
                    });


        }
//        if(imp.reterivePrefrence(appCompatActivity,CConstant.key_PassStatus).equals("0"))
//        {
//            tv_applyforpass.setText("Apply for e-Pass");
//        }
//        else
//        {
//            tv_applyforpass.setText("Check e-Pass status");
//        }

    }

    private  void passStatusApi(final AppCompatActivity appCompatActivity, final String mobile)
    {

//


        Map<String,String> m=new HashMap<>();
        m.put("mobile",mobile);
        new ServerHandler().sendToServer(appCompatActivity, "check_epass_status", m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {


                try {
                    JSONObject obj=new JSONObject(dta);
                    if(obj.getString("status").equalsIgnoreCase("true"))
                    {
                        new SaveImpPrefrences().savePrefrencesData(appCompatActivity,"applied",CConstant.key_PassStatus);
                        if(obj.getString("pass_status").equalsIgnoreCase("Rejected")||obj.getString("pass_status").equalsIgnoreCase("Expired"))
                        {

                            Intent i = new Intent(appCompatActivity, ApplyForPass.class);
                            i.putExtra("mobile", mobile);
                            appCompatActivity.startActivity(i);
                        }
                        else
                        {

                            new CheckPassStatusDialog(appCompatActivity,mobile,obj.getString("pass_id"),obj.getString("pass_status"));
                        }

                    }
                    else
                    {
                        Intent i = new Intent(appCompatActivity, ApplyForPass.class);
                        i.putExtra("mobile", mobile);
                         appCompatActivity.startActivity(i);

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


