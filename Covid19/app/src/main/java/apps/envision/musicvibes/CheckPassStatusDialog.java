package apps.envision.musicvibes;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;


public class CheckPassStatusDialog
{


    private Dialog appUpdatedDialog;

        public CheckPassStatusDialog(final MapsActivity appCompatActivity) {
            if(appUpdatedDialog != null && appUpdatedDialog.isShowing()) {
                appUpdatedDialog.dismiss();
            }
            appUpdatedDialog = new Dialog(appCompatActivity);
            appUpdatedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            appUpdatedDialog.setContentView(R.layout.activity_check_pass_status);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = appUpdatedDialog.getWindow();
            lp.copyFrom(window.getAttributes());
            appUpdatedDialog.setCancelable(false);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            appUpdatedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            appUpdatedDialog.show();


            final LinearLayout ll_innercheckpassstatus =appUpdatedDialog.findViewById(R.id.ll_innercheckpassstatus);

            Animation animation = new TranslateAnimation(500, 00,-1000, 00);
            animation.setDuration(500);
            animation.setFillAfter(true);
            ll_innercheckpassstatus.startAnimation(animation);

            appUpdatedDialog.findViewById(R.id.ll_activecasesouter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation animation = new TranslateAnimation(00, 1000,000, -1000);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    ll_innercheckpassstatus.startAnimation(animation);

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

            appUpdatedDialog.findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Animation animation = new TranslateAnimation(00, 1000,000, -1000);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    ll_innercheckpassstatus.startAnimation(animation);

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




//            Map<String, String> m = new HashMap<>();
//            new ServerHandler().sendToServer(appCompatActivity, "stats", m, 0, new CallBack() {
//                @Override
//                public void getRespone(String dta, ArrayList<Object> respons) {
//
//                    try {
//                        JSONObject data = new JSONObject(dta);
//                        System.out.println("data=-=" + data);
//                        if (data.getString("status").equalsIgnoreCase("true"))
//                        {
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
    }

