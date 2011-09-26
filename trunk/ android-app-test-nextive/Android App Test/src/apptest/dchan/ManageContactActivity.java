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
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class ManageContactActivity extends Activity implements OnClickListener {
	private TableLayout mTable;
	private Button mCreateContact;
	private Button mChooseContact;
	private View mClickedView;
	private final int DELETE_ACTION = 1;
	private final int PICK_CONTACT_REQUEST = 2;
	private final int CREATE_CONTACT_REQUEST = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.manage_contact);
		mTable = (TableLayout) findViewById(R.id.contactTable);
		mCreateContact = (Button) findViewById(R.id.createContactButton);
		mChooseContact = (Button) findViewById(R.id.pickContactButton);

		mCreateContact.setOnClickListener(this);
		mChooseContact.setOnClickListener(this);

		getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
	}

	@Override
	public void onResume() {
		super.onResume();
		mTable.removeAllViews();
		populateTable();
	}

	/**
	 * Gets all the default recipient contacts from the database and then makes
	 * a row for each of them.
	 */
	private void populateTable() {
		LinkedList<Contact> allEntries = DBHelper.getAllContacts(this);
		for (Contact aRow : allEntries) {
			addContactRow(aRow.getName(), aRow.getEmailAddress());
		}
	}

	/**
	 * Shows the delete contact from list menu. That is the only option.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		RelativeLayout rl = (RelativeLayout) v;
		TextView tv1 = (TextView) rl.findViewById(R.id.contactName);
		TextView tv2 = (TextView) rl.findViewById(R.id.contactEmail);
		menu.setHeaderTitle(tv1.getText() + " " + tv2.getText());
		menu.add(0, DELETE_ACTION, 0, "Delete");
		mClickedView = v;
	}

	/**
	 * Deletes the selected contact from the database.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == DELETE_ACTION) {
			TextView email = (TextView) mClickedView.findViewById(R.id.contactEmail);
			DBHelper.deleteContact(this, email.getText().toString());
			mTable.removeView(mClickedView);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.equals(mCreateContact)) {
			Intent intent = new Intent(this, CreateContactsActivity.class);
			startActivityForResult(intent, CREATE_CONTACT_REQUEST);
		} else if (arg0.equals(mChooseContact)) {
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String[] emailOptions = getContactEmail(data).toArray(new String[0]);
			String name = getContactName(data);
			addMail(emailOptions, name);
		}
	}

	/**
	 * Gets the name of the contact that was selected or created and returns it.
	 */
	private String getContactName(Intent data) {
		Cursor cursor = null;
		String contactName = getString(R.string.unknownName);
		try {
			Uri result = data.getData();

			// get the contact id from the Uri
			String id = result.getLastPathSegment();

			String[] columns = { Contacts.DISPLAY_NAME };
			cursor = getContentResolver().query(Contacts.CONTENT_URI, columns, Contacts._ID + "=?",
					new String[] { id }, null);

			if (cursor.moveToNext()) {
				contactName = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
			}
			cursor.close();
		} catch (Exception e) {
		}
		return contactName;
	}

	/** 
	 * Gets a list of all the contact's email addresses.
	 */
	private LinkedList<String> getContactEmail(Intent data) {
		Cursor cursor = null;
		LinkedList<String> emails = new LinkedList<String>();
		try {
			Uri result = data.getData();
			// get the contact id from the Uri
			String id = result.getLastPathSegment();
			String[] columns={Email.DATA};
			// query for everything email
			cursor = getContentResolver().query(Email.CONTENT_URI, columns, Email.CONTACT_ID + "=?",
					new String[] { id }, null);

			int emailIdx = cursor.getColumnIndex(Email.DATA);

			while (cursor.moveToNext()) {
				emails.add(cursor.getString(emailIdx));
			}
			cursor.close();
		} catch (Exception e) {
		}
		return emails;
	}

	/**
	 * Adds a row to the table for the contact's chosen email or show an error if the contact doesn't have one.
	 * @param emailOptions email addresses of the contact
	 * @param name name of the contact
	 */
	private void addMail(final String[] emailOptions, final String name) {
		if (emailOptions.length > 1) {
			final Context c = this;
			AlertDialog.Builder chooseEmailDialog = new Builder(this);
			//Shows an alert dialog that lets the user choose which email of the contact to use if they have more than 1.
			chooseEmailDialog.setItems(emailOptions, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String chosenEmail = emailOptions[which];
					DBHelper.addContact(c, new Contact(name, chosenEmail));
					addContactRow(name, chosenEmail);
				}
			});
			chooseEmailDialog.setTitle(R.string.chooseEmail);
			chooseEmailDialog.show();
		} else if (emailOptions.length == 1) {
			//If they only have 1 email address then use that one.
			String email = emailOptions[0];
			DBHelper.addContact(this, new Contact(name, email));
		} else {
			createError(R.string.noEmail);
		}
	}

	private void createError(int resourceID) {
		AlertDialog.Builder errorMessage = new AlertDialog.Builder(this);
		errorMessage.setTitle(R.string.error);
		errorMessage.setMessage(resourceID);
		errorMessage.setPositiveButton(R.string.ok, null);
		errorMessage.show();
	}

	/**
	 * Adds a new row to the table.
	 * @param _name Contact's name
	 * @param _email Contact's email address
	 */
	private void addContactRow(String _name, String _email) {
		LayoutInflater inflater = getLayoutInflater();
		View myView = inflater.inflate(R.layout.contact_row, null);
		TextView name = (TextView) myView.findViewById(R.id.contactName);
		TextView email = (TextView) myView.findViewById(R.id.contactEmail);
		name.setText(_name);
		email.setText(_email);
		registerForContextMenu(myView);
		mTable.addView(myView);
	}
}
