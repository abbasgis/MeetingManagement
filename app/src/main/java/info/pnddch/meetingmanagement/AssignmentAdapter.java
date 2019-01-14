package info.pnddch.meetingmanagement;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



class AssignmentAdapter extends ArrayAdapter<Assignment> {
    private Context mContext;
    private List<Assignment> assignmentsList = new ArrayList<>();
    private Filter filter;
    private String activity_name;

    AssignmentAdapter(Context context, ArrayList<Assignment> list, String activity_name) {
        super(context, 0, list);
        this.mContext = context;
        this.assignmentsList = list;
        this.activity_name = activity_name;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        Assignment currentAssignment = assignmentsList.get(position);
        if (activity_name != null && activity_name.equalsIgnoreCase("QuickTaskListActivity")) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.list_quick_task, parent, false);
            TextView assign_to = (TextView) listItem.findViewById(R.id.assign_to);
            assign_to.setText(currentAssignment.getAssignTo());
            TextView assign_date = (TextView) listItem.findViewById(R.id.assign_date);
            assign_date.setText(currentAssignment.getAssignDate());
        }


        TextView id = (TextView) listItem.findViewById(R.id.assignment_id);
        String strId = Integer.toString(currentAssignment.getAssignmentId());
        id.setText(strId);

        TextView name = (TextView) listItem.findViewById(R.id.assignment_name);
        name.setText(currentAssignment.getName());

        TextView duedate = (TextView) listItem.findViewById(R.id.assignment_duedate);
        duedate.setText(currentAssignment.getDueDate());
        Boolean is_completed = currentAssignment.isIs_completed();
        if (is_completed) {
            TextView id_tv_is_completed = (TextView) listItem.findViewById(R.id.id_tv_is_completed);
            id_tv_is_completed.setText("Completed: Yes");
            id_tv_is_completed.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
        }


        return listItem;
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter<Assignment>(assignmentsList);
        return filter;
    }

    private class AppFilter<T> extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            sourceObjects = new ArrayList<T>();
            synchronized (this) {
                sourceObjects.addAll(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            String filterSeq = chars.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (filterSeq != null && filterSeq.length() > 0) {
                ArrayList<T> filter = new ArrayList<T>();

                for (T object : sourceObjects) {
                    String name = ((Assignment) object).getName();
                    // the filtering itself:
                    if (name.toLowerCase().contains(filterSeq))
                        filter.add(object);
                }
                result.count = filter.size();
                result.values = filter;
            } else {
                // add all objects
                synchronized (this) {
                    result.values = sourceObjects;
                    result.count = sourceObjects.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            // NOTE: this function is *always* called from the UI thread.
            ArrayList<T> filtered = (ArrayList<T>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filtered.size(); i < l; i++)
                add((Assignment) filtered.get(i));
            notifyDataSetInvalidated();
        }
    }

}
