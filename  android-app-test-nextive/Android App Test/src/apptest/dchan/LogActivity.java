package apptest.dchan;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

public class LogActivity  extends Activity implements OnClickListener{
    private Button mDateDisplay;
    private Button mSave;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    int asdf;
    
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };
    static final int DATE_DIALOG_ID = 0;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        mDateDisplay = (Button) findViewById(R.id.dateDisplay);
        mSave = (Button) findViewById(R.id.saveButton);
        
        asdf=1;
        mSave.setOnClickListener(this);
        
        // add a click listener to the button
        mDateDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date (this method is below)
        updateDisplay();
    }
	 private void updateDisplay() {
	        mDateDisplay.setText(
	            new StringBuilder()
	                    // Month is 0 based so add 1
	                    .append(mMonth + 1).append("-")
	                    .append(mDay).append("-")
	                    .append(mYear).append(" "));
	    }
	 protected Dialog onCreateDialog(int id) {
		    switch (id) {
		    case DATE_DIALOG_ID:
		    	Dialog  dialog=new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		    	dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
		        return dialog;
		    }
		    return null;
		}
	@Override
	public void onClick(View v) {
//		LinkedList<WeightTime> aa=DBHelper.getWeightTime(this, null, null);
//		for(WeightTime abc:aa)
//		{
//			Log.i(abc.getRowID()+"", abc.getWeightKGS()+" "+abc.getDate());
//		}
		DBHelper.insertTimeWeight(this, new WeightTime(new Date(), asdf, WeightTime.KILOGRAM));
		
        asdf++;
	}
}
