package info.pnddch.meetingmanagement.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.pnddch.meetingmanagement.AddEditQuickTaskActivity;
import info.pnddch.meetingmanagement.AssignmentDetailActivity;
import info.pnddch.meetingmanagement.AssignmentsListActivity;
import info.pnddch.meetingmanagement.MainActivity;
import info.pnddch.meetingmanagement.QuickTaskListActivity;
import info.pnddch.meetingmanagement.R;
import info.pnddch.meetingmanagement.URLViewerActivity;
import info.pnddch.meetingmanagement.ViewDataListActivity;


public class CustomGrid extends BaseAdapter {
    private final int[] Imageid;
    private Activity activity;
    private Context context;
    private final String[] menuNames;

    public CustomGrid(Activity act, String[] menuNameList, int[] Imageid) {
        this.context = act;
        this.activity = act;
        this.Imageid = Imageid;
        this.menuNames = menuNameList;
    }

    public int getCount() {
        return this.menuNames.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("WrongConstant") LayoutInflater inflater = (LayoutInflater) this.context.getSystemService("layout_inflater");
        if (convertView != null) {
            return convertView;
        }
        View grid = new View(this.context);
        grid = inflater.inflate(R.layout.menulist, null);
        ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
        ((TextView) grid.findViewById(R.id.grid_text)).setText(this.menuNames[position]);
        imageView.setImageResource(this.Imageid[position]);
        return grid;
    }

    @SuppressLint("WrongConstant")
    public void performMenuItemAction(Activity activity, Context context, String menuName) {
        if (menuName.equalsIgnoreCase("Quick Tasks")) {
            try {
                Intent assignment_listIntent = new Intent(context, QuickTaskListActivity.class);
                assignment_listIntent.putExtra("title", "Quick Tasks");
                assignment_listIntent.putExtra("init_type", "quick_tasks");
                assignment_listIntent.putExtra("from", activity.getIntent().getComponent().getClassName());
                assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                activity.startActivityIfNeeded(assignment_listIntent, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (menuName.equalsIgnoreCase("Important Initiatives")) {
            try {
                getInitiativeDataAndShowActivity("important", "Important Initiatives");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (menuName.equalsIgnoreCase("Short-term Initiatives")) {
            try {
                getInitiativeDataAndShowActivity("short", "Short-term Initiatives");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (menuName.equalsIgnoreCase("Long-term Initiatives")) {
            try {
                getInitiativeDataAndShowActivity("long", "Long-term Initiatives");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (menuName.equalsIgnoreCase("Calender")) {
            String url = "https://calendar.google.com/calendar/";
//            url = "http://pnddch.info/mm/";
            Intent intentURLViewerActivity = new Intent(context, URLViewerActivity.class);
            intentURLViewerActivity.putExtra("url", url);
            intentURLViewerActivity.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            activity.startActivityIfNeeded(intentURLViewerActivity, 0);
        }
        if (menuName.equalsIgnoreCase("Quick Links")) {
            try {
                JSONArray arrData = new JSONArray();
                arrData.put(new JSONObject("{'id':3,'name':'Social Call List','date':'http://pnddch.info/mm/?type=contacts'}"));
                //assignment property name used for
                JSONObject obj = new JSONObject("{'id':1,'name':'SMDP','date':'https://smdp.punjab.gov.pk/'}");
                arrData.put(obj);
                arrData.put(new JSONObject("{'id':2,'name':'ADP Analysis','date':'http://pnddch.info/adp/'}"));
                Intent intent = new Intent(context, ViewDataListActivity.class);
                intent.putExtra("strJsonDataArray", arrData.toString());
                intent.putExtra("title", "Quick Links");
                intent.putExtra("from", activity.getIntent().getComponent().getClassName());
                intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                activity.startActivityIfNeeded(intent, 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void getInitiativeDataAndShowActivity(String initiative_type, String activity_title) {
        Intent assignment_listIntent = new Intent(context, AssignmentsListActivity.class);
        assignment_listIntent.putExtra("title", activity_title);
        assignment_listIntent.putExtra("init_type", initiative_type);
        assignment_listIntent.putExtra("from", activity.getIntent().getComponent().getClassName());
        assignment_listIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        activity.startActivityIfNeeded(assignment_listIntent, 0);
    }
}
