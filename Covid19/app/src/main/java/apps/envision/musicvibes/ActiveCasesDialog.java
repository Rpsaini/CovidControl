package apps.envision.musicvibes;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import communication.CallBack;
import communication.ServerHandler;
import apps.envision.musicvibes.R;


public class ActiveCasesDialog {
    private Dialog appUpdatedDialog;
    public ActiveCasesDialog(final MapsActivity appCompatActivity) {

        if (appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
            appUpdatedDialog.dismiss();
        }
        appUpdatedDialog = new Dialog(appCompatActivity);
        appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appUpdatedDialog.setContentView(R.layout.active_cases_layout);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = appUpdatedDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        appUpdatedDialog.setCancelable(false);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        appUpdatedDialog.show();


        final TextView crona_confirmed = appUpdatedDialog.findViewById(R.id.crona_confirmed);
        final TextView crona_death = appUpdatedDialog.findViewById(R.id.crona_death);
        final TextView crona_homequarantine = appUpdatedDialog.findViewById(R.id.crona_homequarantine);
        final TextView crona_hospitalized = appUpdatedDialog.findViewById(R.id.crona_hospitalized);
        final TextView crona_recovered = appUpdatedDialog.findViewById(R.id.crona_recovered);
        final TextView crona_resultawait = appUpdatedDialog.findViewById(R.id.crona_resultawait);
        final TextView crona_sasnagar = appUpdatedDialog.findViewById(R.id.crona_sasnagar);
        final TextView crona_resultnegative = appUpdatedDialog.findViewById(R.id.crona_resultnegative);
        final TextView crona_tested = appUpdatedDialog.findViewById(R.id.crona_tested);


       final LinearLayout ll_innertransalation =appUpdatedDialog.findViewById(R.id.ll_innertransalation);

        Animation animation = new TranslateAnimation(500, 00,-1000, 00);
        animation.setDuration(500);
        animation.setFillAfter(true);
        ll_innertransalation.startAnimation(animation);


        appUpdatedDialog.findViewById(R.id.ll_activecasesouter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = new TranslateAnimation(00, 1000,000, -1000);
                animation.setDuration(500);
                animation.setFillAfter(true);
                ll_innertransalation.startAnimation(animation);

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        appUpdatedDialog.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }
        });


        Map<String, String> m = new HashMap<>();
        new ServerHandler().sendToServer(appCompatActivity, "stats", m, 0, new CallBack() {
            @Override
            public void getRespone(String dta, ArrayList<Object> respons) {

                try {
                    JSONObject data = new JSONObject(dta);
                    System.out.println("data=-=" + data);
                    if (data.getString("status").equalsIgnoreCase("true"))
                    {

                        crona_confirmed.setText(data.getString("confirmed"));
                        crona_death.setText(data.getString("deaths"));
                        crona_homequarantine.setText(data.getString("home_quarantine"));
                        crona_hospitalized.setText(data.getString("hospital_quarantine"));
                        crona_recovered.setText(data.getString("recovered"));
                        crona_resultawait.setText(data.getString("result_awaited"));
                        crona_sasnagar.setText(data.getString("sas_nagar"));
                        crona_resultnegative.setText(data.getString("result_negetive"));
                        crona_tested.setText(data.getString("tested"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}