package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;
import info.pnddch.meetingmanagement.utilities.FERRPDialogs;

public class AssignmentDetailActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    String[] colName;
    String[] colVal;
    ListView list;
    int assignmentId;
    JSONObject assignmentDetail;
    private CMResponse assignmentsDetailResponse = new CMResponse() {
        @Override
        public void consumeResponse(String result, boolean z) throws JSONException {
//            JSONArray arrResult = new JSONArray(result);
            assignmentDetail = new JSONObject(result);
            showAssignmentDetail(assignmentDetail);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        this.context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_detail);
        ImageButton btnHome = (ImageButton) findViewById(R.id.btnHomeDetailToolBar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.asgn_detail_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        Intent intent = getIntent();
        if (intent != null) {
            try {
                String assignment_id = intent.getStringExtra("assignment_id");
                assignmentId = Integer.parseInt(assignment_id);
                JSONObject params = new JSONObject();
                params.put("assignment_id", assignment_id);
                new CommunicationManager(this.activity).postRequest("mb_get_assignment_detail/", params, this.assignmentsDetailResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }
        });

    }

    private void showAssignmentDetail(JSONObject obj) {
        try {
            this.colName = new String[obj.length()];
            this.colVal = new String[obj.length()];
            Iterator<String> keysIterator = obj.keys();
            int i = 0;
            while (keysIterator.hasNext()) {
                String keyStr = (String) keysIterator.next();
                String valueStr = obj.getString(keyStr);
                this.colName[i] = keyStr;
                this.colVal[i] = valueStr;
                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AssignmentDetailAdapter adapter = new AssignmentDetailAdapter(this, this.colName, this.colVal);
        this.list = (ListView) findViewById(R.id.list);
        this.list.setAdapter(adapter);
        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Object assignment = parent.getItemAtPosition(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar_assignments_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuBack:
                onBackPressed();
                break;
            case R.id.menuAdd:
                Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuEdit:
                Toast.makeText(this, "You clicked Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuDelete:
                Toast.makeText(this, "You clicked Delete", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuRemarks:
                Intent assignment_listIntent = new Intent(AssignmentDetailActivity.this.context, RemarksHistoryListActivity.class);
                AssignmentDetailActivity.this.onPause();
                assignment_listIntent.putExtra("assignment_id", Integer.toString(assignmentId));
                assignment_listIntent.putExtra("from", AssignmentDetailActivity.this.activity.getIntent().getComponent().getClassName());
                assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                AssignmentDetailActivity.this.startActivityIfNeeded(assignment_listIntent, 0);
                break;

            case R.id.menuEmail:
                try {
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = assignmentDetail.getString("Meeting Agenda");
                    String shareSub = assignmentDetail.getString("Assignment");
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(myIntent, "Share using"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.menuPhoto:
                try {
                    String photos = assignmentDetail.getString("Photos");
                    if (photos.equalsIgnoreCase("null")) {
                        FERRPDialogs.showFERRPInfoAlert(activity, "Photos", "No photo attached");
                    } else {
                        showDataInListActivity(photos, "Photos");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.menuDocuments:
                try {
                    String attachments = assignmentDetail.getString("Attachments");
                    if (attachments.equalsIgnoreCase("null")) {
                        FERRPDialogs.showFERRPInfoAlert(activity, "Attachments", "No file attached");
                    } else {
                        showDataInListActivity(attachments, "Documents");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        return true;
    }

    private void showDataInListActivity(String data, String title) {
        try {
            String[] parts = data.split(";");
            JSONArray arrData = new JSONArray();
            for (int i = 0; i < parts.length; i++) {
                JSONObject obj = new JSONObject();
                obj.put("id", i);
                obj.put("name", parts[i]);
                obj.put("date", "");
                arrData.put(obj);
            }
            Intent intent = new Intent(AssignmentDetailActivity.this.context, ViewDataListActivity.class);
            AssignmentDetailActivity.this.onPause();
            intent.putExtra("strJsonDataArray", arrData.toString());
            intent.putExtra("title", title);
            intent.putExtra("from", AssignmentDetailActivity.this.activity.getIntent().getComponent().getClassName());
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            AssignmentDetailActivity.this.startActivityIfNeeded(intent, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
