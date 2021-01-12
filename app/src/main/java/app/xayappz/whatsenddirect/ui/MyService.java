package app.xayappz.whatsenddirect.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.net.URLEncoder;

import app.xayappz.whatsenddirect.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyService extends Service {
    private MyPhoneStateListener phoneListener = null;
    private Handler h;
    private Runnable r;
    BroadcastReceiver phoneReceiver;
    public static String number = "";
    TelephonyManager telephony;
    SharedPreferences prefs;
    private static final String MyOnClick = "Dialer";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification updateNotification() {
        String info = "My Intro Text";

        Context context = getApplicationContext();

        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT); // Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.custom_notification);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            prefs = getApplication().getSharedPreferences("XayPrefs", MODE_PRIVATE);
            String CHANNEL_ID = "xay channel";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "xayChannel",
                    NotificationManager.IMPORTANCE_NONE);
            channel.setDescription("xay channel description");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            remoteViews.setTextViewText(R.id.introTxtNoti, prefs.getString("introText", ""));
            builder.setContent(remoteViews);

            Intent notificationIntent = new Intent(Intent.ACTION_DIAL);
            notificationIntent.setData(Uri.parse("tel:+91"));
            notificationIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);


            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent
                    , 0);
            remoteViews.setOnClickPendingIntent(R.id.sendtxt, pendingNotificationIntent);

        } else {

            prefs = getApplication().getSharedPreferences("XayPrefs", MODE_PRIVATE);


            builder = new NotificationCompat.Builder(this);
            remoteViews.setTextViewText(R.id.introTxtNoti, prefs.getString("introText", ""));
            builder.setContent(remoteViews);

            Intent notificationIntent = new Intent(Intent.ACTION_DIAL);
            notificationIntent.setData(Uri.parse("tel:+91"));
            notificationIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);


            PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent
                    , 0);
            remoteViews.setOnClickPendingIntent(R.id.sendtxt, pendingNotificationIntent);

        }


        return builder.setContentIntent(null)


                .setSmallIcon(R.drawable.logo)
                //.setContentIntent(action)
                .setOngoing(true).build();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().contains("start")) {
            h = new Handler();
            r = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    startForeground(101, updateNotification());

                    new Xay();


                    h.postDelayed(this, 1000);
                }
            };

            h.post(r);
        } else {
            h.removeCallbacks(r);
            stopForeground(true);
            stopSelf();
        }

        return Service.START_NOT_STICKY;
    }

    public class Xay extends BroadcastReceiver {


        Xay() {
            prefs = getApplication().getSharedPreferences("XayPrefs", MODE_PRIVATE);
            phoneListener = new MyPhoneStateListener();
            telephony = (TelephonyManager) getApplication()
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);


        }

        @Override
        public void onReceive(Context context, Intent intent) {
            prefs = context.getSharedPreferences("XayPrefs", MODE_PRIVATE);
            phoneListener = new MyPhoneStateListener();
            telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Intent i = new Intent();
                i.setAction("start");
                context.startService(i);
            }
        }
    }

    public class MyPhoneStateListener extends PhoneStateListener {


        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    if (incomingNumber.endsWith("*")) {

                        String numberr = number;


                        //  String introText = prefs.getString("introText", "hi there!");


                        if (numberr.endsWith("*")) {

                            String introText = prefs.getString("introText", "");
                            openWhatsapp(getApplicationContext(), numberr, introText);


                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:

                    break;
            }
            number = incomingNumber;
        }

    }


    void openWhatsapp(Context context, String number, String txt) {
        number = number.replace(" ", "");

        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number.substring(1).trim() + "&text=" + URLEncoder.encode(txt, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
            if (i.resolveActivity(packageManager) != null) {
                Log.e("2", "WHATSAPP");
                context.startActivity(i);
            }else
            {
                Toast.makeText(context, "WhatsApp is not installed!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Err", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }


}
