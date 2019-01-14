package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import info.pnddch.meetingmanagement.utilities.CustomGrid;
import info.pnddch.meetingmanagement.utilities.ScheduledService;
import info.pnddch.meetingmanagement.utilities.SessionManager;
import info.pnddch.meetingmanagement.utilities.UtilitiesManager;


public class MainActivity extends AppCompatActivity {
    Activity activity;
    Context context;
    static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.context = this;
        this.activity = this;
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createGUI();
        new UtilitiesManager(this.context, this).createMMDirectories();
    }

    private void createGUI() {
        GridView gridViewMenu = (GridView) findViewById(R.id.gridViewMenu);
        final String[] menuNameList = new String[]{"Quick Tasks", "Important Initiatives", "Short-term Initiatives", "Long-term Initiatives", "Calender", "Quick Links"};
        final CustomGrid gridAdapter = new CustomGrid(this, menuNameList, new int[]{R.drawable.task_all, R.drawable.task_imp, R.drawable.task_short, R.drawable.task_long, R.drawable.calender, R.drawable.links});
        gridViewMenu.setAdapter(gridAdapter);
        final Activity activity = this;
        new UtilitiesManager(this.context, this).createMMDirectories();
        FloatingActionButton fabLogout = (FloatingActionButton) findViewById(R.id.fabLogout);
        FloatingActionButton fabLogIn = (FloatingActionButton) findViewById(R.id.fabLogIn);
        if (SessionManager.getUserId(this.context).equalsIgnoreCase("-1")) {
            fabLogout.setVisibility(View.INVISIBLE);
            activity.finishAffinity();
            activity.startActivity(new Intent(MainActivity.this.context, LoginActivity.class));
        } else {
            fabLogIn.setVisibility(View.INVISIBLE);

        }
        fabLogIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                activity.finishAffinity();
                activity.startActivity(new Intent(MainActivity.this.context, LoginActivity.class));
            }
        });
        fabLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SessionManager.clearSessionData(MainActivity.this.context);
                FloatingActionButton fabLogIn = (FloatingActionButton) MainActivity.this.findViewById(R.id.fabLogIn);
                ((FloatingActionButton) MainActivity.this.findViewById(R.id.fabLogout)).setVisibility(View.INVISIBLE);
                fabLogIn.setVisibility(View.VISIBLE);
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                mGoogleSignInClient.signOut().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT).show();
                        activity.finishAffinity();
                        activity.startActivity(new Intent(MainActivity.this.context, LoginActivity.class));
                    }
                });
            }
        });
//        fabLogout.setOnClickListener(new C03162());
        gridViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                gridAdapter.performMenuItemAction(activity, MainActivity.this.context, menuNameList[position]);
            }
        });
        if (!checkServiceRunning() && !SessionManager.getUserId(context).equalsIgnoreCase("-1")) {
            Intent i = new Intent(context, ScheduledService.class);
            context.startService(i);
        }

    }

    public boolean checkServiceRunning() {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals("info.pnddch.meetingmanagement.utilities.ScheduledService")) {
                return true;
            }
        }
        return false;
    }
}
