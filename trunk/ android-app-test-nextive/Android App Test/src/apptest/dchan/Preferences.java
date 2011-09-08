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
	public final static String FIRST="FIRST";
	
	private final static String PREF_FILE="file";
	
	public static void setUnit(Context c, String unit)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=settings.edit();
		editor.putString(UNIT, unit);
		editor.commit();
	}
	public static String getUnit(Context c)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		return settings.getString(UNIT, "");
	}
	public static boolean isFirstTime(Context c)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		return settings.getBoolean(FIRST, true);
	}
	public static void setFirstTime(Context c, boolean firstTime)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=settings.edit();
		editor.putBoolean(FIRST, firstTime);
		editor.commit();
	}
	public static void setName(Context c, String name)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=settings.edit();
		editor.putString(NAME, name);
		editor.commit();
	}
	public static String getName(Context c)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		return settings.getString(NAME, "");
	}
	public static void setEmail(Context c, String email)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=settings.edit();
		editor.putString(EMAIL, email);
		editor.commit();
	}
	public static String getEmail(Context c)
	{
		SharedPreferences settings=c.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		return settings.getString(EMAIL, "");
	}
	
}
