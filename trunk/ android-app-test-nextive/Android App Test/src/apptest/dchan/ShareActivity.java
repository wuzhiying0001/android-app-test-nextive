package apptest.dchan;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ToggleButton;

public class ShareActivity extends Activity implements OnClickListener {
	private Button mShareButton;
	private Button mCreateContactButton;
	private Button mFirstDate;
	private Button mLastDate;
	private ToggleButton mShareMyself;
	private ToggleButton mShareContact;
	private ToggleButton mShareFirst;
	private ToggleButton mShareLast;
	private GregorianCalendar mBeginningDate;
	private GregorianCalendar mEndingDate;
	private SimpleDateFormat formatter;
	private DatePickerDialog.OnDateSetListener beginngDateListener;
	private DatePickerDialog.OnDateSetListener enddingDateListener;

	static final int BEGINNING_DIALOG_ID = 0;
	static final int ENDING_DIALOG_ID = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);

		mShareButton = (Button) findViewById(R.id.shareButton);
		mCreateContactButton = (Button) findViewById(R.id.createNewContact);
		mFirstDate = (Button) findViewById(R.id.firstDateButton);
		mLastDate = (Button) findViewById(R.id.lastDateButton);

		mShareMyself = (ToggleButton) findViewById(R.id.shareMyselfToggleButton);
		mShareContact = (ToggleButton) findViewById(R.id.shareContactToggleButton);
		mShareFirst = (ToggleButton) findViewById(R.id.firstDateToggleButton);
		mShareLast = (ToggleButton) findViewById(R.id.lastDateToggleButton);

		formatter = new SimpleDateFormat("MMMMM d, yyyy");


		mShareButton.setOnClickListener(this);
		mFirstDate.setOnClickListener(this);
		mLastDate.setOnClickListener(this);
		mCreateContactButton.setOnClickListener(this);
		
		// Hides or shows the beginning date selection button
		mShareFirst.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mFirstDate.getVisibility() == Button.VISIBLE)
					mFirstDate.setVisibility(Button.INVISIBLE);
				else
					mFirstDate.setVisibility(Button.VISIBLE);
			}
		});

		// Hides or shows the ending date selection button
		mShareLast.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (mLastDate.getVisibility() == Button.VISIBLE)
					mLastDate.setVisibility(Button.INVISIBLE);
				else
					mLastDate.setVisibility(Button.VISIBLE);
			}
		});

		mBeginningDate = DBHelper.getFirstEntryDate(this);
		if (mBeginningDate == null) {
			mBeginningDate = new GregorianCalendar(2000, 1, 1);
		}
		mEndingDate = new GregorianCalendar();

		// changes the beginning date based on what the user has picked
		beginngDateListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mBeginningDate.set(year, monthOfYear, dayOfMonth);
				updateBeginningDate();
			}
		};

		// changes the ending date based on what the user has picked
		enddingDateListener = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mEndingDate.set(year, monthOfYear, dayOfMonth);
				updateEndingDate();
			}
		};

		updateBeginningDate();
		updateEndingDate();
	}

	/**
	 * See which date picker button was pressed to show a different date.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case BEGINNING_DIALOG_ID:
			Dialog dialog = new DatePickerDialog(this, beginngDateListener,
					mBeginningDate.get(GregorianCalendar.YEAR),
					mBeginningDate.get(GregorianCalendar.MONTH),
					mBeginningDate.get(GregorianCalendar.DAY_OF_MONTH));
			dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog;

		case ENDING_DIALOG_ID:
			Dialog dialog1 = new DatePickerDialog(this, enddingDateListener,
					mEndingDate.get(GregorianCalendar.YEAR),
					mEndingDate.get(GregorianCalendar.MONTH),
					mEndingDate.get(GregorianCalendar.DAY_OF_MONTH));
			dialog1.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog1;
		}
		return null;
	}

	private void updateBeginningDate() {
		mFirstDate.setText(formatter.format(mBeginningDate.getTime()));
	}

	private void updateEndingDate() {
		mLastDate.setText(formatter.format(mEndingDate.getTime()));
	}

	/**
	 * Creates the email's message body.
	 * @param startDate the date of the earliest entry to get
	 * @param endDate the date of the latest entry to get
	 * @return
	 */
	private String createMessage(GregorianCalendar startDate, GregorianCalendar endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy hh:mm a");
		WeightTime.Unit unit = Preferences.getUnit(this);
		LinkedList<WeightTime> info = DBHelper.getWeightTime(this, startDate, endDate);
		StringBuilder message = new StringBuilder();
		Iterator<WeightTime> iter = info.iterator();
		while (iter.hasNext()) {
			WeightTime item = iter.next();
			message.append(formatter.format(item.getDate().getTime()));
			if (unit.equals(WeightTime.Unit.KILOGRAM)) {
				message.append("	" + item.getWeightKGString() + getString(R.string.kilos));
			} else {
				message.append("	" + item.getWeightLBString() + getString(R.string.pounds));
			}
			message.append("\r\n");
		}
		return message.toString();
	}

	private void sendEmail(String[] emailAddresses, String message) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
		emailIntent
				.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.emailSubject));
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

		this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mShareButton)) {
			LinkedList<String> emailList = new LinkedList<String>();
			if (mShareContact.isChecked()) {
				//add all the default recipient's email addresses
				emailList.addAll(DBHelper.getAllEmails(this));
			}
			if (mShareMyself.isChecked()) {
				//add the user's email address
				emailList.add(Preferences.getEmail(this));
			}
			GregorianCalendar date1 = null;
			GregorianCalendar date2 = null;
			if (mShareFirst.isChecked()) {
				date1 = mBeginningDate;
			}
			if (mShareLast.isChecked()) {
				date2 = mEndingDate;
				//adding 1 day here so if the user puts in two exact same dates they still get the values for that day
				date2.add(GregorianCalendar.DAY_OF_MONTH, 1);
			}
			//change the linked list of email address strings to an array of strings
			String[] finalEmailList = emailList.toArray(new String[emailList.size()]);

			sendEmail(finalEmailList, createMessage(date1, date2));

		} else if (v.equals(mCreateContactButton)) {
			Intent intent = new Intent(this, ManageContactActivity.class);
			startActivity(intent);
		} else if (v.equals(mFirstDate)) {
			showDialog(BEGINNING_DIALOG_ID);
		} else if (v.equals(mLastDate)) {
			showDialog(ENDING_DIALOG_ID);
		}
	}
}
