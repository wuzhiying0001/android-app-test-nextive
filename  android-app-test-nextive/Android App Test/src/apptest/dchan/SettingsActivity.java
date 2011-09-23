package apptest.dchan;

import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity implements OnClickListener, OnCheckedChangeListener
{
	final private int PICK_CONTACT_REQUEST = 1;
	final private int CREATE_CONTACT_REQUEST = 2;

	Button saveButton;
	Button createContactButton;
	EditText nameEditText;
	EditText emailEditText;
	RadioButton kilograms;
	RadioButton pounds;
	TextView defaultEmail;
	ToggleButton myself;
	ToggleButton contact;
	String[] emailOptions;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		saveButton=(Button)findViewById(R.id.saveButton);
		createContactButton=(Button)findViewById(R.id.createNewContact);
		myself=(ToggleButton)findViewById(R.id.myselfDefaultContact);
		contact=(ToggleButton)findViewById(R.id.contactDefaultContact);
		nameEditText=(EditText)findViewById(R.id.nameEditText);
		emailEditText=(EditText)findViewById(R.id.emailEditText);
		kilograms=(RadioButton)findViewById(R.id.optionsKilos);
		pounds=(RadioButton)findViewById(R.id.optionsPounds);
		defaultEmail=(TextView)findViewById(R.id.defaultRecipientEmail);
		
		myself.setOnCheckedChangeListener(this);
		contact.setOnCheckedChangeListener(this);
		saveButton.setOnClickListener(this);
		createContactButton.setOnClickListener(this);
				
		defaultEmail.setText(Preferences.getRecipientEmail(this));
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case PICK_CONTACT_REQUEST:
				emailOptions=getContactEmail(data).toArray(new String[0]);
				setMail();
				break;
			case CREATE_CONTACT_REQUEST:
				emailOptions=getContactEmail(data).toArray(new String[0]);
				setMail();
				break;
			}
		}
		else if(resultCode==RESULT_CANCELED && requestCode==PICK_CONTACT_REQUEST)
		{
			contact.setChecked(false);
		}
	}
	private void setMail()
	{
		if(emailOptions.length>1)
		{
			final Context c=this;
			AlertDialog.Builder asdf=new Builder(this);
			asdf.setItems(emailOptions, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String zxcv=emailOptions[which];
					Preferences.setRecipientEmail(c, zxcv);
					defaultEmail.setText(zxcv);
				}
				
			});
			asdf.show();
		}
		else if(emailOptions.length==1)
		{
			String zxcv=emailOptions[0];
			Preferences.setRecipientEmail(this, zxcv);
			defaultEmail.setText(zxcv);
		}
		else
		{
			createError(R.string.noEmail);
			contact.setChecked(false);
		}
	}
	private LinkedList<String> getContactEmail(Intent data)
	{
		Cursor cursor = null;
		LinkedList<String> emails=new LinkedList<String>();
		try
		{
			Uri result = data.getData();
			// get the contact id from the Uri
			String id = result.getLastPathSegment();

			// query for everything email
			cursor = getContentResolver().query(Email.CONTENT_URI,
					null, Email.CONTACT_ID + "=?", new String[] { id },
					null);

			int emailIdx = cursor.getColumnIndex(Email.DATA);

			// let's just get the first email
			while (cursor.moveToNext())
			{
				emails.add(cursor.getString(emailIdx));
			}

		}
		catch (Exception e)
		{
		}
		return emails;
	}
	@Override
	public void onClick(View arg0)
	{
		if(arg0.equals(saveButton))
		{
			if(!emailEditText.getText().toString().equals("") && !nameEditText.getText().toString().equals(""))
			{
				savePreferences();
				if(Preferences.isFirstTime(this))
				{
					Preferences.setFirstTime(this, false);
					setResult(Activity.RESULT_OK);
		            finish();
				}
			}
			else if(nameEditText.getText().toString().equals(""))
			{
				createError(R.string.email_missing);
			}
			else
			{
				createError(R.string.name_missing);
			}
		}
		else if(arg0.equals(createContactButton))
		{
			Intent intent = new Intent(this, CreateContactsActivity.class);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
		
	}
	private void savePreferences()
	{
		Preferences.setName(this, nameEditText.getText().toString());
		Preferences.setEmail(this, emailEditText.getText().toString());
		if(kilograms.isChecked())
		{
			Preferences.setUnit(this, WeightTime.KILOGRAM);
		}
		else
		{
			Preferences.setUnit(this, WeightTime.POUND);
		}
	}
	@Override
	public void onResume()
	{
		super.onResume();
		nameEditText.setText(Preferences.getName(this));
		emailEditText.setText(Preferences.getEmail(this));
		if(Preferences.getUnit(this).equals(WeightTime.KILOGRAM))
		{
			kilograms.setChecked(true);
			pounds.setChecked(false);
		}
		else
		{
			kilograms.setChecked(false);
			pounds.setChecked(true);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView.equals(contact))
		{
			if(isChecked)
			{
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setData(ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, PICK_CONTACT_REQUEST);
			}
			else
			{
				defaultEmail.setText("");
			}
		}
	}
}
