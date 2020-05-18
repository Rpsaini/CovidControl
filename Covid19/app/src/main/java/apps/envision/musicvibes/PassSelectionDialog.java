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

import androidx.appcompat.app.AppCompatActivity;

public class PassSelectionDialog
{

    private Dialog appUpdatedDialog;

    public  PassSelectionDialog(final AppCompatActivity act)
    {
        try {

                if (appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
                    appUpdatedDialog.dismiss();
                }
                appUpdatedDialog = new Dialog(act);
                appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                appUpdatedDialog.setContentView(R.layout.passselectiondialog);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                Window window = appUpdatedDialog.getWindow();
                lp.copyFrom(window.getAttributes());
                appUpdatedDialog.setCancelable(true);
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(lp);
                appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                appUpdatedDialog.show();




                appUpdatedDialog.findViewById(R.id.passforpunjab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://epasscovid19.pais.net.in/"));
                        act.startActivity(i);
                        appUpdatedDialog.dismiss();

                    }
                });

            appUpdatedDialog.findViewById(R.id.paasforchandigarh).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://admser.chd.nic.in/dpc/"));
                    act.startActivity(i);
                    appUpdatedDialog.dismiss();

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

}
}
