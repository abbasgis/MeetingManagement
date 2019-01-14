package info.pnddch.meetingmanagement;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ViewDataListActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    EditText inputSearch;
    private ListView lv;
    AssignmentAdapter adapter;
    String activity_title;
    private static int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        this.context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_list);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_CODE);
        Toolbar view_data_toolbar = (Toolbar) findViewById(R.id.view_data_toolbar);
        TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        view_data_toolbar.setTitle("");
        setSupportActionBar(view_data_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        if (intent != null) {
            try {
                String strJsonDataArray = intent.getStringExtra("strJsonDataArray");
                activity_title = intent.getStringExtra("title");
                tvToolbarTitle.setText(activity_title);
                JSONArray data = new JSONArray(strJsonDataArray);
                createGroupListView(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void createGroupListView(final JSONArray data) {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                int assignment_id = data.getJSONObject(i).getInt("id");
                String assignment_name = data.getJSONObject(i).getString("name");
                String assignment_duedate = data.getJSONObject(i).getString("date");
                assignmentList.add(new Assignment(assignment_id, assignment_name, assignment_duedate, "", "", false));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.lv = (ListView) findViewById(R.id.list_view_data);
        this.inputSearch = (EditText) findViewById(R.id.inputSearch);
        this.inputSearch.addTextChangedListener(filterTextWatcher);
        this.adapter = new AssignmentAdapter(this, assignmentList, "ViewDataListActivity");
        this.lv.setAdapter(this.adapter);
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Assignment assignment = (Assignment) parent.getItemAtPosition(position);
                    String name = assignment.getName();
                    String url = "http://pnddch.info/media/mm/";
                    if (activity_title.equalsIgnoreCase("Photos")) {
                        url = url + "pics/" + name;
                        showURLinBrowser(url);
                    } else if (activity_title.equalsIgnoreCase("Attachments")) {
                        url = url + "docs/" + name;
                        downLoadFileFromURL(url);
                    } else if (activity_title.equalsIgnoreCase("Quick Links")) {
                        url = assignment.getDueDate();
                        showURLinBrowser(url);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void showURLinBrowser(String url) {
        Intent intentURLViewerActivity = new Intent(context, URLViewerActivity.class);
        intentURLViewerActivity.putExtra("url", url);
        intentURLViewerActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        activity.startActivityIfNeeded(intentURLViewerActivity, 0);
    }

    private void downLoadFileFromURL(String url) {
        final long downloadID = startDownload(url, activity_title);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == reference) {
                    Toast toast = Toast.makeText(context,
                            "Download Complete", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 25, 400);
                    toast.show();
                }
            }
        };
        registerReceiver(receiver, filter);
    }

    public long startDownload(String url, String filename) {
        DownloadManager mManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = new DownloadManager.Request(Uri.parse(url));
//        mRqRequest.setDescription("This is Test File");
        mRqRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
        mRqRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        long idDownLoad = mManager.enqueue(mRqRequest);
        return idDownLoad;

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (adapter != null) {
                adapter.getFilter().filter(s);
            } else {
                Log.d("filter", "no filter availible");
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                break;

        }
        return true;
    }
}
