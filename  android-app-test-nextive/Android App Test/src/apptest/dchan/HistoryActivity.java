package apptest.dchan;

import java.awt.font.TextAttribute;
import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistoryActivity  extends Activity implements OnLongClickListener{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        populateTable();
    }
	private void populateTable()
	{
		TableLayout table=(TableLayout)findViewById(R.id.histroy_table);
		LinkedList<WeightTime> allEntries=DBHelper.getWeightTime(this, null, null);
		boolean kg=Preferences.getUnit(this).equals(WeightTime.KILOGRAM);
		TableLayout.LayoutParams lp=new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 10, 0, 0);
		for(WeightTime aRow:allEntries)
		{
        	TableRow tr=new TableRow(this);
        	tr.setLayoutParams(lp);
        	tr.setId(aRow.getRowID());
        	TextView date=new TextView(this);
        	date.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        	date.setGravity(Gravity.LEFT);
        	date.setText(aRow.getDate().toLocaleString());
        	
        	TextView weight=new TextView(this);
        	weight.setTextAppearance(this, android.R.style.TextAppearance_Medium);
        	weight.setGravity(Gravity.RIGHT);
        	if(kg)
        	{
        		weight.setText(aRow.getWeightKGString()+"kgs");
        	}
        	else
        	{
        		weight.setText(aRow.getWeightLBString()+"lbs");
        	}
        	tr.setOnLongClickListener(this);
        	tr.addView(date);
        	tr.addView(weight);
        	table.addView(tr);
		}
	}
	@Override
	public boolean onLongClick(View v) {
		v.getId();
		return false;
	}
}
