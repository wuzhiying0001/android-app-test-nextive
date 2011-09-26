package apptest.dchan;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class LogActivity extends Activity implements OnClickListener {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.log);
		mDateDisplay = (Button) findViewById(R.id.dateDisplay);
		mTimeDisplay = (Button) findViewById(R.id.timeDisplay);
		mSave = (Button) findViewById(R.id.saveButton);
		mWeightText = (EditText) findViewById(R.id.weightEditText);
		mWeightSeekbar = (SeekBar) findViewById(R.id.weightSeekBar);

		mSave.setOnClickListener(this);

		mDate = new GregorianCalendar();

		updateDisplay();

		mWeightSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			/**
			 * Update the EditText every time the slider's value is changed.
			 */
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				mWeightText.setText(progress / 10.0 + "");
			}
		});
		
		mDateDisplay.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		mTimeDisplay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(TIME_DIALOG_ID);
			}
		});
		
		mDateSetListener = new DatePickerDialog.OnDateSetListener() {
			/**
			 * Sets the date to what the user chose.
			 */
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mDate.set(year, monthOfYear, dayOfMonth);
				updateDisplay();
			}
		};
		
		mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

			/**
			 * Sets the time to what ever the user chose.
			 */
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				mDate.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
				mDate.set(GregorianCalendar.MINUTE, minute);
				updateDisplay();
			}
		};
		
		//Animation for going to and from this activity.
		getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
	}

	/**
	 * Updates the text on the date button to the date object's time and day
	 */
	protected void updateDisplay() {
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
		mDateDisplay.setText(formatter.format(mDate.getTime()));
		formatter.applyPattern("HH:mm");
		mTimeDisplay.setText(formatter.format(mDate.getTime()));
	}

	/**
	 * Starts the time or date picker dialogs.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Dialog dialog = new DatePickerDialog(this, mDateSetListener,
					mDate.get(GregorianCalendar.YEAR), mDate.get(GregorianCalendar.MONTH),
					mDate.get(GregorianCalendar.DAY_OF_MONTH));

			dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog;

		case TIME_DIALOG_ID:
			Dialog dialog1 = new TimePickerDialog(this, mTimeSetListener,
					mDate.get(GregorianCalendar.HOUR_OF_DAY), mDate.get(GregorianCalendar.MINUTE),
					true);

			dialog1.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog1;
		}
		return null;
	}

	/**
	 * Saves the weight and time to the database.
	 */
	@Override
	public void onClick(View v) {
		if (v.equals(mSave)) {
			try {
				float weight = Float.parseFloat(mWeightText.getText().toString());
				WeightTime wt = new WeightTime(mDate, weight, Preferences.getUnit(this));
				DBHelper.insertRow(this, wt);
				Toast.makeText(this, R.string.recordSaved, Toast.LENGTH_LONG).show();
			} catch (NumberFormatException e) {
				createError(R.string.weight_number);
			}
		}

	}

	/**
	 * Creates the error messages.
	 * @param resourceID the string to display for the error message
	 */
	protected void createError(int resourceID) {
		AlertDialog.Builder errorMessage = new AlertDialog.Builder(this);
		errorMessage.setTitle(R.string.error);
		errorMessage.setMessage(resourceID);
		errorMessage.setPositiveButton(R.string.ok, null);
		errorMessage.show();
	}

	/**
	 * Sets the max number for the slider then fills in the previously filled in data.
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (Preferences.getUnit(this).equals(WeightTime.Unit.POUND)) {
			mWeightSeekbar.setMax(4000);
		} else {
			mWeightSeekbar.setMax(1800);
		}
		fillInPrevious();
	}

	/**
	 * Saves the weight information to the preferences
	 */
	@Override
	public void onPause() {
		super.onPause();
		float lastEnteredWeight = Float.parseFloat(mWeightText.getText().toString());
		if (Preferences.getUnit(this).equals(WeightTime.Unit.POUND)) {
			Preferences.setLastWeight(this, WeightTime.lbsToKgs(lastEnteredWeight));
		} else {
			Preferences.setLastWeight(this, lastEnteredWeight);
		}

	}

	/**
	 * Fills in the weight as the last value the user last put in there even if it was not saved by the user.
	 */
	protected void fillInPrevious() {
		float lastWeight = Preferences.getLastWeight(this);
		if (Preferences.getUnit(this).equals(WeightTime.Unit.POUND)) {
			lastWeight = WeightTime.kgsToLbs(lastWeight);
		}
		DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
		mWeightText.setText(maxDigitsFormatter.format((lastWeight)));
		mWeightSeekbar.setProgress((int) (lastWeight * 10));
	}
}
