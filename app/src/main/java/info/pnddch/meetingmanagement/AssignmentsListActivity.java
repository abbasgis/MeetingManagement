package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import org.json.JSONObject;

import java.util.ArrayList;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;
import info.pnddch.meetingmanagement.utilities.SessionManager;

public class AssignmentsListActivity extends AppCompatActivity {
    private Activity activity;
    private int REQUEST_READ_PHONE_STATE = 1;
    private Context context;
    EditText inputSearch;
    private ListView lv;
    String response;
    AssignmentAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        this.context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignments_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        try {
            String init_type = "all";
            Intent intent = getIntent();
            if (intent != null) {
                init_type = intent.getStringExtra("init_type");
                TextView tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
                String activity_title = intent.getStringExtra("title");
                tvToolbarTitle.setText(activity_title);
            }
            JSONObject params = new JSONObject();
            params.put("init_type", init_type);
            params.put("token", SessionManager.getToken(context));
            params.put("auth_code", SessionManager.getAuthCode(context));
            new CommunicationManager(this.activity).postRequest("mb_get_assignments_data/", params, this.assignmentsResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    CMResponse assignmentsResponse = new CMResponse() {
        @Override
        public void consumeResponse(String result, boolean success) throws JSONException {
            if (success) {
                AssignmentsListActivity.this.response = result;
                AssignmentsListActivity.this.createGroupListView(new JSONArray(result));
            }
        }
    };

    public void createGroupListView(final JSONArray data) {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                int assignment_id = data.getJSONObject(i).getInt("id");
                String assignment_name = data.getJSONObject(i).getString("assignment");
                String assignment_duedate = "Due Date: " + data.getJSONObject(i).getString("due_date");
                assignmentList.add(new Assignment(assignment_id, assignment_name, assignment_duedate,"","",false));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.lv = (ListView) findViewById(R.id.list_view);
        this.inputSearch = (EditText) findViewById(R.id.inputSearch);
        this.inputSearch.addTextChangedListener(filterTextWatcher);
//        Collections.sort(arl, String.CASE_INSENSITIVE_ORDER);
        this.adapter = new AssignmentAdapter(this, assignmentList,"AssignmentsListActivity");
//                new ArrayAdapter(this, R.layout.list_item, R.id.assignment_name, arl);
        this.lv.setAdapter(this.adapter);
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {

                    Assignment assignment = (Assignment) parent.getItemAtPosition(position);
                    int assignmentId = assignment.getAssignmentId();
                    Intent assignment_listIntent = new Intent(AssignmentsListActivity.this.context, AssignmentDetailActivity.class);
                    AssignmentsListActivity.this.onPause();
                    assignment_listIntent.putExtra("assignment_id", Integer.toString(assignmentId));
                    assignment_listIntent.putExtra("from", AssignmentsListActivity.this.activity.getIntent().getComponent().getClassName());
                    assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    AssignmentsListActivity.this.startActivityIfNeeded(assignment_listIntent, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar_assignments_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                break;
            case R.id.menuAdd:
                Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuImportant:
                getInitiativeDataAndShowActivity("important","Important Initiatives");
                break;

            case R.id.menuShort:
                getInitiativeDataAndShowActivity("short","Short-term Initiatives");
                break;

            case R.id.menuLong:
                getInitiativeDataAndShowActivity("long","Long-term Initiatives");
                break;

        }
        return true;
    }

    private void getInitiativeDataAndShowActivity(String initiative_type,String activity_title) {
        Intent assignment_listIntent = new Intent(context, AssignmentsListActivity.class);
        assignment_listIntent.putExtra("title", activity_title);
        assignment_listIntent.putExtra("init_type", initiative_type);
        assignment_listIntent.putExtra("from", activity.getIntent().getComponent().getClassName());
        assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        activity.startActivityIfNeeded(assignment_listIntent, 0);
        finish();
    }
}
