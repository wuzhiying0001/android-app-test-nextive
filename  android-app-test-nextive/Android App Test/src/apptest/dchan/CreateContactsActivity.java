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
import android.util.Log;
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
        
        loadAccounts();
        loadRows();
    }
    @Override
    public Object onRetainNonConfigurationInstance()
    {
    	return saveRows();
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
		ContactInfo info=saveRows();
		String name = info.getName();
		String accountName=info.getAccountName();
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
        
        LinkedList<Integer> contactPhoneType=info.getPhoneTypes();
		LinkedList<String> contactPhoneNumbers=info.getPhoneNumbers();
		Iterator<Integer> iter=contactPhoneType.iterator();
		Iterator<String> iter1=contactPhoneNumbers.iterator();
		while(iter.hasNext() && iter1.hasNext())
		{
			int phoneType=phoneTypes.get(iter.next().intValue());
        	int phoneNumber;
        	try
        	{
        		phoneNumber=Integer.parseInt(iter1.next());
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
		LinkedList<Integer> contactEmailTypes=info.getEmailType();
		LinkedList<String> contactEmails=info.getEmails();
		iter=contactEmailTypes.iterator();
		iter1=contactEmails.iterator();
		while(iter.hasNext() && iter1.hasNext())
		{
			int emailType=emailTypes.get(iter.next().intValue());
        	String emailAddress=iter1.next();
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
    	ContactInfo info=(ContactInfo)getLastNonConfigurationInstance();
    	if(info!=null)
    	{
    		contactName.setText(info.getName());
    		accountSpinner.setSelection(info.getSelectedAccount());
    		LinkedList<Integer> phoneType=info.getPhoneTypes();
    		LinkedList<String> phoneNumber=info.getPhoneNumbers();
    		Iterator<Integer> i=phoneType.iterator();
    		Iterator<String> i1=phoneNumber.iterator();
    		while(i.hasNext() && i1.hasNext())
    		{
    			addPhoneRow();
    			LinearLayout zxcv=(LinearLayout)phoneTable.getChildAt(phoneTable.getChildCount()-1);
            	Spinner phoneSpinner=(Spinner)zxcv.getChildAt(0);
            	phoneSpinner.setSelection(i.next());
            	EditText phone=(EditText)zxcv.getChildAt(1);
            	phone.setText(i1.next());
    		}
    		
    		LinkedList<Integer> emailType=info.getEmailType();
    		LinkedList<String> emails=info.getEmails();
    		i=emailType.iterator();
    		i1=emails.iterator();
    		while(i.hasNext() && i1.hasNext())
    		{
    			addEmailRow();
    			LinearLayout zxcv=(LinearLayout)emailTable.getChildAt(emailTable.getChildCount()-1);
            	Spinner emailSpinner=(Spinner)zxcv.getChildAt(0);
            	emailSpinner.setSelection(i.next());
            	EditText emailAddress=(EditText)zxcv.getChildAt(1);
            	emailAddress.setText(i1.next());
    		}
    	}
    }
    private ContactInfo saveRows()
    {
    	ContactInfo info=new ContactInfo();
    	info.setName(contactName.getText().toString());
    	info.setSelectedAccount(accountSpinner.getSelectedItemPosition());
    	info.setAccountName(accountSpinner.getSelectedItem().toString());
    	for(int i=0; i<emailTable.getChildCount(); i++)
        {
        	LinearLayout zxcv=(LinearLayout)emailTable.getChildAt(i);
        	Spinner emailSpinner=(Spinner)zxcv.getChildAt(0);
        	int emailType=emailSpinner.getSelectedItemPosition();
        	String emailAddress=((EditText)zxcv.getChildAt(1)).getText().toString();
        	info.addEmail(emailType, emailAddress);
        }
    	for(int i=0; i<phoneTable.getChildCount(); i++)
        {
        	LinearLayout zxcv=(LinearLayout)phoneTable.getChildAt(i);
        	Spinner phoneSpinner=(Spinner)zxcv.getChildAt(0);
        	int phoneType=phoneSpinner.getSelectedItemPosition();
        	String phoneNumber=((EditText)zxcv.getChildAt(1)).getText().toString();
        	info.addPhone(phoneType, phoneNumber);
        }
    	return info;
    }
    private class ContactInfo
    {
    	private String contactName;
    	private int selectedAccount;
    	private String selectedAccountName;
    	private LinkedList<Integer> phoneType;
    	private LinkedList<String> phoneNumber;
    	private LinkedList<Integer> emailType;
    	private LinkedList<String> email;
    	public ContactInfo()
    	{
    		selectedAccount=0;
    		selectedAccountName="";
    		contactName="";
    		phoneType=new LinkedList<Integer>();
    		phoneNumber=new LinkedList<String>();
    		emailType=new LinkedList<Integer>();
    		email=new LinkedList<String>();
    	}
    	public void setAccountName(String name)
    	{
    		selectedAccountName=name;
    	}
    	public String getAccountName()
    	{
    		return selectedAccountName;
    	}
    	public void addPhone(int type, String number)
    	{
    		phoneType.addLast(new Integer(type));
    		phoneNumber.addLast(number);
    	}
    	public void addEmail(int type, String number)
    	{
    		emailType.addLast(new Integer(type));
    		email.addLast(number);
    	}
    	public void setName(String name)
    	{
    		contactName=name;
    	}
    	public String getName()
    	{
    		return contactName;
    	}
    	public void setSelectedAccount(int account)
    	{
    		selectedAccount=account;
    	}
    	public int getSelectedAccount()
    	{
    		return selectedAccount;
    	}
    	public LinkedList<Integer> getPhoneTypes()
    	{
    		return phoneType;
    	}
    	public LinkedList<String> getPhoneNumbers()
    	{
    		return phoneNumber;
    	}
    	public LinkedList<Integer> getEmailType()
    	{
    		return emailType;
    	}
    	public LinkedList<String> getEmails()
    	{
    		return email;
    	}
    }
}
