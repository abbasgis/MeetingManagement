package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;
import info.pnddch.meetingmanagement.utilities.DatabaseManager;
import info.pnddch.meetingmanagement.utilities.FERRPDialogs;
import info.pnddch.meetingmanagement.utilities.SessionManager;

public class QuickTaskListActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    EditText inputSearch;
    private ListView lv;
    AssignmentAdapter adapter;
    private int selectedTaskId = -1;
    DatabaseManager dbm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        this.context = this;
        dbm = new DatabaseManager(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_task_list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.quick_task_toolbar);
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
            getTasksFromDB();
//            JSONObject params = new JSONObject();
//            params.put("init_type", init_type);
//            params.put("token", SessionManager.getToken(context));
//            params.put("auth_code", SessionManager.getAuthCode(context));
//            new CommunicationManager(this.activity).postRequest("mb_get_assignments_data/", params, this.quickTaskResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getTasksFromDB() {
        try {
            JSONArray rs = dbm.selectResultFromDB("select *  from tbl_quick_tasks order by updated_at desc");
            createGroupListView(rs);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    CMResponse quickTaskResponse = new CMResponse() {
        @Override
        public void consumeResponse(String result, boolean success) throws JSONException {
            if (success) {
                createGroupListView(new JSONArray(result));
            }
        }
    };

    public void createGroupListView(final JSONArray data) {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                int assignment_id = data.getJSONObject(i).getInt("id");
                String assignment_name = data.getJSONObject(i).getString("task_name");
                String assignment_duedate = "Due Date:" + changeDateStringOrder(data.getJSONObject(i).getString("task_date"));
                String assignDate = changeDateStringOrder(data.getJSONObject(i).getString("task_date"));
                String assignTo = "Assign To: ";
                String assigned_to = data.getJSONObject(i).getString("assigned_to");
                JSONArray rs = dbm.selectResultFromDB("Select * from tbl_users where id=" + assigned_to);
                if (rs.length() > 0) {
                    JSONObject user = rs.getJSONObject(0);
                    assignTo = assignTo + user.getString("name");
                    if (!user.isNull("designation")) {
                        assignTo = assignTo + " (" + user.getString("designation") + ")";
                    }
                }
                String is_completed = data.getJSONObject(i).getString("is_completed");
                Boolean is_completed_bool = false;
                if (is_completed.equalsIgnoreCase("1") || is_completed.equalsIgnoreCase("true") || is_completed.equalsIgnoreCase("t")) {
                    is_completed_bool = true;
//                    id_tv_is_completed.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ok, 0, 0, 0);
                }
                assignmentList.add(new Assignment(assignment_id, assignment_name, "", assignDate, assignTo, is_completed_bool));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.lv = (ListView) findViewById(R.id.list_view);
        registerForContextMenu(this.lv);
        this.inputSearch = (EditText) findViewById(R.id.inputSearch);
        this.inputSearch.addTextChangedListener(filterTextWatcher);
        this.adapter = new AssignmentAdapter(this, assignmentList, "QuickTaskListActivity");
        this.lv.setAdapter(this.adapter);
        this.lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        this.lv.setSelector(android.R.color.darker_gray);
        this.lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Assignment assignment = (Assignment) parent.getItemAtPosition(position);
                    int assignmentId = assignment.getAssignmentId();
                    if (selectedTaskId == assignmentId) {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                    selectedTaskId = assignmentId;
//                    ListView list = (ListView) findViewById(R.id.list_view);
//                    registerForContextMenu(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_view) {
            menu.setHeaderTitle("Task Options");
            menu.add(Menu.NONE, 0, 0, "Edit this task");
            menu.add(Menu.NONE, 1, 1, "Delete this task");
            menu.add(Menu.NONE, 2, 2, "Share this task");
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Assignment assignment = (Assignment) adapter.getItem(menuInfo.position);
        if (item.getTitle() == "Edit this task") {
            try {
                String task_id = String.valueOf(assignment.getAssignmentId());
                Intent addEditTaskIntent = new Intent(context, AddEditQuickTaskActivity.class);
                QuickTaskListActivity.this.onPause();
                addEditTaskIntent.putExtra("task_id", task_id);
                addEditTaskIntent.putExtra("from", QuickTaskListActivity.this.activity.getIntent().getComponent().getClassName());
                addEditTaskIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                QuickTaskListActivity.this.startActivityIfNeeded(addEditTaskIntent, 0);
            } finally {
                finish();
            }
        } else if (item.getTitle() == "Delete this task") {
            try {
                if (isConnected(context)) {
                    deleteTaskConfirmDialog(String.valueOf(assignment.getAssignmentId()));
                } else {
                    FERRPDialogs.showFERRPInfoAlert(activity, "Internet Status", "Please connect with internet");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (item.getTitle() == "Share this task") {
            try {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = assignment.getName();
                String shareSub = "Quick Task";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent, "Share using"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;

    }

    private CMResponse deleteTaskDataResponse = new CMResponse() {
        @Override
        public void consumeResponse(String str, boolean z) throws JSONException {
            FERRPDialogs.showFERRPInfoAlert(activity, "Message", str);
            Intent assignment_listIntent = new Intent(context, QuickTaskListActivity.class);
            assignment_listIntent.putExtra("title", "Quick Tasks");
            assignment_listIntent.putExtra("init_type", "quick_tasks");
            assignment_listIntent.putExtra("from", activity.getIntent().getComponent().getClassName());
            assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            activity.startActivityIfNeeded(assignment_listIntent, 0);
        }
    };
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
        menu.add(0, R.id.icon_add_task, 2, "Add Task").setIcon(R.drawable.add).setShowAsActionFlags(1);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                break;
            case R.id.icon_add_task:
                try {
                    Intent addEditTaskIntent = new Intent(context, AddEditQuickTaskActivity.class);
                    QuickTaskListActivity.this.onPause();
                    addEditTaskIntent.putExtra("data", "");
                    addEditTaskIntent.putExtra("from", QuickTaskListActivity.this.activity.getIntent().getComponent().getClassName());
                    addEditTaskIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    QuickTaskListActivity.this.startActivityIfNeeded(addEditTaskIntent, 0);
                } finally {
                    finish();
                }
                break;

        }
        return true;
    }

    private void deleteTaskConfirmDialog(final String taskId) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle((CharSequence) "Please Confirm");
        dialogBuilder.setMessage((CharSequence) "Are you want to delete? This will also delete from server");
        dialogBuilder.setPositiveButton((CharSequence) "Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JSONArray rs = null;
                try {
                    rs = dbm.selectResultFromDB("select * from tbl_quick_tasks where id=" + taskId);
                    if (rs.length() > 0) {
                        JSONObject task = rs.getJSONObject(0);
                        JSONObject params = new JSONObject();
                        params.put("id", task.getString("id_server"));
                        new CommunicationManager(activity).postRequestScheduleWithoutDialogue("mb_delete_task/", params, deleteTaskDataResponse);
                        dbm.deleteRecord("tbl_quick_tasks", "id=?", new String[]{taskId});
                        finish();
                        startActivity(getIntent());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
//        dialogBuilder.setOnDismissListener(new C02909());
        dialogBuilder.create().show();
    }

    private String changeDateStringOrder(String d) {
        if (d != null && d.indexOf('-') > -1) {
            String[] arr_d = d.split("-");
            return arr_d[2] + "/" + arr_d[1] + "/" + arr_d[0];
        } else {
            return d;
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }
}
