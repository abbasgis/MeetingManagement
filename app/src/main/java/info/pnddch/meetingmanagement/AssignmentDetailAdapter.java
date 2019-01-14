package info.pnddch.meetingmanagement;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class AssignmentDetailAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemVal;
    private final String[] itemname;


    public AssignmentDetailAdapter(Activity context, String[] colName, String[] colVal) {
        super(context, R.layout.list_assignment_detail, colName);
        this.context = context;
        this.itemname = colName;
        this.itemVal = colVal;
        View rowView = this.context.getLayoutInflater().inflate(R.layout.list_assignment_detail, null, true);

    }

    public View getView(int position, View view, ViewGroup parent) {
        View rowView = this.context.getLayoutInflater().inflate(R.layout.list_assignment_detail, null, true);
        TextView tvColumnName = (TextView) rowView.findViewById(R.id.item);
        TextView tvColumnValue = (TextView) rowView.findViewById(R.id.tvColumnValue);
        EditText etColumnValue = (EditText) rowView.findViewById(R.id.etColumnValue);
        tvColumnName.setText(this.itemname[position]);
        tvColumnValue.setText(this.itemVal[position]);
        etColumnValue.setText(this.itemVal[position]);
        return rowView;
    }


}
