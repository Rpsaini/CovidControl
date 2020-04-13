//package apps.envision.musicvibes;
//
//import androidx.appcompat.app.AppCompatActivity;
//import communication.CallBack;
//import communication.SaveImpPrefrences;
//import communication.ServerHandler;
//import fcm.FusedLocationNew;
//
//import apps.envision.musicvibes.R;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//public class VerifyOtpScreen extends AppCompatActivity {
//    private TextView resendotp;
//
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_verify_otp_screen);
//        resendotp = findViewById(R.id.resendotp);
//        resendotp.setVisibility(View.GONE);
//        final EditText inputcode = findViewById(R.id.inputcode);
//        final TextView done = findViewById(R.id.done);
//
//
////      Map<String, String> m = new HashMap<>();
////            m.put("user_id", getIntent().getStringExtra("user_id"));
//        //  resendOTP(m);
//        //  counter();
//
////            resendotp.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View v) {
////                    Map<String, String> m = new HashMap<>();
////                    m.put("user_id", getIntent().getStringExtra("user_id"));
//////                    resendOTP(m);
////
////                }
////            });
//
//
//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (inputcode.getText().toString().length() == 0) {
//                    Toast.makeText(VerifyOtpScreen.this, "Enter OTP", Toast.LENGTH_LONG).show();
//                    return;
//                } else if (!inputcode.getText().toString().equalsIgnoreCase(getIntent().getStringExtra("otp"))) {
//                    Toast.makeText(VerifyOtpScreen.this, "Enter valid OTP", Toast.LENGTH_LONG).show();
//                    return;
//                } else {
//                    Map<String, String> m = new HashMap<>();
//                    m.put("mobile", getIntent().getStringExtra("mobile"));
//                    m.put("otp", inputcode.getText().toString());
//                    m.put("lat", FusedLocationNew.mCurrentLocation.getLatitude() + "");
//                    m.put("lng", FusedLocationNew.mCurrentLocation.getLongitude() + "");
//                    System.out.println("before====" + m);
//                    saveUserDetails(m);
//                }
//            }
//        });
//
////===================initialize===
//
//
//    }
//
//
//    private void saveUserDetails(Map<String, String> m) {
//        new ServerHandler().sendToServer(this, "login", m, 0, new CallBack() {
//            @Override
//            public void getRespone(String data, ArrayList<Object> respons) {
//                if (data.equals("error")) {
//                } else {
//                    try {
//                        JSONObject obj = new JSONObject(data);
//                        String status = obj.get("status") + "";
//                        if (status.equalsIgnoreCase("false")) {
//
//                            Toast.makeText(VerifyOtpScreen.this, obj.getString("message"), Toast.LENGTH_LONG).show();
//
//                        } else {
//
//
//                            new SaveImpPrefrences().savePrefrencesData(VerifyOtpScreen.this, obj.getJSONObject("data"), "user_data");
//                            new SaveImpPrefrences().savePrefrencesData(VerifyOtpScreen.this, "yes", "isQuarantine");
//
//                            Intent i = new Intent(VerifyOtpScreen.this, UploadSelfieNew.class);
//                            startActivity(i);
//                            finish();
//
//
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//
//                    }
//                }
//
//            }
//        });
//    }
//}
//
//
//
//
//
