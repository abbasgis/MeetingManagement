package info.pnddch.meetingmanagement.utilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import info.pnddch.meetingmanagement.MainActivity;
import info.pnddch.meetingmanagement.utilInterface.CMResponse;

public class SyncManager {
    Activity activity;
    Context context;
    DatabaseManager dbManager;
    UtilitiesManager utilManager;

    public SyncManager() {
        activity = MainActivity.getInstance();
        context = MainActivity.getInstance();
        utilManager = new UtilitiesManager(context, activity);
        dbManager = new DatabaseManager(context);
    }

    public void syncQuickTasks() {
        try {
//            String sql = "select * from tbl_quick_tasks where is_synced is null or is_synced='' or is_synced = 'f' or is_synced = 'false' or is_synced =0";
            String sql = "select t1.*,t2.id_server as assigned_to_id_server from tbl_quick_tasks t1 INNER JOIN tbl_users t2 on t1.assigned_to = t2.id\n" +
                    "where t1.is_synced is null or t1.is_synced='' or t1.is_synced = 'f' or t1.is_synced = 'false' or t1.is_synced =0";
            JSONArray rs = dbManager.selectResultFromDB(sql);
            List<String> serverIdInClient = dbManager.selectResultAsListFromDB("tbl_quick_tasks", "id_server");
//            if (rs.length() > 0) {
            JSONObject params = new JSONObject();
            params.put("client_updates", rs.toString());
            params.put("server_ids_in_client", serverIdInClient.toString());
            params.put("user_id", SessionManager.getUserId(context));
            String sql_update_at_user = "select max(updated_at) updated_at from tbl_users";
            String sql_update_at_tasks = "select max(updated_at) updated_at from tbl_quick_tasks";
            SessionManager.setTaskSyncDate(context, "");
            JSONArray tasks = dbManager.selectResultFromDB(sql_update_at_tasks);
            if (tasks.length() > 0) {
                String task_updated_at = tasks.getJSONObject(0).getString("updated_at");
                SessionManager.setTaskSyncDate(context, task_updated_at);
                params.put("sync_date_tasks", task_updated_at);
            }
            SessionManager.setUsersSyncDate(context, "");
            JSONArray users = dbManager.selectResultFromDB(sql_update_at_user);
            if (users.length() > 0) {
                String updated_at = users.getJSONObject(0).getString("updated_at");
                SessionManager.setUsersSyncDate(context, updated_at);
                params.put("sync_date_users", updated_at);
            }
            new CommunicationManager(activity).postRequestScheduleWithoutDialogue("mb_sync_quick_task/", params, syncQuickTaskResponse);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    CMResponse syncQuickTaskResponse = new CMResponse() {
        @Override
        public void consumeResponse(String response, boolean z) throws JSONException {
            if (!response.equalsIgnoreCase("404")) {
                JSONObject objRes = new JSONObject(response);
                JSONArray arrTasksId = new JSONArray(objRes.getString("tasks_client"));
                JSONArray tasks_server = new JSONArray(objRes.getString("tasks_server"));
                JSONArray users_server = new JSONArray(objRes.getString("users_server"));
                if (users_server.length() > 0) {
                    add_users_in_local_db(users_server);
                }
                JSONArray tasks_server_deleted = new JSONArray(objRes.getString("tasks_server_deleted"));
                if (tasks_server_deleted.length() > 0) {
                    delete_tasks_in_local_db(tasks_server);
                }
                for (int i = 0; i < arrTasksId.length(); i++) {
                    JSONObject obj = arrTasksId.getJSONObject(i);
                    ContentValues cv = new ContentValues();
                    cv.put("id_server", obj.getString("id_server"));
                    cv.put("is_synced", true);
                    dbManager.updateRecord("tbl_quick_tasks", cv, "id=?", new String[]{obj.getString("id_client")});
                }
                if (tasks_server.length() > 0) {
                    add_tasks_in_local_db(tasks_server);
                }
            }
        }
    };

    private void add_users_in_local_db(JSONArray users_server) {
        try {
            JSONArray arrToAdded = new JSONArray();
            String formattedDate = utilManager.getCurrentDateTime();
            for (int k = 0; k < users_server.length(); k++) {
                JSONObject o = users_server.getJSONObject(k);
                JSONArray rs = dbManager.selectResultFromDB("select * from tbl_users where id_server=" + o.getInt("id"));
                if (rs.length() > 0) {
                    JSONObject task = rs.getJSONObject(0);
                    ContentValues cv = new ContentValues();
                    cv.put("name", o.getString("name"));
                    cv.put("designation", String.valueOf(o.getString("designation")));
                    cv.put("email_id", o.getString("email_id"));
                    cv.put("contact_no", o.getString("contact_no"));
//                    cv.put("id_server", task.getString("id_server"));
                    cv.put("updated_at", o.getString("updated_at"));
                    dbManager.updateRecord("tbl_users", cv, "id=?", new String[]{task.getString("id")});
                } else {
                    o.put("id_server", o.getInt("id"));
                    o.remove("id");
                    arrToAdded.put(o);
                }

            }
            dbManager.insertJSONArrayInTable(arrToAdded, "tbl_users");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void add_tasks_in_local_db(JSONArray tasks_server) {
        try {
            JSONArray arrToAdded = new JSONArray();
            String formattedDate = utilManager.getCurrentDateTime();
            for (int k = 0; k < tasks_server.length(); k++) {
                JSONObject o = tasks_server.getJSONObject(k);
                String assigned_to = String.valueOf(o.getString("assigned_to"));
                JSONArray users = dbManager.selectResultFromDB("select * from tbl_users where id_server=" + assigned_to);
                JSONObject user = users.getJSONObject(0);
                JSONArray rs = dbManager.selectResultFromDB("select * from tbl_quick_tasks where id_server=" + o.getInt("id"));
                if (rs.length() > 0) {
                    JSONObject task = rs.getJSONObject(0);
                    ContentValues cv = new ContentValues();
                    cv.put("task_name", o.getString("task_name"));
                    cv.put("assigned_to", String.valueOf(user.getString("id")));
                    cv.put("task_date", o.getString("task_date"));
                    cv.put("is_completed", Boolean.valueOf(o.getString("is_completed")));
                    cv.put("is_synced", true);
                    cv.put("updated_by", o.getString("updated_by"));
                    cv.put("updated_at", o.getString("updated_at"));
                    dbManager.updateRecord("tbl_quick_tasks", cv, "id=?", new String[]{task.getString("id")});
                } else {
                    o.remove("id");
                    o.remove("assigned_to_id");
                    o.remove("assigned_to");
                    o.put("assigned_to", user.getString("id"));
                    arrToAdded.put(o);
                }

            }
            dbManager.insertJSONArrayInTable(arrToAdded, "tbl_quick_tasks");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delete_tasks_in_local_db(JSONArray tasks_server) {

        try {
            for (int k = 0; k < tasks_server.length(); k++) {
                JSONObject obj = tasks_server.getJSONObject(k);
                dbManager.deleteRecord("tbl_quick_tasks", "id_server=?", new String[]{obj.getString("id")});
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
}
