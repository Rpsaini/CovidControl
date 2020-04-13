package communication;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apps.envision.musicvibes.CConstant;
import apps.envision.musicvibes.R;


public class ServerHandler {
    private Dialog progressdlg;

    private Context ct1;
    private String url1="";
    public ServerHandler() {
    }

    public boolean CheckInternetState(Context ct, int sholoader) {
        try {

            ConnectivityManager cm = (ConnectivityManager) ct.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                return true;
            } else {
                if (sholoader <= 0) {
                    Toast.makeText(ct1,"Network Error !",Toast.LENGTH_LONG).show();

                }
                return false;
            }
        } catch (Exception e) {
            //Toast.makeText(ct, "Exception CheckNetState", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void sendToServer(final Context ct,  String url, final Map<String, String> data, final int showloader, final CallBack cb) {

        ct1=ct;

        if (progressdlg != null && progressdlg.isShowing()) {
            progressdlg.dismiss();
        }
        if (CheckInternetState(ct, showloader)) {
            try
            {
              url1= CConstant.baseUrl +""+url;
              data.put("device_type","android");
              data.put("device_info",getDeviceName());
              data.put("device_token", new SaveImpPrefrences().reterivePrefrence(ct,"device_token")+"");
              System.out.println("before to send=="+data);
              StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //    Toast.makeText(ct, "response back " + response, Toast.LENGTH_LONG).show();

                                    System.out.println("respone back==="+url1+"="+response);
                                    cb.getRespone(response, null);
                                    progressdlg.dismiss();

                                } catch (Exception e) {
                                    cb.getRespone("error", null);
                                    if (showloader <= 0)
                                        Toast.makeText(ct1,"Network Error !",Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                try {
                                    System.out.println("error===="+error.getMessage());
                                    cb.getRespone("error", null);
                                    if (progressdlg != null && progressdlg.isShowing()) {
                                        progressdlg.dismiss();
                                    }
                                    if (showloader <= 0) {
                                        Toast.makeText(ct1,"Network Error !",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        return data;
                    }


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        Map<String, String> params=new HashMap<>();
                        try {
                            ArrayList<String> headerRet=getHashKey(url1);
                            params.put("Authstring", headerRet.get(0));
                            params.put("Timestamp", headerRet.get(1));
                            params.put("x-api-key", "CODEX@123");
                            params.put("Content-Type", "application/x-www-form-urlencoded");

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        return params;
                    };
                };

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


                RequestQueue requestQueue = Volley.newRequestQueue(ct);
                requestQueue.add(stringRequest);

                stringRequest.setShouldCache(true);
                showProgressDialog();



                progressdlg.show();

                if (showloader >= 1) {
                    progressdlg.dismiss();
                }
            } catch (Exception e) {
                if (progressdlg != null)
                    progressdlg.dismiss();

            }
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    private ArrayList<String> getHashKey(String url)
    {
        String[]urlArray=url.split("/");
        String methodName=urlArray[urlArray.length-1];
        String key="AGSDF34DFGSDFG345DFGDF344545DFGDFG6756fg";
        long timeStamp= System.currentTimeMillis();
        System.out.println("method name=="+methodName+"==="+key+"=="+timeStamp);
        ArrayList<String> headerArray=new ArrayList<>();
        headerArray.add(md5(methodName+key+timeStamp));
        headerArray.add(timeStamp+"");
        return  headerArray;
    }

  public String md5(String s) {

      String password = null;
      MessageDigest mdEnc;
      try {
          mdEnc = MessageDigest.getInstance("MD5");
          mdEnc.update(s.getBytes(), 0, s.length());
         String pass = new BigInteger(1, mdEnc.digest()).toString(16);
          while (pass.length() < 32) {
              pass = "0" + pass;
          }
          password = pass;
      } catch (Exception e1) {
          e1.printStackTrace();
      }
      return password;
   }



private void showProgressDialog()
{
    progressdlg = new Dialog(ct1);
    progressdlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
    progressdlg.setContentView(R.layout.showprogressdialog);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    Window window = progressdlg.getWindow();
    lp.copyFrom(window.getAttributes());
    progressdlg.setCancelable(false);


    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
    window.setAttributes(lp);
    progressdlg.getWindow().setBackgroundDrawableResource(R.color.translucent_black);


    progressdlg.getWindow().setDimAmount(0);
    progressdlg.show();

}


}
