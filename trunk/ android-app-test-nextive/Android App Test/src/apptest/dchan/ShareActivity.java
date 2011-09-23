package apptest.dchan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ToggleButton;

public class ShareActivity extends Activity implements OnClickListener {
	Button shareButton;
	Button createContactButton;
	Button firstDate;
	Button lastDate;
	ToggleButton shareMyself;
	ToggleButton shareContact;
	ToggleButton shareFirst;
	ToggleButton shareLast;
	GregorianCalendar beginningDate;
	GregorianCalendar endingDate;
	
	private DatePickerDialog.OnDateSetListener beginngDateListener;
	private DatePickerDialog.OnDateSetListener enddingDateListener;

	static final int BEGINNING_DIALOG_ID = 0;
	static final int ENDING_DIALOG_ID = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share);

		shareButton = (Button) findViewById(R.id.shareButton);
		createContactButton = (Button) findViewById(R.id.createNewContact);
		firstDate = (Button) findViewById(R.id.firstDateButton);
		lastDate = (Button) findViewById(R.id.lastDateButton);

		shareMyself = (ToggleButton) findViewById(R.id.shareMyselfToggleButton);
		shareContact = (ToggleButton) findViewById(R.id.shareContactToggleButton);
		shareFirst = (ToggleButton) findViewById(R.id.firstDateToggleButton);
		shareLast = (ToggleButton) findViewById(R.id.lastDateToggleButton);

		shareButton.setOnClickListener(this);
		firstDate.setOnClickListener(this);
		lastDate.setOnClickListener(this);
		
		beginningDate=DBHelper.getFirstEntryDate(this);
		if(beginningDate==null)
		{
			beginningDate=new GregorianCalendar(2000, 1, 1);
		}
		endingDate=new GregorianCalendar();
		
		beginngDateListener=new DatePickerDialog.OnDateSetListener()
		{
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				beginningDate.set(year, monthOfYear, dayOfMonth);
				updateBeginningDate();
			}
		};
		
		enddingDateListener=new DatePickerDialog.OnDateSetListener()
		{
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				endingDate.set(year, monthOfYear, dayOfMonth);
				updateEndingDate();
			}
		};
		
		updateBeginningDate();
		updateEndingDate();
	}
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case BEGINNING_DIALOG_ID:
			Dialog dialog = new DatePickerDialog(this, beginngDateListener, beginningDate.get(GregorianCalendar.YEAR), beginningDate.get(GregorianCalendar.MONTH), beginningDate.get(GregorianCalendar.DAY_OF_MONTH));
			dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog;

		case ENDING_DIALOG_ID:
			Dialog dialog1 = new DatePickerDialog(this, enddingDateListener, endingDate.get(GregorianCalendar.YEAR), endingDate.get(GregorianCalendar.MONTH), endingDate.get(GregorianCalendar.DAY_OF_MONTH));
			dialog1.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
			return dialog1;
		}
		return null;
	}
	private void updateBeginningDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
		firstDate.setText(formatter.format(beginningDate.getTime()));
	}
	
	private void updateEndingDate()
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MMMMM d, yyyy");
		lastDate.setText(formatter.format(endingDate.getTime()));
	}
	
	private String createMessage(GregorianCalendar startDate, GregorianCalendar endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MMMMM d, yyyy hh:mm a");
		String unit = Preferences.getUnit(this);
		LinkedList<WeightTime> info = DBHelper.getWeightTime(this, startDate,
				endDate);
		StringBuilder message = new StringBuilder();
		Iterator<WeightTime> iter = info.iterator();
		while (iter.hasNext()) {
			WeightTime item = iter.next();
			message.append(formatter.format(item.getDate().getTime()));
			if (unit.equals(WeightTime.KILOGRAM)) {
				message.append("	" + item.getWeightKGString()
						+ getString(R.string.kilos));
			} else {
				message.append("	" + item.getWeightLBString()
						+ getString(R.string.pounds));
			}
			message.append("\r\n");
		}
		return message.toString();
	}

	private void sendEmail(String[] emailAddresses, String message) {
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

		emailIntent.setType("plain/text");

		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);

		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.emailSubject));

		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

		this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}

	@Override
	public void onClick(View v) {
		if (v.equals(shareButton)) {
			String[] s = new String[1];
			if(shareMyself.isChecked() && shareContact.isChecked())
			{
				s=new String[2];
			}
			if(shareMyself.isChecked())
			{
				s[0]=Preferences.getEmail(this);
			}
			if(shareContact.isChecked())
			{
				if(shareMyself.isChecked())
				{
					s[1] = Preferences.getRecipientEmail(this);
				}
				else
				{
					s[0]=Preferences.getRecipientEmail(this);
				}
				
			}
			if(shareFirst.isChecked() && shareLast.isChecked())
			{
				sendEmail(s, createMessage(beginningDate, endingDate));
			}
			else if(shareFirst.isChecked() && !shareLast.isChecked())
			{
				sendEmail(s, createMessage(beginningDate, null));
			}
			else if(!shareFirst.isChecked() && shareLast.isChecked())
			{
				sendEmail(s, createMessage(null, endingDate));
			}
			else
			{
				sendEmail(s, createMessage(null, null));
			}
			
		}
		else if(v.equals(firstDate))
		{
			showDialog(BEGINNING_DIALOG_ID);
		}
		else if(v.equals(lastDate))
		{
			showDialog(ENDING_DIALOG_ID);
		}
	}
}
