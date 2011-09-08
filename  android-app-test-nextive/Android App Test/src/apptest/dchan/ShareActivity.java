package apptest.dchan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ShareActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share);
    }
	private void sendEmail(String[] emailAddresses)
	{
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		 
		emailIntent .setType("plain/text");
		 
		emailIntent .putExtra(android.content.Intent.EXTRA_EMAIL, emailAddresses);
		 
		emailIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "testing");
		 
		emailIntent .putExtra(android.content.Intent.EXTRA_TEXT, "asdfasdf");
		 
		this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
}
