package saurav.chandra.listentothis;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseMessaging.getInstance().subscribeToTopic("song_updated");
        FirebaseMessaging.getInstance().subscribeToTopic("update_available");
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences prefs = getSharedPreferences("App", MODE_PRIVATE);
        prefs.edit().putString("fcm_id",refreshedToken).apply();
    }
}