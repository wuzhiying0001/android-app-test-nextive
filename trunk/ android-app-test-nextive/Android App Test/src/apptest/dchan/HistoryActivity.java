package apptest.dchan;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	private View mClickedOnRow;
	private TableLayout mTable;
	private final int DELETE_ACTION = 1;
	private final int MODIFY_ACTION = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		mTable = (TableLayout) findViewById(R.id.historyTable);
	}

	/**
	 * Removes everything from the table and then adds it back incase the user added more log entries in another activity.
	 */
	@Override
	public void onResume() {
		super.onResume();
		mTable.removeAllViews();
		populateTable();
	}

	/**
	 * Gets all the weight time entries and then adds it to the table.  Each row is a history_row view.
	 * Each row's id is the same is the database's row id for that entry.
	 */
	private void populateTable() {
		LinkedList<WeightTime> allEntries = DBHelper.getWeightTime(this, null, null);
		boolean kg = Preferences.getUnit(this).equals(WeightTime.Unit.KILOGRAM);
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
		for (WeightTime aRow : allEntries) {
			LayoutInflater inflater = getLayoutInflater();
			View myView = inflater.inflate(R.layout.history_row, null);
			myView.setId(aRow.getRowID());
			TextView date = (TextView) myView.findViewById(R.id.date);
			TextView weight = (TextView) myView.findViewById(R.id.weight);
			date.setText(formatter.format(aRow.getDate().getTime()));
			if (kg) {
				weight.setText(aRow.getWeightKGString() + "kgs");
			} else {
				weight.setText(aRow.getWeightLBString() + "lbs");
			}
			registerForContextMenu(myView);
			mTable.addView(myView);
		}
	}

	/**
	 * The popup context menu.  Has two options, modify or delete the record.  The title of the menu is the date and weight of the row entry.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		RelativeLayout rl = (RelativeLayout) v;
		TextView tv1 = (TextView) rl.findViewById(R.id.date);
		TextView tv2 = (TextView) rl.findViewById(R.id.weight);
		menu.setHeaderTitle(tv1.getText() + " " + tv2.getText());
		menu.add(0, MODIFY_ACTION, 0, "Modify");
		menu.add(0, DELETE_ACTION, 1, "Delete");
		mClickedOnRow = v;
	}

	/**
	 * If the selected action from the context menu is modify then the ModifyRecordActivity is started.  
	 * The row id of the selected row is passed.  If delete is pressed, it will delete the entry from the database then remove that row's view.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == MODIFY_ACTION) {
			Intent intent = new Intent(getBaseContext(), ModifyRecordActivity.class);
			intent.putExtra(DBHelper.UID, mClickedOnRow.getId());
			startActivity(intent);
			return true;
		} else if (item.getItemId() == DELETE_ACTION) {
			DBHelper.deleteRow(this, mClickedOnRow.getId());
			mTable.removeView(mClickedOnRow);
			return true;
		}

		return false;
	}
}
