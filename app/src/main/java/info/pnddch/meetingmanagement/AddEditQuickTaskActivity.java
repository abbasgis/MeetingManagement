package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;
import info.pnddch.meetingmanagement.utilities.DatabaseManager;
import info.pnddch.meetingmanagement.utilities.FERRPDialogs;
import info.pnddch.meetingmanagement.utilities.SessionManager;

public class AddEditQuickTaskActivity extends AppCompatActivity {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView tv_assign_date;
    EditText et_task;
    TextView tv_task_id;
    Spinner spinner_assign_to;
    CheckBox cb_is_completed;
    private Button btn_add_edit_task;
    private int year, month, day;
    private Activity activity;
    private Context context;
    DatabaseManager dbm;
    ArrayAdapter spinnerArrayAdapter;
    JSONArray arrUsers = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.activity = this;
        this.context = this;
        dbm = new DatabaseManager(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_quick_task);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.add_edit_quick_task_toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        tv_task_id = (TextView) findViewById(R.id.tv_task_id);
        et_task = (EditText) findViewById(R.id.et_task);
        spinner_assign_to = (Spinner) findViewById(R.id.spinner_assign_to);
        tv_assign_date = (TextView) findViewById(R.id.tv_assign_date);
        cb_is_completed = ((CheckBox) findViewById(R.id.cb_is_completed));
        btn_add_edit_task = findViewById(R.id.btn_add_edit_task);
//        JSONObject params = new JSONObject();
//        new CommunicationManager(this.activity).postRequest("mb_users_list/", params, this.getUsersDataResponse);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);
        btn_add_edit_task.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                save_add_edit_task_data();
            }
        });
        populateUserDataInSpinnerAssignTo();

    }

    private void populateUserDataInSpinnerAssignTo() {
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Select Any");
//        spinnerArray.add("Add New In List");
        try {
            arrUsers = dbm.selectResultFromDB("Select * from tbl_users order by name");
            for (int i = 0; i < arrUsers.length(); i++) {
                JSONObject row = arrUsers.getJSONObject(i);
                String user = row.getString("name");
                if (!row.isNull("designation")) {
                    user = user + " (" + row.getString("designation") + ")";
                }
//                spinnerArray.add(row.getString("name"));
                spinnerArray.add(user);
            }
            spinnerArrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_assign_to.setAdapter(spinnerArrayAdapter);
//        spinner_assign_to.setBackgroundResource(R.drawable.spinner_background);
            spinner_assign_to.setSelection(0);
            Intent intent = getIntent();
            if (intent != null) {
                try {
                    String task_id = intent.getStringExtra("task_id");
                    if (task_id != "") {
                        JSONArray rs = dbm.selectResultFromDB("Select * from tbl_quick_tasks where id = " + task_id);
                        JSONObject objData = rs.getJSONObject(0);
                        setAddTaskFormValues(objData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CMResponse getUsersDataResponse = new CMResponse() {
        @Override
        public void consumeResponse(String response, boolean success) throws JSONException {
            ArrayList<String> spinnerArray = new ArrayList<>();
            spinnerArray.add("Select Any");
            spinnerArray.add("Add New In List");
            try {
                arrUsers = new JSONArray(response);
                for (int i = 0; i < arrUsers.length(); i++) {
                    JSONObject row = arrUsers.getJSONObject(i);
                    spinnerArray.add(row.getString("name"));
//                    spinnerArray.add(row.getString("id") + ":" + row.getString("name"));
                }
                spinnerArrayAdapter = new ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_assign_to.setAdapter(spinnerArrayAdapter);
//        spinner_assign_to.setBackgroundResource(R.drawable.spinner_background);
                spinner_assign_to.setSelection(0);
                Intent intent = getIntent();
                if (intent != null) {
                    try {
                        String data = intent.getStringExtra("data");
                        if (data != "") {
                            JSONObject objData = new JSONObject(data);
                            setAddTaskFormValues(objData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void save_add_edit_task_data() {
        String tv_task_id_val = tv_task_id.getText().toString();
        String et_task_val = et_task.getText().toString();
        String spinner_assign_to_val = spinner_assign_to.getSelectedItem().toString();
        int assigned_to_id = getAssignUserId(arrUsers, spinner_assign_to_val);
        String tv_assign_date_val = tv_assign_date.getText().toString();
        Boolean cb_is_completed_val = cb_is_completed.isChecked();
        JSONObject params = new JSONObject();
        try {
//            params.put("user_id", SessionManager.getUserId(context));
//            params.put("id", tv_task_id_val);
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
            params.put("task_name", et_task_val);
            params.put("assigned_to", String.valueOf(assigned_to_id));
            params.put("task_date", tv_assign_date_val);
            params.put("is_completed", Boolean.valueOf(cb_is_completed_val));
            params.put("created_by", SessionManager.getUserId(context));
            params.put("updated_by", SessionManager.getUserId(context));
            params.put("updated_at", formattedDate);
            params.put("is_synced", false);
            JSONArray arrTasks = new JSONArray();
            arrTasks.put(params);
            if (arrTasks.length() > 0) {
                JSONArray rs = dbm.selectResultFromDB("select * from tbl_quick_tasks where id=" + tv_task_id_val);
                if (rs.length() == 0) {
                    dbm.insertJSONArrayInTable(arrTasks, "tbl_quick_tasks");
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put("task_name", et_task_val);
                    cv.put("assigned_to", String.valueOf(assigned_to_id));
                    cv.put("task_date", tv_assign_date_val);
                    cv.put("is_completed", Boolean.valueOf(cb_is_completed_val));
                    cv.put("is_synced", false);
                    cv.put("updated_by", SessionManager.getUserId(context));
                    cv.put("updated_at", formattedDate);
                    dbm.updateRecord("tbl_quick_tasks", cv, "id=?", new String[]{tv_task_id_val});
                }
                try {
//                FERRPDialogs.showFERRPInfoAlert(activity, "Message", "Added Successfully");
                    Intent assignment_listIntent = new Intent(context, QuickTaskListActivity.class);
                    assignment_listIntent.putExtra("title", "Quick Tasks");
                    assignment_listIntent.putExtra("init_type", "quick_tasks");
                    assignment_listIntent.putExtra("from", activity.getIntent().getComponent().getClassName());
                    assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    activity.startActivityIfNeeded(assignment_listIntent, 0);
                } finally {
                    finish();
                }

            }
//            new CommunicationManager(this.activity).postRequest("mb_save_task/", params, this.saveTaskDataResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CMResponse saveTaskDataResponse = new CMResponse() {
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

    private List<String> getSpinnerDataFromDB() {
        List<String> spinnerArray = new ArrayList();
        spinnerArray.add("Select Any");
        try {
            JSONObject row = new JSONObject("{'id':1,'name':'M. Ali Nazir'}");
            spinnerArray.add(row.getString("id") + ":" + row.getString("name"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return spinnerArray;
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        tv_assign_date.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish(); // close this activity and return to preview activity (if there is any)
                break;
        }
        return true;
    }

    private void setAddTaskFormValues(JSONObject obj) {
        try {
            tv_task_id.setText(obj.getString("id"));
            et_task.setText(obj.getString("task_name"));
            tv_assign_date.setText(obj.getString("task_date"));
            int assigned_to = obj.getInt("assigned_to");
            JSONArray rs = dbm.selectResultFromDB("Select * from tbl_users where id=" + assigned_to);
            JSONObject user = rs.getJSONObject(0);
            String assign_to = user.getString("name");
            if (!user.isNull("designation")) {
                assign_to = assign_to + " (" + user.getString("designation") + ")";
            }
            int index = getIndex(spinner_assign_to, assign_to);
//            int spinnerPosition = spinnerArrayAdapter.getPosition(obj.getString("assign_to"));
            spinner_assign_to.setSelection(index);
            String is_completed = obj.getString("is_completed");
            if (is_completed.equalsIgnoreCase("1") || is_completed.equalsIgnoreCase("true") || is_completed.equalsIgnoreCase("t")) {
                cb_is_completed.setChecked(true);
            } else {
                cb_is_completed.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private int getIndex(Spinner spinner, String myString) {
        myString = myString.trim().toLowerCase();
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().toLowerCase().contains(myString)) {
                return i;
            }
        }

        return 0;
    }

    private int getAssignUserId(JSONArray arrUsers, String myString) {
        try {
            myString = myString.trim().toLowerCase();
            for (int i = 0; i < arrUsers.length(); i++) {
                JSONObject obj = arrUsers.getJSONObject(i);
                String user = obj.getString("name");
                if (!obj.isNull("designation")) {
                    user = user + " (" + obj.getString("designation") + ")";
                }
                if (user.toLowerCase().contains(myString)) {
                    return obj.getInt("id");
                }
            }

        } catch (Exception e) {

        }
        return 0;
    }

}
