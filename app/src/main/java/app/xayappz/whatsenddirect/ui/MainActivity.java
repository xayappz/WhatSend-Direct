package app.xayappz.whatsenddirect.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import app.xayappz.whatsenddirect.R;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private Toolbar toolbar;
    private TextView preftv;
    private Button setBn;
    private FloatingActionButton share;
    private SharedPreferences.Editor preferencesEditor;
    private LinearLayout savedIntro;
    private TextInputEditText introTx;
    private String TAG = "XAY-ADD";
    private LinearLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        savedIntro = findViewById(R.id.savedIntro);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkAndroidVersion();
        preftv = findViewById(R.id.preftextv);
        getPrefs();
    }

    private void getPrefs() {

        SharedPreferences shared = getSharedPreferences("XayPrefs", MODE_PRIVATE);
        if (!shared.getString("introText", "").isEmpty()) {
            savedIntro.setVisibility(View.VISIBLE);


            String channel = (shared.getString("introText", "hello...how are you?"));
            preftv.setText(channel);
        } else {
            savedIntro.setVisibility(View.GONE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.htumenu:
                LayoutInflater factory = LayoutInflater.from(this);
                final View deleteDialogView = factory.inflate(R.layout.dialog_how_to_use, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(this).create();
                deleteDialog.setView(deleteDialogView);
                deleteDialog.show();

                break;

            case R.id.rtmenu:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);

                break;


        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            checkPermission();

        } else {
            startIt();
        }


    }

    private void prefs() {
        preferencesEditor = getSharedPreferences("XayPrefs", MODE_PRIVATE).edit();

    }

    private void startIt() {

        prefs();

        introTx = findViewById(R.id.introTxt);
        share = findViewById(R.id.fab);
        setBn = findViewById(R.id.setBn);
        setBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (introTx.length() > 0) {

                    service();

                    String intro = introTx.getText().toString();

                    preferencesEditor.putString("introText", intro);

                    preferencesEditor.apply();
                    Toast.makeText(MainActivity.this, "Intro Saved Successfully", Toast.LENGTH_SHORT).show();
                    introTx.setText("");


                    getPrefs();

                } else {
                    introTx.setError("Enter Your Intro Text");
                }
            }

        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp(MainActivity.this);
            }
        });

    }

    private void service() {
        if (isMyServiceRunning(MyService.class)) return;
        Intent startIntent = new Intent(this, MyService.class);
        startIntent.setAction("start");
        this.startService(startIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void shareApp(Context context) {
        final String appPackageName = context.getPackageName();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Now send your first text in one click to your unknown friend by this Amazing App download now at: https://play.google.com/store/apps/details?id=" + appPackageName);
        startActivity(shareIntent);


    }

    private void checkPermission() {
        int permissionCheckk = ContextCompat.checkSelfPermission(this, Manifest.permission_group.PHONE);

        if (permissionCheckk != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG}, PERMISSIONS_MULTIPLE_REQUEST);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    startIt();
                } else {
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    finishAffinity();
                }
                break;

            default:
                break;
        }
    }
}
