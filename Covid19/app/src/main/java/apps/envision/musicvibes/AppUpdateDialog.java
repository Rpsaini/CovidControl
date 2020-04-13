package apps.envision.musicvibes;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import apps.envision.musicvibes.MapsActivity;
import apps.envision.musicvibes.R;


public class AppUpdateDialog {

    private Dialog appUpdatedDialog;

    public void appupdatedDialog(final MapsActivity act, final String url, String appversion)
    {
        try {
            int serverversioncode = (int) Float.parseFloat(appversion);
            PackageInfo pInfo = act.getPackageManager().getPackageInfo(act.getPackageName(), 0);
            int versioncode = pInfo.versionCode;
            if (serverversioncode != versioncode) {
                if (appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
                    appUpdatedDialog.dismiss();
                }
                appUpdatedDialog = new Dialog(act);
                appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                appUpdatedDialog.setContentView(R.layout.update);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = appUpdatedDialog.getWindow();
                lp.copyFrom(window.getAttributes());
                appUpdatedDialog.setCancelable(false);
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                appUpdatedDialog.show();

                TextView newUpdtes = appUpdatedDialog.findViewById(R.id.newUpdateText);
                newUpdtes.setText("Covid Control new version is available,Update now.");


                appUpdatedDialog.findViewById(R.id.updatenow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        act.startActivity(i);
                        appUpdatedDialog.dismiss();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}