package apptest.dchan;

import java.util.LinkedList;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HistoryActivity extends Activity
{
	private View clickedOnView;
	private TableLayout table;
	private final int DELETE_ACTION=1;
	private final int MODIFY_ACTION=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		table = (TableLayout) findViewById(R.id.histroy_table);
	}

	@Override
	public void onResume()
	{
		super.onResume();
		table.removeAllViews();
		populateTable();
	}

	private void populateTable()
	{
		LinkedList<WeightTime> allEntries = DBHelper.getWeightTime(this, null, null);
		boolean kg = Preferences.getUnit(this).equals(WeightTime.KILOGRAM);
		TableLayout.LayoutParams lp = new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 10, 0, 0);
		for (WeightTime aRow : allEntries)
		{
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(lp);
			tr.setId(aRow.getRowID());
			TextView date = new TextView(this);
			date.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			date.setGravity(Gravity.LEFT);
			date.setText(aRow.getDate().toLocaleString());

			TextView weight = new TextView(this);
			weight.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			weight.setGravity(Gravity.RIGHT);
			if (kg)
			{
				weight.setText(aRow.getWeightKGString() + "kgs");
			}
			else
			{
				weight.setText(aRow.getWeightLBString() + "lbs");
			}
			registerForContextMenu(tr);
			// tr.setOnLongClickListener(this);
			tr.addView(date);
			tr.addView(weight);
			table.addView(tr);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		// menu.setHeaderTitle("Sample menu");
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MODIFY_ACTION, 0, "Modify");
		menu.add(0, DELETE_ACTION, 1, "Delete");
		clickedOnView = v;
	}

	@Override
	 public boolean onContextItemSelected(MenuItem item) 
	{
		if(item.getItemId()==MODIFY_ACTION)
		{
			return true;
		}
		else if(item.getItemId()==DELETE_ACTION)
		{
			DBHelper.deleteRow(this, clickedOnView.getId());
			table.removeView(clickedOnView);
			return true;
		}
		
	    return false;
	 }
}
