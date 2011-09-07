package apptest.dchan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SettingsActivity extends Activity implements OnItemSelectedListener, OnClickListener
{
	final private int PICK_CONTACT_REQUEST = 1;
	final private int CREATE_CONTACT_REQUEST = 2;

	Spinner defaultEmailSpinner;
	Button saveButton;
	EditText nameEditText;
	EditText emailEditText;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		defaultEmailSpinner = (Spinner) findViewById(R.id.defaultEmailSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.default_email_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		defaultEmailSpinner.setAdapter(adapter);
		defaultEmailSpinner.setOnItemSelectedListener(this);

		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(this);
		
		nameEditText=(EditText)findViewById(R.id.nameEditText);
		emailEditText=(EditText)findViewById(R.id.emailEditText);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		String DEBUG_TAG = "1";
		if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case PICK_CONTACT_REQUEST:
				Cursor cursor = null;
				String email = "";
				try
				{
					Uri result = data.getData();
					Log.v(DEBUG_TAG,
							"Got a contact result: " + result.toString());

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
						email = cursor.getString(emailIdx);
						Log.v(DEBUG_TAG, "Got email: " + email);
					}

				}
				catch (Exception e)
				{
					Log.e(DEBUG_TAG, "Failed to get email data", e);
				}
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		Resources res = getResources();
		String[] defaultEmailList = res.getStringArray(R.array.default_email_array);
		if (arg0.getItemAtPosition(arg2).toString().equals(defaultEmailList[1]))
		{
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
		else if (arg0.getItemAtPosition(arg2).toString().equals(defaultEmailList[2]))
		{
			Intent intent = new Intent(this, CreateContactsActivity.class);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0)
	{
	}

	@Override
	public void onClick(View arg0)
	{
		if(!emailEditText.getText().toString().equals("") && !nameEditText.getText().toString().equals(""))
		{
			savePreferences();
			if(Preferences.getFirstTime(this))
			{
				finish();
			}
		}
	}
	private void savePreferences()
	{
		Preferences.setName(this, nameEditText.getText().toString());
		Preferences.setEmail(this, emailEditText.getText().toString());
		
	}
}