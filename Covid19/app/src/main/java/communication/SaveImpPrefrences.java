package communication;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import apps.envision.musicvibes.CConstant;

public class SaveImpPrefrences
{
    private String prefName = "covidcontrol";

    public void savePrefrencesData(Context ct, Object data, String sharekey)
    {
        SharedPreferences sharedpreferences = ct.getSharedPreferences(prefName, ct.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();


        if (sharekey.equals("url")) {
            try {
                editor.putString("url", data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }
       else if (sharekey.equals("user_data")) {
            try {
                editor.putString(sharekey, data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }



        else if (sharekey.equals("device_token")) {
            try {
                editor.putString("device_token", data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }


        else if (sharekey.equals("isQuarantine")) {
            try {
                editor.putString("isQuarantine", data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }
        else if (sharekey.equals("isNormalUser")) {
            try {
                editor.putString("isNormalUser", data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }

        else if (sharekey.equals("Nu_mobile")) {
            try {
                editor.putString("Nu_mobile", data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }

        }

        else if (sharekey.equals(CConstant.key_PassStatus)) {
            try {
                editor.putString(CConstant.key_PassStatus, data + "");
            } catch (Exception e) {
                Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
            }
        }
          else if (sharekey.equals(CConstant.key_downloadid)) {
                try {
                    editor.putString(CConstant.key_downloadid, data + "");
                } catch (Exception e) {
                    Toast.makeText(ct, e.getMessage() + " ", Toast.LENGTH_LONG).show();
                }




        }









        editor.commit();
    }

    public Object reterivePrefrence(Context ct, String shareKey)
      {
        if (ct != null) {
            SharedPreferences prefs = ct.getSharedPreferences(prefName, ct.MODE_PRIVATE);

            if (shareKey.equals("user_data")) {
                if (prefs.contains("user_data"))//sarpanch pass
                {
                    return prefs.getString("user_data", "");
                }
            }


            else if (shareKey.equals("device_token")) {
                if (prefs.contains("device_token"))// pass
                {
                    return prefs.getString("device_token", "");
                }
            }



            else if (shareKey.equals("isQuarantine")) {
                if(prefs.contains("isQuarantine"))// pass
                {
                    return prefs.getString("isQuarantine", "");
                }
            }
            else if (shareKey.equals("isNormalUser")) {
                if(prefs.contains("isNormalUser"))// pass
                {
                    return prefs.getString("isNormalUser", "");
                }
            }

            else if (shareKey.equals("Nu_mobile")) {
                if(prefs.contains("Nu_mobile"))// pass
                {
                    return prefs.getString("Nu_mobile", "");
                }
            }

            else if (shareKey.equals(CConstant.key_PassStatus)) {
                if(prefs.contains(CConstant.key_PassStatus))// pass
                {
                    return prefs.getString(CConstant.key_PassStatus, "");
                }
            }

            else if (shareKey.equals(CConstant.key_downloadid)) {
                if(prefs.contains(CConstant.key_downloadid))// pass
                {
                    return prefs.getString(CConstant.key_downloadid, "");
                }
            }








          }





            return "0";
        }


}
