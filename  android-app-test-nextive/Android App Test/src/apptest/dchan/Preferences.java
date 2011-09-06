package apptest.dchan;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public final static String UNIT="UNIT";
	public final static String LAST_WEIGHT="WEIGHT";
	public final static String DEFAULT_RECIPIENT="RECIPIENT";
	public final static String NAME="NAME";
	public final static String EMAIL="EMAIL";
	public final static String DEFAULT_RECIPIENT_EMAIL="DEFAULT_RECIPIENT_EMAIL";
	
	public static String getUnit(Context c)
	{
		SharedPreferences settings=c.getSharedPreferences(UNIT, Context.MODE_PRIVATE);
		return settings.getString(UNIT, "");
	}
}
