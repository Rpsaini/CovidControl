package apps.envision.musicvibes;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.appcompat.app.AppCompatActivity;


public class CheckPassStatusDialog
{


    private Dialog appUpdatedDialog;

        public CheckPassStatusDialog(final AppCompatActivity appCompatActivity,String mobile,String id,String passStatus) {
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

            if(passStatus.equalsIgnoreCase("Approved"))
            {
                generateQrCode(mobile,id,(ImageView) appUpdatedDialog.findViewById(R.id.qrcode));
            }
            else
            {
                appUpdatedDialog.findViewById(R.id.ll_isShowQr).setVisibility(View.GONE);
            }


            final LinearLayout ll_innercheckpassstatus =appUpdatedDialog.findViewById(R.id.ll_innercheckpassstatus);

            Animation animation = new TranslateAnimation(500, 00,-1000, 00);
            animation.setDuration(500);
            animation.setFillAfter(true);
            ll_innercheckpassstatus.startAnimation(animation);

            TextView passStatusedt =appUpdatedDialog.findViewById(R.id.checkpassstatus);

            passStatusedt.setText(passStatus);
//            appUpdatedDialog.findViewById(R.id.ll_activecasesouter).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Animation animation = new TranslateAnimation(00, 1000,000, -1000);
//                    animation.setDuration(500);
//                    animation.setFillAfter(true);
//                    ll_innercheckpassstatus.startAnimation(animation);
//
//                    animation.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            appUpdatedDialog.dismiss();
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//
//
//                }
//            });

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
        }

        private void generateQrCode(String mobile, String passid, ImageView imageView)
        {
            String text=mobile+"-"+passid;
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,250,250);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

