package apptest.dchan;

import java.util.ArrayList;
import java.util.LinkedList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import apptest.dchan.R;
import apptest.dchan.R.array;
import apptest.dchan.R.id;
import apptest.dchan.R.layout;
import apptest.dchan.R.string;

public final class CreateContactsActivity extends Activity implements OnClickListener
{
    TableLayout phoneTable;
    TableLayout emailTable;
    ImageButton phoneAdd;
    ImageButton phoneMinus;
    ImageButton emailAdd;
    ImageButton emailMinus;
    Button saveButton;
    ArrayList<Integer> phoneTypes;
    ArrayList<Integer> emailTypes;
    Spinner accountSpinner;
    EditText contactName;
    Account[] allAccouts;
    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.create_contact);
        
        phoneTable=(TableLayout)findViewById(R.id.phone_table);
        emailTable=(TableLayout)findViewById(R.id.email_table);
        phoneAdd=(ImageButton)findViewById(R.id.phone_add_button);
        phoneMinus=(ImageButton)findViewById(R.id.phone_minus_button);
        emailAdd=(ImageButton)findViewById(R.id.email_add_button);
        emailMinus=(ImageButton)findViewById(R.id.email_minus_button);
        accountSpinner=(Spinner)findViewById(R.id.accounts);
        saveButton=(Button)findViewById(R.id.createContactSave);
        contactName=(EditText)findViewById(R.id.nameField);
        
        phoneAdd.setOnClickListener(this);
        phoneMinus.setOnClickListener(this);
        emailAdd.setOnClickListener(this);
        emailMinus.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        
        phoneTypes = new ArrayList<Integer>();
        phoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        phoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        phoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        phoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
        emailTypes = new ArrayList<Integer>();
        emailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_HOME);
        emailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        emailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        emailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
        
        allAccouts=AccountManager.get(this).getAccounts();
        //addPhoneRow();
        //addEmailRow();
        loadAccounts();
        loadRows();
    }
    private void loadAccounts()
    {
    	ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(Account i : allAccouts)
        {
        	adapter.add(i.name);
        }
        accountSpinner.setAdapter(adapter);
    }
	@Override
	public void onClick(View v)
	{
		if(v.equals(phoneAdd))
		{
			addPhoneRow();
		}
		else if(v.equals(phoneMinus))
		{
			minusPhoneRow();
		}
		else if(v.equals(emailAdd))
		{
			addEmailRow();
		}
		else if(v.equals(emailMinus))
		{
			minusEmailRow();
		}
		else if(v.equals(saveButton))
		{
			save();
		}
	}
	private Account findAccount(String name)
	{
		for(Account i : allAccouts)
		{
			if(i.name.equals(name))
				return i;
		}
		return null;
	}
	private void save()
	{
		String name = contactName.getText().toString();
		String accountName=(String)accountSpinner.getSelectedItem();
		Account selectedAccount=findAccount(accountName);
        // Prepare contact creation request
        //
        // Note: We use RawContacts because this data must be associated with a particular account.
        //       The system will aggregate this with any other data for this contact and create a
        //       coresponding entry in the ContactsContract.Contacts provider for us.
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, selectedAccount.type)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, selectedAccount.name)
                .build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
        
        for(int i=0; i<phoneTable.getChildCount(); i++)
        {
        	LinearLayout zxcv=(LinearLayout)phoneTable.getChildAt(i);
        	Spinner phoneSpinner=(Spinner)zxcv.getChildAt(0);
        	int phoneType=phoneTypes.get(phoneSpinner.getSelectedItemPosition());
        	int phoneNumber;
        	try
        	{
        		phoneNumber=Integer.parseInt(((EditText)zxcv.getChildAt(1)).getText().toString());
        	}
        	catch(NumberFormatException e)
        	{
        		Context ctx = getApplicationContext();
                CharSequence txt = getString(R.string.phoneNumberFormatException);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(ctx, txt, duration);
                toast.show();
        		return;
        	}
        	 ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                     .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                     .withValue(ContactsContract.Data.MIMETYPE,
                             ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                     .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
                     .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType)
                     .build());
        }
        
        for(int i=0; i<emailTable.getChildCount(); i++)
        {
        	LinearLayout zxcv=(LinearLayout)emailTable.getChildAt(i);
        	Spinner emailSpinner=(Spinner)zxcv.getChildAt(0);
        	int emailType=emailTypes.get(emailSpinner.getSelectedItemPosition());
        	String emailAddress=((EditText)zxcv.getChildAt(1)).getText().toString();
        	ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailAddress)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
                    .build());
        }

        try {
            ContentProviderResult[] asdf=getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Intent intent=new Intent(Intent.ACTION_PICK, asdf[0].uri);
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
    private void addPhoneRow()
    {
    	LinearLayout linearLayout=new LinearLayout(this);
		
		ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(Integer i : phoneTypes)
        {
        	String contactType=(String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(), i, getString(R.string.unknownType));
        	adapter.add(contactType);
        }
        
		Spinner phoneSpinner=new Spinner(this);
		phoneSpinner.setAdapter(adapter);
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		
		EditText et=new EditText(this);
		et.setRawInputType(InputType.TYPE_CLASS_PHONE);
		et.setLayoutParams(layoutParams);
		linearLayout.addView(phoneSpinner);
		linearLayout.addView(et);
		phoneTable.addView(linearLayout);
    }
    private void minusPhoneRow()
    {
    	if(phoneTable.getChildCount()>0)
    		phoneTable.removeViewAt(phoneTable.getChildCount()-1);
//    	LinearLayout zxcv=(LinearLayout)phoneTable.getChildAt(phoneTable.getChildCount());
//    	Spinner phoneSpinner=(Spinner)zxcv.getChildAt(0);
//    	Log.i(phoneSpinner.getSelectedItem().toString(), "");
    }
    private void addEmailRow()
    {
    	LinearLayout linearLayout=new LinearLayout(this);
    	
    	ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(Integer i : emailTypes)
        {
        	String contactType=(String) ContactsContract.CommonDataKinds.Email.getTypeLabel(this.getResources(), i, getString(R.string.unknownType));
        	adapter.add(contactType);
        }
        
		Spinner emailSpinner=new Spinner(this);
		emailSpinner.setAdapter(adapter);
		
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		
		EditText et=new EditText(this);
		et.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et.setLayoutParams(layoutParams);
		
		linearLayout.addView(emailSpinner);
		linearLayout.addView(et);
		emailTable.addView(linearLayout);
    }
    private void minusEmailRow()
    {
    	if(emailTable.getChildCount()>0)
    		emailTable.removeViewAt(emailTable.getChildCount()-1);
    }
    private void loadRows()
    {
    	
    }
    private class ContactInfo
    {
    	private String contactName;
    	private LinkedList<Integer> phoneType;
    	private LinkedList<Integer> phoneNumber;
    	private LinkedList<Integer> emailType;
    	private LinkedList<String> email;
    	public ContactInfo()
    	{
    		phoneType=new LinkedList<Integer>();
    		phoneNumber=new LinkedList<Integer>();
    		emailType=new LinkedList<Integer>();
    		email=new LinkedList<String>();
    	}
    	public void addPhone(int type, int number)
    	{
    		phoneType.addLast(new Integer(type));
    		phoneNumber.addLast(new Integer(number));
    	}
    	public void removePhone()
    	{
    		phoneNumber.removeLast();
    		phoneType.removeLast();
    	}
    	public void addEmail(int type, String number)
    	{
    		emailType.addLast(new Integer(type));
    		email.addLast(number);
    	}
    	public void removeEmail()
    	{
    		phoneNumber.removeLast();
    		email.removeLast();
    	}
    }
}
