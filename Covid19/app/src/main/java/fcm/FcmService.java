package fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import communication.SaveImpPrefrences;


public class FcmService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String registrationToken= FirebaseInstanceId.getInstance().getToken();
        SaveImpPrefrences imp=new SaveImpPrefrences();
        imp.savePrefrencesData(getApplicationContext(),registrationToken,"device_token");
    }
}
