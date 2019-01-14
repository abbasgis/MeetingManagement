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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;
import info.pnddch.meetingmanagement.utilities.CommunicationManager;

public class RemarksHistoryListActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    EditText inputSearch;
    private ListView lv;
    AssignmentAdapter adapter;
    private CMResponse remarksHistoryResponse = new CMResponse() {
        @Override
        public void consumeResponse(String result, boolean success) throws JSONException {
            if (success) {
                createGroupListView(new JSONArray(result));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remarks_history_list);
        Toolbar remarks_history_toolbar = (Toolbar) findViewById(R.id.remarks_history_toolbar);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btnBack);
        remarks_history_toolbar.setTitle("");
        setSupportActionBar(remarks_history_toolbar);
        this.activity = this;
        this.context = this;
        try {
            String assignment_id = "-1";
            Intent intent = getIntent();
            if (intent != null) {
                assignment_id = intent.getStringExtra("assignment_id");
            }
            JSONObject params = new JSONObject();
            params.put("assignment_id", assignment_id);
            new CommunicationManager(this.activity).postRequest("mb_get_remarks_history/", params, this.remarksHistoryResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void createGroupListView(final JSONArray data) {
        ArrayList<Assignment> assignmentList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                int assignment_id = data.getJSONObject(i).getInt("id");
                String assignment_name = data.getJSONObject(i).getString("remarks");
                String assignment_duedate = "Remarks Date: " + data.getJSONObject(i).getString("remarks_date");
                assignmentList.add(new Assignment(assignment_id, assignment_name, assignment_duedate,"","",false));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.lv = (ListView) findViewById(R.id.list_view_remarks);
        this.inputSearch = (EditText) findViewById(R.id.inputSearch);
        this.inputSearch.addTextChangedListener(filterTextWatcher);
        this.adapter = new AssignmentAdapter(this, assignmentList,"RemarksHistoryListActivity");
        this.lv.setAdapter(this.adapter);
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
}
