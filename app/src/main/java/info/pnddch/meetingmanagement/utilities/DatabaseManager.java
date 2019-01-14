package info.pnddch.meetingmanagement.utilities;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseManager {
    Context context;
    SQLiteDatabase db = null;

    public DatabaseManager(Context c) {

        context = c;
    }


    public SQLiteDatabase establishSQLiteConnection() {
        try {
            String dirPath = context.getExternalFilesDir(null).getAbsolutePath();
            if (dirPath != null) {
                db = SQLiteDatabase.openOrCreateDatabase(new File(dirPath + "/mm/db/mm.db"), null);
            }
            return db;
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public Cursor fetchDBChildren(String query) {
        return db.rawQuery(query, null);
    }

    public JSONArray selectResultFromDB(String query) throws JSONException {
        JSONArray arrJson = new JSONArray();
        SQLiteDatabase db = establishSQLiteConnection();
        try {
            Cursor c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                JSONObject obj = new JSONObject();
                String[] columnNames = c.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    String val = c.getString(c.getColumnIndex(columnNames[i]));
                    if (val == null) {
                        val = "";
                    }
                    obj.put(columnNames[i], val);
                }
                arrJson.put(obj);
            }
            return arrJson;
        } finally {
            db.close();
        }
    }

    public List<String> selectResultAsListFromDB(String tableName, String colName) throws JSONException {
        String query = "Select " + colName + " from " + tableName;
        List<String> arlRs = new ArrayList<>();
        SQLiteDatabase db = establishSQLiteConnection();
        try {
            Cursor c = db.rawQuery(query, null);
            while (c.moveToNext()) {
                String val = c.getString(c.getColumnIndex(colName));
//                if (val == null) {
//                    val = -1;
//                }
                arlRs.add(val);
            }
            return arlRs;
        } finally {
            db.close();
        }
    }

    public void insertJSONArrayInTable(JSONArray arrJson, String tableName) throws JSONException {
        SQLiteDatabase db = establishSQLiteConnection();
        int i = 0;
        while (i < arrJson.length()) {
            try {
                db.beginTransaction();
                JSONObject row = new JSONObject(arrJson.getString(i));
                ContentValues cv = new ContentValues();
                Iterator<String> keys = row.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    cv.put(key, row.getString(key));
                }
                db.insert(tableName, null, cv);
                db.setTransactionSuccessful();
                db.endTransaction();
                i++;
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
//                db.endTransaction();
//                db.close();
            }
        }
        db.close();
    }

    public void updateRecord(String tableName, ContentValues cv, String whereColumns, String[] whereValues) {
        SQLiteDatabase db = establishSQLiteConnection();
        try {
            db.update(tableName, cv, whereColumns, whereValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteRecord(String tableName, String whereColumns, String[] whereValues) {
        SQLiteDatabase db = establishSQLiteConnection();
        try {
            db.delete(tableName, whereColumns, whereValues);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
