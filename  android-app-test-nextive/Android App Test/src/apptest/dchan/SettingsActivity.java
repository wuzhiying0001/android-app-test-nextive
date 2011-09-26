package apptest.dchan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener {
	private Button mSaveButton;
	private Button mEditDefaultContact;
	private EditText mNameEditText;
	private EditText mEmailEditText;
	private RadioButton mKilograms;
	private RadioButton mPounds;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		mSaveButton = (Button) findViewById(R.id.saveButton);
		mEditDefaultContact = (Button) findViewById(R.id.editContacts);
		mNameEditText = (EditText) findViewById(R.id.nameEditText);
		mEmailEditText = (EditText) findViewById(R.id.emailEditText);
		mKilograms = (RadioButton) findViewById(R.id.optionsKilos);
		mPounds = (RadioButton) findViewById(R.id.optionsPounds);

		mSaveButton.setOnClickListener(this);
		mEditDefaultContact.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(mSaveButton)) {
			if (!mEmailEditText.getText().toString().equals("")
					&& !mNameEditText.getText().toString().equals("")) {
				savePreferences();
				if (Preferences.isFirstTime(this)) {
					Preferences.setFirstTime(this, false);
					setResult(Activity.RESULT_OK);
					finish();
				}
				Toast.makeText(this, R.string.recordSaved, Toast.LENGTH_LONG).show();
			} else if (mNameEditText.getText().toString().equals("")) {
				createError(R.string.email_missing);
			} else {
				createError(R.string.name_missing);
			}
		} else if (arg0.equals(mEditDefaultContact)) {
			Intent intent = new Intent(this, ManageContactActivity.class);
			startActivity(intent);
		}

	}

	private void savePreferences() {
		Preferences.setName(this, mNameEditText.getText().toString());
		Preferences.setEmail(this, mEmailEditText.getText().toString());
		if (mKilograms.isChecked()) {
			Preferences.setUnit(this, WeightTime.Unit.KILOGRAM);
		} else {
			Preferences.setUnit(this, WeightTime.Unit.POUND);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mNameEditText.setText(Preferences.getName(this));
		mEmailEditText.setText(Preferences.getEmail(this));
		if (Preferences.getUnit(this).equals(WeightTime.Unit.KILOGRAM)) {
			mKilograms.setChecked(true);
			mPounds.setChecked(false);
		} else {
			mKilograms.setChecked(false);
			mPounds.setChecked(true);
		}
	}

	private void createError(int resourceID) {
		AlertDialog.Builder errorMessage = new AlertDialog.Builder(this);
		errorMessage.setTitle(R.string.error);
		errorMessage.setMessage(resourceID);
		errorMessage.setPositiveButton(R.string.ok, null);
		errorMessage.show();
	}

}
