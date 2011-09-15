package apptest.dchan;

import java.util.ArrayList;
import java.util.LinkedList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public final class CreateContactsActivity extends Activity implements OnClickListener
{
    TableLayout phoneTable;
    TableLayout emailtable;
    ImageButton phoneAdd;
    ImageButton phoneMinus;
    ImageButton emailAdd;
    ImageButton emailMinus;
    /**
     * Called when the activity is first created. Responsible for initializing the UI.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.create_contact);
        phoneTable=(TableLayout)findViewById(R.id.phone_table);
        emailtable=(TableLayout)findViewById(R.id.email_table);
        
        phoneAdd=(ImageButton)findViewById(R.id.phone_add_button);
        phoneMinus=(ImageButton)findViewById(R.id.phone_minus_button);
        emailAdd=(ImageButton)findViewById(R.id.email_add_button);
        emailMinus=(ImageButton)findViewById(R.id.email_minus_button);
        
        phoneAdd.setOnClickListener(this);
        phoneMinus.setOnClickListener(this);
        emailAdd.setOnClickListener(this);
        emailMinus.setOnClickListener(this);
        
        addPhoneRow();
        addEmailRow();
        loadRows();
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
	}
    private void addPhoneRow()
    {
    	LinearLayout linearLayout=new LinearLayout(this);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.phoneOptions,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner phoneSpinner=new Spinner(this);
		phoneSpinner.setAdapter(adapter);
		phoneSpinner.getSelectedItem().toString();
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
    	LinearLayout zxcv=(LinearLayout)phoneTable.getChildAt(0);
    	Spinner phoneSpinner=(Spinner)zxcv.getChildAt(0);
    	Log.i(phoneSpinner.getSelectedItem().toString(), "");
    }
    private void addEmailRow()
    {
    	LinearLayout linearLayout=new LinearLayout(this);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.emailOptions,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner phoneSpinner=new Spinner(this);
		phoneSpinner.setAdapter(adapter);
		
		LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
		
		EditText et=new EditText(this);
		et.setRawInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		et.setLayoutParams(layoutParams);
		
		linearLayout.addView(phoneSpinner);
		linearLayout.addView(et);
		emailtable.addView(linearLayout);
    }
    private void minusEmailRow()
    {
    	
    }
    private void loadRows()
    {
    	
    }
    private class ContactInfo
    {
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
