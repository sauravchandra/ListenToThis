package saurav.chandra.listentothis;

import android.app.Application;
import android.content.SharedPreferences;

public class ListenToThis extends Application {

    @Override
    public void onCreate() {

        super.onCreate();
    }

    public Boolean isNameSet() {
        Boolean state;
        SharedPreferences prefs = getSharedPreferences("App", MODE_PRIVATE);
        if(!prefs.getString("user_name","").equals("")){
            state = true;
        }
        else{
            state = false;
        }
        return state;
    }

}


