package info.pnddch.meetingmanagement.utilities;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import info.pnddch.meetingmanagement.MainActivity;

public class ScheduledService extends Service {
    Activity activity;
    Context context;
    Intent serviceIntent;
    int mStartMode;
    private Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        final SyncManager smg = new SyncManager();
        activity = MainActivity.getInstance();
        context = MainActivity.getInstance();
        super.onCreate();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isConnected(context)) {
                    smg.syncQuickTasks();
                }
//            sendRequestToServer();   //Your code here
            }
        }, 0, 1 * 60 * 1000);//5 Minutes
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        // The service is starting, due to a call to startService()
//        return mStartMode;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        context.stopService(serviceIntent);
        super.onDestroy();
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}