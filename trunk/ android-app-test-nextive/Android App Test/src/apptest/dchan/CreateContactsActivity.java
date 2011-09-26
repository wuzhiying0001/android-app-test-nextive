package apptest.dchan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TableLayout;
import apptest.dchan.R;

public final class CreateContactsActivity extends Activity implements OnClickListener {
	private TableLayout mPhoneTable;
	private TableLayout mEmailTable;
	private ImageButton mPhoneAdd;
	private ImageButton mPhoneMinus;
	private ImageButton mEmailAdd;
	private ImageButton mEmailMinus;
	private Button mSaveButton;
	private ArrayList<Integer> mPhoneTypes;
	private ArrayList<Integer> mEmailTypes;
	private Spinner mAccountSpinner;
	private EditText mContactName;
	private Account[] mAllAccouts;

	/**
	 * Called when the activity is first created. Responsible for initializing
	 * the UI.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.create_contact);

		mPhoneTable = (TableLayout) findViewById(R.id.phone_table);
		mEmailTable = (TableLayout) findViewById(R.id.email_table);
		mPhoneAdd = (ImageButton) findViewById(R.id.phone_add_button);
		mPhoneMinus = (ImageButton) findViewById(R.id.phone_minus_button);
		mEmailAdd = (ImageButton) findViewById(R.id.email_add_button);
		mEmailMinus = (ImageButton) findViewById(R.id.email_minus_button);
		mAccountSpinner = (Spinner) findViewById(R.id.accounts);
		mSaveButton = (Button) findViewById(R.id.createContactSave);
		mContactName = (EditText) findViewById(R.id.nameField);

		mPhoneAdd.setOnClickListener(this);
		mPhoneMinus.setOnClickListener(this);
		mEmailAdd.setOnClickListener(this);
		mEmailMinus.setOnClickListener(this);
		mSaveButton.setOnClickListener(this);

		mPhoneTypes = new ArrayList<Integer>();
		mPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
		mPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
		mPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
		mPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);

		mEmailTypes = new ArrayList<Integer>();
		mEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_HOME);
		mEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
		mEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
		mEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_OTHER);

		mAllAccouts = AccountManager.get(this).getAccounts();

		loadAccounts();
		ContactInfo info = (ContactInfo) getLastNonConfigurationInstance();
		if (info != null) {
			loadRows(info);
		}
		
		getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
	}

	/**
	 * Called on rotation of the screen.
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
		return saveRows();
	}

	/**
	 * Loads all the accounts of the user.
	 */
	private void loadAccounts() {
		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (Account i : mAllAccouts) {
			adapter.add(i.name);
		}
		mAccountSpinner.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mPhoneAdd)) {
			addPhoneRow();
		} else if (v.equals(mPhoneMinus)) {
			minusPhoneRow();
		} else if (v.equals(mEmailAdd)) {
			addEmailRow();
		} else if (v.equals(mEmailMinus)) {
			minusEmailRow();
		} else if (v.equals(mSaveButton)) {
			save();
		}
	}

	/**
	 * Finds an Account based on the name.
	 * 
	 * @param name
	 *            the name that is used to search
	 * @return the account associated with the name or null if non is found
	 */
	private Account findAccount(String name) {
		for (Account i : mAllAccouts) {
			if (i.name.equals(name))
				return i;
		}
		return null;
	}

	/**
	 * Saves the contact's information with the selected account.
	 */
	private void save() {
		ContactInfo info = saveRows();
		String name = info.getName();
		String accountName = info.getAccountName();
		Account selectedAccount = findAccount(accountName);

		// Prepare contact creation request
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, selectedAccount.type)
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, selectedAccount.name).build());
		ops.add(ContentProviderOperation
				.newInsert(ContactsContract.Data.CONTENT_URI)
				.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
				.build());

		// Get an iterator for the list of phone types and numbers.
		// Since they are added at the same time, when iterating through them,
		// they should be for the same row.
		LinkedList<Integer> contactPhoneType = info.getPhoneTypes();
		LinkedList<String> contactPhoneNumbers = info.getPhoneNumbers();
		Iterator<Integer> iter = contactPhoneType.iterator();
		Iterator<String> iter1 = contactPhoneNumbers.iterator();
		while (iter.hasNext() && iter1.hasNext()) {
			int phoneType = mPhoneTypes.get(iter.next().intValue());
			String phoneNumber=iter1.next();

			//If the text entered for phone number has weird characters then show and error and stop saving.
			if(!checkPhoneNumber(phoneNumber))
			{
				showError(R.string.phoneNumberFormatException);
				return;
			}

			// Adds the phone number and type to the ContentProviderOperation
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
					.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType).build());
		}
		LinkedList<Integer> contactEmailTypes = info.getEmailType();
		LinkedList<String> contactEmails = info.getEmails();
		iter = contactEmailTypes.iterator();
		iter1 = contactEmails.iterator();
		while (iter.hasNext() && iter1.hasNext()) {
			int emailType = mEmailTypes.get(iter.next().intValue());
			String emailAddress = iter1.next();
			
			//Make sure the email text at least has an @ sign.
			if (emailAddress.indexOf("@") < 0) {
				showError(R.string.emailFormatException);
				return;
			}
			ops.add(ContentProviderOperation
					.newInsert(ContactsContract.Data.CONTENT_URI)
					.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE,
							ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
					.withValue(ContactsContract.CommonDataKinds.Email.DATA, emailAddress)
					.withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType).build());
		}
		try {
			ContentProviderResult[] asdf = getContentResolver().applyBatch(
					ContactsContract.AUTHORITY, ops);
			Intent intent = new Intent(Intent.ACTION_PICK, asdf[0].uri);
			setResult(Activity.RESULT_OK, intent);
			finish();
		} catch (Exception e) {
			showError(R.string.contactCreationFailure);
		}
	}

	/**
	 * Adds a row in the ui for the user to input a phone number and type into.
	 */
	private void addPhoneRow() {
		LinearLayout linearLayout = new LinearLayout(this);

		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (Integer i : mPhoneTypes) {
			String contactType = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(
					this.getResources(), i, getString(R.string.unknownType));
			adapter.add(contactType);
		}

		Spinner phoneSpinner = new Spinner(this);
		phoneSpinner.setAdapter(adapter);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		EditText et = new EditText(this);
		et.setRawInputType(InputType.TYPE_CLASS_PHONE);
		et.setLayoutParams(layoutParams);
		linearLayout.addView(phoneSpinner);
		linearLayout.addView(et);
		mPhoneTable.addView(linearLayout);
	}

	/**
	 * Removes a phone row from the ui.
	 */
	private void minusPhoneRow() {
		if (mPhoneTable.getChildCount() > 0)
			mPhoneTable.removeViewAt(mPhoneTable.getChildCount() - 1);
	}

	/**
	 * Adds a row in th ui for the user to input an email address and type into.
	 */
	private void addEmailRow() {
		LinearLayout linearLayout = new LinearLayout(this);

		ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for (Integer i : mEmailTypes) {
			String contactType = (String) ContactsContract.CommonDataKinds.Email.getTypeLabel(
					this.getResources(), i, getString(R.string.unknownType));
			adapter.add(contactType);
		}

		Spinner emailSpinner = new Spinner(this);
		emailSpinner.setAdapter(adapter);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		EditText et = new EditText(this);
		et.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et.setLayoutParams(layoutParams);

		linearLayout.addView(emailSpinner);
		linearLayout.addView(et);
		mEmailTable.addView(linearLayout);
	}

	/**
	 * Removes an email row from the ui.
	 */
	private void minusEmailRow() {
		if (mEmailTable.getChildCount() > 0)
			mEmailTable.removeViewAt(mEmailTable.getChildCount() - 1);
	}

	/**
	 * Creates and then fills in the phone and email rows.
	 * 
	 * @param info
	 *            the contact info used to fill in the information.
	 */
	private void loadRows(ContactInfo info) {

		mContactName.setText(info.getName());
		mAccountSpinner.setSelection(info.getSelectedAccount());
		LinkedList<Integer> phoneType = info.getPhoneTypes();
		LinkedList<String> phoneNumber = info.getPhoneNumbers();
		Iterator<Integer> i = phoneType.iterator();
		Iterator<String> i1 = phoneNumber.iterator();
		while (i.hasNext() && i1.hasNext()) {
			addPhoneRow();
			LinearLayout zxcv = (LinearLayout) mPhoneTable
					.getChildAt(mPhoneTable.getChildCount() - 1);
			Spinner phoneSpinner = (Spinner) zxcv.getChildAt(0);
			phoneSpinner.setSelection(i.next());
			EditText phone = (EditText) zxcv.getChildAt(1);
			phone.setText(i1.next());
		}

		LinkedList<Integer> emailType = info.getEmailType();
		LinkedList<String> emails = info.getEmails();
		i = emailType.iterator();
		i1 = emails.iterator();
		while (i.hasNext() && i1.hasNext()) {
			addEmailRow();
			LinearLayout zxcv = (LinearLayout) mEmailTable
					.getChildAt(mEmailTable.getChildCount() - 1);
			Spinner emailSpinner = (Spinner) zxcv.getChildAt(0);
			emailSpinner.setSelection(i.next());
			EditText emailAddress = (EditText) zxcv.getChildAt(1);
			emailAddress.setText(i1.next());
		}

	}

	/**
	 * Saves the contact's name, phone number(s), and email(s) into a
	 * ContactInfo object.
	 * 
	 * @return the ContactInfo object where all the information is stored
	 */
	private ContactInfo saveRows() {
		ContactInfo info = new ContactInfo();
		info.setName(mContactName.getText().toString());
		info.setSelectedAccount(mAccountSpinner.getSelectedItemPosition());
		info.setAccountName(mAccountSpinner.getSelectedItem().toString());

		// Get each email row's email type and email address and add it to the
		// ContactInfo object.
		for (int i = 0; i < mEmailTable.getChildCount(); i++) {
			LinearLayout zxcv = (LinearLayout) mEmailTable.getChildAt(i);
			Spinner emailSpinner = (Spinner) zxcv.getChildAt(0);
			int emailType = emailSpinner.getSelectedItemPosition();
			String emailAddress = ((EditText) zxcv.getChildAt(1)).getText().toString();
			info.addEmail(emailType, emailAddress);
		}

		// Get each phone row's phone type and phone number and add it to the
		// contact info object.
		for (int i = 0; i < mPhoneTable.getChildCount(); i++) {
			LinearLayout zxcv = (LinearLayout) mPhoneTable.getChildAt(i);
			Spinner phoneSpinner = (Spinner) zxcv.getChildAt(0);
			int phoneType = phoneSpinner.getSelectedItemPosition();
			String phoneNumber = ((EditText) zxcv.getChildAt(1)).getText().toString();
			info.addPhone(phoneType, phoneNumber);
		}
		return info;
	}

	/**
	 * Shows an error message.
	 */
	private void showError(int errorId) {
		Context ctx = getApplicationContext();
		CharSequence txt = getString(errorId);
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(ctx, txt, duration);
		toast.show();
	}

	/**
	 * Checks to see if a string only has numbers, parentheses, or dashes
	 * @param str
	 * @return
	 */
	private boolean checkPhoneNumber(String str) {
		// It can't contain only numbers if it's null or empty...
		if (str == null || str.length() == 0) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {

			// If we find a non-digit character we return false.
			char character=str.charAt(i);
			if (!Character.isDigit(character) && character!='(' &&  character!=')' && character!='-')
				return false;
		}
		return true;
	}

	private class ContactInfo {
		private String mContactName;
		private int mSelectedAccount;
		private String mSelectedAccountName;
		private LinkedList<Integer> mPhoneType;
		private LinkedList<String> mPhoneNumber;
		private LinkedList<Integer> mEmailType;
		private LinkedList<String> mEmail;

		public ContactInfo() {
			mSelectedAccount = 0;
			mSelectedAccountName = "";
			mContactName = "";
			mPhoneType = new LinkedList<Integer>();
			mPhoneNumber = new LinkedList<String>();
			mEmailType = new LinkedList<Integer>();
			mEmail = new LinkedList<String>();
		}

		public void setAccountName(String name) {
			mSelectedAccountName = name;
		}

		public String getAccountName() {
			return mSelectedAccountName;
		}

		public void addPhone(int type, String number) {
			mPhoneType.addLast(new Integer(type));
			mPhoneNumber.addLast(number);
		}

		public void addEmail(int type, String number) {
			mEmailType.addLast(new Integer(type));
			mEmail.addLast(number);
		}

		public void setName(String name) {
			mContactName = name;
		}

		public String getName() {
			return mContactName;
		}

		public void setSelectedAccount(int account) {
			mSelectedAccount = account;
		}

		public int getSelectedAccount() {
			return mSelectedAccount;
		}

		public LinkedList<Integer> getPhoneTypes() {
			return mPhoneType;
		}

		public LinkedList<String> getPhoneNumbers() {
			return mPhoneNumber;
		}

		public LinkedList<Integer> getEmailType() {
			return mEmailType;
		}

		public LinkedList<String> getEmails() {
			return mEmail;
		}
	}
}
