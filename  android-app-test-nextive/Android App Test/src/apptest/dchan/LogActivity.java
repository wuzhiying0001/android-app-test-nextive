package apptest.dchan;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LogActivity  extends Activity implements OnClickListener{
    private Button mDateDisplay;
    private Button mSave;
    private SeekBar mWeightSeekbar;
    private EditText mWeightText;
    private int mYear;
    private int mMonth;
    private int mDay;
        
    private DatePickerDialog.OnDateSetListener mDateSetListener;
        
    static final int DATE_DIALOG_ID = 0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log);
        mDateDisplay = (Button) findViewById(R.id.dateDisplay);
        mSave = (Button) findViewById(R.id.saveButton);
        mWeightText=(EditText)findViewById(R.id.weightEditText);
        mWeightSeekbar=(SeekBar)findViewById(R.id.weightSeekBar);
        mWeightSeekbar.setMax(4000);
        mWeightSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				mWeightText.setText(progress/10.0+"");
			}
		});
        
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
        
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };
    }
	 private void updateDisplay() {
	        mDateDisplay.setText(
	            new StringBuilder()
	                    // Month is 0 based so add 1
	                    .append(mMonth + 1).append("-")
	                    .append(mDay).append("-")
	                    .append(mYear).append(" "));
	    }
	 @Override
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
		try
		{
			double weight=Double.parseDouble(mWeightText.getText().toString());
			Date d=new Date(mYear-1900, mMonth, mDay);
			WeightTime wt=new WeightTime(d, weight, Preferences.getUnit(this));
			DBHelper.insertTimeWeight(this, wt);
			
			Preferences.setLastWeight(this, wt.getWeightKG());
		}
		catch(NumberFormatException e)
		{
			createError(R.string.weight_number);
		}
	}
	private void createError(int resourceID)
	{
		AlertDialog.Builder errorMessage=new AlertDialog.Builder(this);
		errorMessage.setTitle(R.string.error);
		errorMessage.setMessage(resourceID);
		errorMessage.setPositiveButton(R.string.ok, null);
		errorMessage.show();
	}
	@Override
	public void onResume()
	{
		super.onResume();
		double lastWeight=Preferences.getLastWeight(this);
		if(Preferences.getUnit(this).equals(WeightTime.POUND))
		{
			lastWeight=WeightTime.kgsToLbs(lastWeight);
		}
		DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
		mWeightText.setText(maxDigitsFormatter.format((lastWeight)));
		mWeightSeekbar.setProgress((int) (lastWeight*10));
	}
}
