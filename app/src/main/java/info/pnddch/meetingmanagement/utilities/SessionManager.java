package info.pnddch.meetingmanagement.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SessionManager {
    private static String PREF_NAME = "prefsFERRP";
    private SharedPreferences sharedPreferences;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, 0);
    }

    public static String getUserId(Context context) {
        return getPrefs(context).getString("userId_key", "-1");
    }

    public static void setUserId(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        editor.putString("userId_key", input);
        editor.commit();
    }

    public static String getToken(Context context) {
        return getPrefs(context).getString("token_key", null);
    }

    public static void setToken(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        editor.putString("token_key", input);
        editor.commit();
    }

    public static String getAuthCode(Context context) {
        return getPrefs(context).getString("authCode", null);
    }

    public static void setAuthCode(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        editor.putString("authCode", input);
        editor.commit();
    }

    public static void setProjectsId(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        editor.putString("project_id_key", input);
        editor.commit();
    }

    public static String getProjectsId(Context context) {
        return getPrefs(context).getString("project_id_key", null);
    }

    public static void clearSessionData(Context context) {
        Editor editor = getPrefs(context).edit();
        editor.clear();
        editor.commit();
    }

    public static void setTaskSyncDate(Context context, String dateTime) {
        Editor editor = getPrefs(context).edit();
        if (dateTime == null) {
            dateTime = "";
//            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
        editor.putString("syncDate", dateTime);
        editor.commit();
    }

    public static String getTaskSyncDate(Context context) {
        return getPrefs(context).getString("syncDate", null);
    }

    public static void setUsersSyncDate(Context context, String dateTime) {
        Editor editor = getPrefs(context).edit();
        if (dateTime == null) {
            dateTime = "";
//            dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        }
        editor.putString("syncDate", dateTime);
        editor.commit();
    }

    public static String getUsersSyncDate(Context context) {
        return getPrefs(context).getString("syncDate", null);
    }

    public static void setUploadedDate(Context context) {
        Editor editor = getPrefs(context).edit();
        editor.putString("uploadDate", new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()));
        editor.commit();
    }

    public static String getUploadedDate(Context context) {
        return getPrefs(context).getString("uploadDate", null);
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString("email_id", null);
    }

    public static void setEmail(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        editor.putString("email_id", input);
        editor.commit();
    }

    public static String getOwnerName(Context context) {
        return getPrefs(context).getString("owner_name", null);
    }

    public static void setOwnerName(Context context, String input) {
        Editor editor = getPrefs(context).edit();
        input = input.replace("Name:", "");
        input = "Name:" + input;
        editor.putString("owner_name", input);
        editor.commit();
    }

    public static void setFeedbackTempImageLatLong(Context context, String image_name, String latitude, String longitude) {
        Editor editor = getPrefs(context).edit();
        String jsonText = getPrefs(context).getString("lat_long_text", null);
        try {
            JSONArray arrJson = new JSONArray();
            if (jsonText != null) {
                arrJson = new JSONArray(jsonText);
            }
            JSONObject obj = new JSONObject();
            obj.put(image_name, longitude + "," + latitude);
            arrJson.put(obj);
            editor.putString("lat_long_text", arrJson.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getFeedbackTempImageLatLong(Context context, String image_name) {
        String jsonText = getPrefs(context).getString("lat_long_text", null);
        String lat_long = null;
        if (jsonText != null) {
            try {
                JSONArray arrJson = new JSONArray(jsonText);
                for (int i = 0; i < arrJson.length(); i++) {
                    JSONObject obj = arrJson.getJSONObject(i);
                    if (obj.has(image_name)) {
                        lat_long = obj.getString(image_name);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return lat_long;
    }

    public static void setFeedbackTempImageLatLongEmpty(Context context) {
        Editor editor = getPrefs(context).edit();
        editor.putString("lat_long_text", null);
        editor.commit();
    }
}
