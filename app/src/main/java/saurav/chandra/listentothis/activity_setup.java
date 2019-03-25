package saurav.chandra.listentothis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import mbanje.kurt.fabbutton.FabButton;

public class activity_setup extends Activity{

    private FirebaseAuth mAuth;

    TextView activity_setup_text;
    EditText activity_setup_user_name;
    FabButton activity_setup_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();

        activity_setup_text = findViewById(R.id.activity_setup_text);
        activity_setup_user_name = findViewById(R.id.activity_setup_user_name);
        activity_setup_button = findViewById(R.id.activity_setup_button);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity_setup_text.invalidate();
                activity_setup_text.setText(R.string.setup_text_2);
                activity_setup_user_name.setVisibility(View.VISIBLE);
                activity_setup_user_name.setFocusableInTouchMode(true);
                activity_setup_user_name.requestFocus();
            }
        }, 2000);

        activity_setup_user_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(activity_setup_user_name.getText().toString().trim().length() != 0 && activity_setup_user_name.getText().toString().trim().length() > 3 ){
                    activity_setup_button.setVisibility(View.VISIBLE);
                }

                if(activity_setup_user_name.getText().toString().trim().length() < 3 ){
                    activity_setup_button.setVisibility(View.GONE);
                }
            }
        });

        activity_setup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressHelper helper = new ProgressHelper(activity_setup_button,activity_setup.this);
                helper.startIndeterminate();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = getSharedPreferences("App", MODE_PRIVATE);
                        prefs.edit().putString("user_name",activity_setup_user_name.getText().toString().trim()).apply();

                        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                String uid = mAuth.getCurrentUser().getUid();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userNameRef = database.getReference(uid).child("user_name");
                                DatabaseReference fcmRef = database.getReference(uid).child("fcm_ids");
                                fcmRef.setValue(getSharedPreferences("App", MODE_PRIVATE).getString("fcm_id","none"));
                                userNameRef.setValue(activity_setup_user_name.getText().toString().trim());
                                Intent start_main_activity_intent = new Intent(activity_setup.this, activity_main.class);
                                start_main_activity_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(start_main_activity_intent);
                                finish();
                            }
                        });

                    }
                }, 1000);

            }
        });
    }
}
