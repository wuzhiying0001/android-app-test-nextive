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
		loadRows();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return saveRows();
	}

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

	private Account findAccount(String name) {
		for (Account i : mAllAccouts) {
			if (i.name.equals(name))
				return i;
		}
		return null;
	}

	private void save() {
		ContactInfo info = saveRows();
		String name = info.getName();
		String accountName = info.getAccountName();
		Account selectedAccount = findAccount(accountName);
		// Prepare contact creation request
		//
		// Note: We use RawContacts because this data must be associated with a
		// particular account.
		// The system will aggregate this with any other data for this contact
		// and create a
		// coresponding entry in the ContactsContract.Contacts provider for us.
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

		LinkedList<Integer> contactPhoneType = info.getPhoneTypes();
		LinkedList<String> contactPhoneNumbers = info.getPhoneNumbers();
		Iterator<Integer> iter = contactPhoneType.iterator();
		Iterator<String> iter1 = contactPhoneNumbers.iterator();
		while (iter.hasNext() && iter1.hasNext()) {
			int phoneType = mPhoneTypes.get(iter.next().intValue());
			int phoneNumber;
			try {
				phoneNumber = Integer.parseInt(iter1.next());
			} catch (NumberFormatException e) {
				Context ctx = getApplicationContext();
				CharSequence txt = getString(R.string.phoneNumberFormatException);
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(ctx, txt, duration);
				toast.show();
				return;
			}
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
			// Display warning
			Context ctx = getApplicationContext();
			CharSequence txt = getString(R.string.contactCreationFailure);
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(ctx, txt, duration);
			toast.show();
		}
	}

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

	private void minusPhoneRow() {
		if (mPhoneTable.getChildCount() > 0)
			mPhoneTable.removeViewAt(mPhoneTable.getChildCount() - 1);
	}

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

	private void minusEmailRow() {
		if (mEmailTable.getChildCount() > 0)
			mEmailTable.removeViewAt(mEmailTable.getChildCount() - 1);
	}

	private void loadRows() {
		ContactInfo info = (ContactInfo) getLastNonConfigurationInstance();
		if (info != null) {
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
	}

	private ContactInfo saveRows() {
		ContactInfo info = new ContactInfo();
		info.setName(mContactName.getText().toString());
		info.setSelectedAccount(mAccountSpinner.getSelectedItemPosition());
		info.setAccountName(mAccountSpinner.getSelectedItem().toString());
		for (int i = 0; i < mEmailTable.getChildCount(); i++) {
			LinearLayout zxcv = (LinearLayout) mEmailTable.getChildAt(i);
			Spinner emailSpinner = (Spinner) zxcv.getChildAt(0);
			int emailType = emailSpinner.getSelectedItemPosition();
			String emailAddress = ((EditText) zxcv.getChildAt(1)).getText().toString();
			info.addEmail(emailType, emailAddress);
		}
		for (int i = 0; i < mPhoneTable.getChildCount(); i++) {
			LinearLayout zxcv = (LinearLayout) mPhoneTable.getChildAt(i);
			Spinner phoneSpinner = (Spinner) zxcv.getChildAt(0);
			int phoneType = phoneSpinner.getSelectedItemPosition();
			String phoneNumber = ((EditText) zxcv.getChildAt(1)).getText().toString();
			info.addPhone(phoneType, phoneNumber);
		}
		return info;
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
