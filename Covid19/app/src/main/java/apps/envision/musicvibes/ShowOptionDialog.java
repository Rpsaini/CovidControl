package apps.envision.musicvibes;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import communication.SaveImpPrefrences;



public class ShowOptionDialog
{

    private Dialog appUpdatedDialog;
    public ShowOptionDialog(final MapsActivity appCompatActivity)
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

        appUpdatedDialog.findViewById(R.id.tv_activecases).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdatedDialog.dismiss();
                new ActiveCasesDialog(appCompatActivity);
            }
        });

        appUpdatedDialog.findViewById(R.id.rr_outer_activepopup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdatedDialog.dismiss();
            }
        });

        appUpdatedDialog.findViewById(R.id.tv_reportQuarantine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdatedDialog.dismiss();
                Intent i=new Intent(appCompatActivity, ReortaCaseActivity.class);
                appCompatActivity.startActivity(i);
            }
        });


        TextView tv_applyforpass =appUpdatedDialog.findViewById(R.id.tv_applyforpass);

        if(imp.reterivePrefrence(appCompatActivity,CConstant.key_PassStatus).equals("0"))
        {
            tv_applyforpass.setText("Apply for e-Pass");
        }
        else
        {
            tv_applyforpass.setText("Check e-Pass status");
        }
        appUpdatedDialog.findViewById(R.id.tv_applyforpass).setOnClickListener(
                new View.OnClickListener()
             {
            @Override
            public void onClick(View v)
              {
                appUpdatedDialog.dismiss();

                String isNormalUser=imp.reterivePrefrence(appCompatActivity,"isNormalUser").toString();
                String passStatus=imp.reterivePrefrence(appCompatActivity,CConstant.key_PassStatus).toString();
                if(passStatus.equalsIgnoreCase("0")) {
                     if (isNormalUser.equalsIgnoreCase("0")) {
                         Intent i = new Intent(appCompatActivity, MobileNumberActivity.class);
                         i.putExtra("epass", "1");
                         appCompatActivity.startActivity(i);

                     } else {
                         Intent i = new Intent(appCompatActivity, ApplyForPass.class);
                         i.putExtra("mobile", imp.reterivePrefrence(appCompatActivity, "Nu_mobile") + "");
                         appCompatActivity.startActivity(i);
                     }
                 }
                 else
                 {
                     new CheckPassStatusDialog(appCompatActivity);
                 }
              }
              });






}
}
