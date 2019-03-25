package saurav.chandra.listentothis;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hsalf.smilerating.SmileRating;
import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class activity_main extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private FirebaseAuth mAuth;
    TextView activity_main_user_name;
    private YouTubePlayerView youTubeView;
    SmileRating smileRating;
    FirebaseDatabase database;
    private long enqueue;
    private DownloadManager dm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        database.getReference("update_available").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String update_val = dataSnapshot.getValue().toString();
                try {
                    PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                    int version = pInfo.versionCode;
                    if(Integer.parseInt(update_val) > version){
                        final AlertDialog.Builder builder = new AlertDialog.Builder(activity_main.this);

                        builder.setTitle("Listen To This");
                        builder.setIcon(R.mipmap.logo);
                        builder.setCancelable(false);
                        builder.setMessage("Latest Version is Available. Click on OK to update.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity_main.this, new String[]{WRITE_EXTERNAL_STORAGE} , 1);
                            }
                        });
                        builder.setNegativeButton("Remind Me Later",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                        positiveButton.setTextColor(Color.parseColor("#FF7B7979"));
                        negativeButton.setTextColor(Color.parseColor("#FF7B7979"));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuth = FirebaseAuth.getInstance();

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);

        activity_main_user_name = findViewById(R.id.activity_main_user_name);

        smileRating = (SmileRating) findViewById(R.id.smile_rating);
        TextView footer_1 = (TextView) findViewById(R.id.footer_1);
        footer_1.setText("Made with ♥ for homies by ");
        TextView footer_2 = (TextView) findViewById(R.id.footer_2);
        footer_2.setText(R.string.footer_text_2);
        footer_2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void downloadAndInstall() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://github.com/sauravchandra/listentothis/raw/master/ListenToThis.apk"));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "listentothis.apk");
        enqueue = dm.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_LONG).show();
                dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                downloadAndInstall();
            }
            else if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) ){
                ActivityCompat.requestPermissions(activity_main.this, new String[]{WRITE_EXTERNAL_STORAGE} , 1);
            }
            else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity_main.this);

                builder.setTitle("Permission Denied");
                builder.setIcon(R.mipmap.logo);
                builder.setCancelable(false);
                builder.setMessage("Storage permission is required to update the app. To continue allow the permission from settings.");
                builder.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("CANCEL",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                positiveButton.setTextColor(Color.parseColor("#FF7B7979"));
                negativeButton.setTextColor(Color.parseColor("#FF7B7979"));
            }
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.cuePlaylist("PLJCnquUeq6ebGChx9wFTK8Dn6rPaqE1WL",0,0);

            smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
                @Override
                public void onRatingSelected(int level, boolean reselected) {
                    DatabaseReference ratingRef = database.getReference(mAuth.getCurrentUser().getUid()).child("ratings");
                    ratingRef.setValue(level);
                }
            });
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        IntentFilter iff= new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(broadcastReceiver, iff);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        registerReceiver(broadcastReceiverinstall, intentFilter);
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiverinstall);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long downloadId = intent.getLongExtra( DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(enqueue);
            Cursor c = dm.query(query);
            if (c.moveToFirst()) {
                int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                    String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));

                    Log.d("ainfo", uriString);

                    if (downloadId == c.getInt(0)) {
                        Log.d("DOWNLOAD PATH:", c.getString(c.getColumnIndex("local_uri")));
                        //if your device is not rooted
                        Intent intent_install = new Intent(Intent.ACTION_VIEW);
                        intent_install.setDataAndType(FileProvider.getUriForFile(activity_main.this, "saurav.chandra.listentothis.fileprovider", new File(Environment.getExternalStorageDirectory() + "/Download/" + "listentothis.apk")), "application/vnd.android.package-archive");
                        intent_install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent_install);
                    }
                }
            }
            c.close();
        }
    };

    private BroadcastReceiver broadcastReceiverinstall = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String app = intent.getData().toString();
            Log.d("xxx_data",app);
            if (app.equals("package:saurav.chandra.listentothis")) {
                ContentResolver contentResolver = getContentResolver();
                contentResolver.delete(FileProvider.getUriForFile(activity_main.this, "saurav.chandra.listentothis.fileprovider", new File(Environment.getExternalStorageDirectory() + "/Download/" + "listentothis.apk")), null, null);
            }
        }
    };


    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }
}