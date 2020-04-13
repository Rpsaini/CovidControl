package apps.envision.musicvibes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import communication.SaveImpPrefrences;
import apps.envision.musicvibes.R;


//public class ShowWarningDialog {
//}
public class ShowWarningDialog {
    private Dialog appUpdatedDialog;
    public ShowWarningDialog(final Context appCompatActivity,final MediaPlayer mediaPlayer) {

        if (appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
            appUpdatedDialog.dismiss();
        }
        appUpdatedDialog = new Dialog(appCompatActivity);
        appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appUpdatedDialog.setContentView(R.layout.show_warningdialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = appUpdatedDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        appUpdatedDialog.setCancelable(false);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        appUpdatedDialog.show();

        TextView quarantine_message=appUpdatedDialog.findViewById(R.id.quarantine_message);

        String isQuarantine=new SaveImpPrefrences().reterivePrefrence(appCompatActivity,"isQuarantine").toString();
        if(isQuarantine.equalsIgnoreCase("yes"))
        {
            quarantine_message.setText(R.string.quarantin_exit);
        }
        else
        {
            quarantine_message.setText(R.string.normalenter);
        }

        appUpdatedDialog.findViewById(R.id.quarantine_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                }
                appUpdatedDialog.dismiss();
            }
        });




    }
    }
