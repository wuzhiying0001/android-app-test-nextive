package apptest.dchan;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.TimePicker;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class LogActivity extends Activity implements OnClickListener
{
	protected Button mDateDisplay;
	protected Button mTimeDisplay;
	protected Button mSave;
	protected SeekBar mWeightSeekbar;
	protected EditText mWeightText;
	protected GregorianCalendar mDate;

	private DatePickerDialog.OnDateSetListener mDateSetListener;
	private TimePickerDialog.OnTimeSetListener mTimeSetListener;

	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);
		mDateDisplay = (Button) findViewById(R.id.dateDisplay);
		mTimeDisplay = (Button) findViewById(R.id.timeDisplay);
		mSave = (Button) findViewById(R.id.saveButton);
		mWeightText = (EditText) findViewById(R.id.weightEditText);
		mWeightSeekbar = (SeekBar) findViewById(R.id.weightSeekBar);

		if (Preferences.getUnit(this).equals(WeightTime.POUND))
		{
			mWeightSeekbar.setMax(4000);
		}
		else
		{
			mWeightSeekbar.setMax(1800);
		}

		mWeightSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
				mWeightText.setText(progress / 10.0 + "");
			}
		});

		mSave.setOnClickListener(this);

		// add a click listener to the button
		mDateDisplay.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
		mTimeDisplay.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				showDialog(TIME_DIALOG_ID);
			}
		});
		// get the current date
		// final Calendar c = Calendar.getInstance();
		mDate = new GregorianCalendar();
		mDate.setGregorianChange(new Date());
		// display the current date (this method is below)
		updateDisplay();

		mDateSetListener=new DatePickerDialog.OnDateSetListener()
		{
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				mDate.set(year, monthOfYear, dayOfMonth);
				updateDisplay();
			}
		};
		mTimeSetListener=new TimePickerDialog.OnTimeSetListener()
		{

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				// TODO Auto-generated method stub
				mDate.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
				mDate.set(GregorianCalendar.MINUTE, minute);
				updateDisplay();
			}
		};
	}

	protected void updateDisplay()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
		mDateDisplay.setText(formatter.format(mDate.getTime()));
		formatter.applyPattern("HH:mm");
		mTimeDisplay.setText(formatter.format(mDate.getTime()));
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case DATE_DIALOG_ID:
			Dialog dialog = new DatePickerDialog(this, mDateSetListener, mDate.get(GregorianCalendar.YEAR), mDate.get(GregorianCalendar.MONTH), mDate.get(GregorianCalendar.DAY_OF_MONTH));
			dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog;

		case TIME_DIALOG_ID:
			Dialog dialog1 = new TimePickerDialog(this, mTimeSetListener, mDate.get(GregorianCalendar.HOUR_OF_DAY), mDate.get(GregorianCalendar.MINUTE), true);
			dialog1.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog1;
		}
		return null;
	}

	@Override
	public void onClick(View v)
	{
		if (v.equals(mSave))
		{
			try
			{
				float weight = Float.parseFloat(mWeightText.getText().toString());

				WeightTime wt = new WeightTime(mDate, weight, Preferences.getUnit(this));
				DBHelper.insertRow(this, wt);

				Preferences.setLastWeight(this, wt.getWeightKG());
			}
			catch (NumberFormatException e)
			{
				createError(R.string.weight_number);
			}
		}

	}

	protected void createError(int resourceID)
	{
		AlertDialog.Builder errorMessage = new AlertDialog.Builder(this);
		errorMessage.setTitle(R.string.error);
		errorMessage.setMessage(resourceID);
		errorMessage.setPositiveButton(R.string.ok, null);
		errorMessage.show();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		fillInPrevious();

	}

	protected void fillInPrevious()
	{
		float lastWeight = Preferences.getLastWeight(this);
		if (Preferences.getUnit(this).equals(WeightTime.POUND))
		{
			lastWeight = WeightTime.kgsToLbs(lastWeight);
		}
		DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
		mWeightText.setText(maxDigitsFormatter.format((lastWeight)));
		mWeightSeekbar.setProgress((int) (lastWeight * 10));
	}
}
