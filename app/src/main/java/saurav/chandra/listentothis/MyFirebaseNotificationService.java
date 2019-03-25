package saurav.chandra.listentothis;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseNotificationService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseNotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage == null) {
            return;
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("notif: ", remoteMessage.getNotification().getBody());
        }
    }
}