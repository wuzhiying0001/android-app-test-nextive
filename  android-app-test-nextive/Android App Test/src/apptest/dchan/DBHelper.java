package apptest.dchan;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public final static String DBNAME = "weightAppDB";

	public final static String DATA_TABLE = "DATA";
	public final static String CONTACT_TABLE = "CONTACTS";

	public final static String CONTACT_NAME = "CONTACT_NAME";
	public final static String EMAIL_ADDRESS = "EMAIL_ADDRESS";
	public final static String DATE = "DATE_VALUE";
	public final static String WEIGHT = "WEIGHT";
	public final static String UID = "ROWID";

	public static int VERSION = 1;

	public DBHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
	}

	/*
	 * Called when the database has been opened. The implementation should check
	 * isReadOnly() before updating the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_TABLE + "(" + UID
					+ " INTEGER PRIMARY KEY, " + DATE + " DATE, " + WEIGHT + " REAL)");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + "(" + CONTACT_NAME
					+ " TEXT, " + EMAIL_ADDRESS + " TEXT)");
		} catch (SQLException e) {
			Log.e("SqliteAndroid", "DBOpenHelper", e);
		}
	}

	/*
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public static long insertRow(Context c, WeightTime weightTime) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DATE, weightTime.getDate().getTimeInMillis());
		cv.put(WEIGHT, weightTime.getWeightKG());
		long result = db.insertOrThrow(DATA_TABLE, null, cv);
		db.close();
		return result;
	}

	public static LinkedList<WeightTime> getWeightTime(Context c, GregorianCalendar startDate,
			GregorianCalendar endDate) {
		LinkedList<WeightTime> results = new LinkedList<WeightTime>();
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = { UID, DATE, WEIGHT };
		String selection = "";

		if (startDate != null) {
			selection = selection + DATE + ">" + startDate.getTimeInMillis() + " ";
		}
		if (startDate != null && endDate != null) {
			selection = selection + " and ";
		}
		if (endDate != null) {
			selection = selection + DATE + "<" + endDate.getTimeInMillis() + " ";
		}

		Cursor cursor = db.query(DATA_TABLE, columns, selection, null, null, null, DATE);
		while (cursor.moveToNext()) {
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));
			float weight = cursor.getFloat(cursor.getColumnIndex(WEIGHT));
			int rowNum = cursor.getInt(cursor.getColumnIndex(UID));
			WeightTime wt = new WeightTime(date, weight, WeightTime.KILOGRAM, rowNum);
			results.addLast(wt);
		}
		cursor.close();
		db.close();
		return results;
	}

	public static int deleteRow(Context c, int rowID) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete(DATA_TABLE, UID + "=" + rowID, null);
		db.close();
		return result;
	}

	public static int updateRow(Context c, int rowID, WeightTime weightTime) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(DATE, weightTime.getDate().getTimeInMillis());
		cv.put(WEIGHT, weightTime.getWeightKG());
		int result = db.update(DATA_TABLE, cv, UID + "=" + rowID, null);
		db.close();
		return result;
	}

	public static WeightTime getRow(Context c, int rowID) {
		WeightTime returnItem = null;
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = { UID, DATE, WEIGHT };
		String selection = UID + "=" + rowID;
		Cursor cursor = db.query(DATA_TABLE, columns, selection, null, null, null, DATE);
		while (cursor.moveToNext()) {
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));
			float weight = cursor.getFloat(cursor.getColumnIndex(WEIGHT));
			returnItem = new WeightTime(date, weight, WeightTime.KILOGRAM, rowID);
		}
		cursor.close();
		return returnItem;
	}

	public static GregorianCalendar getFirstEntryDate(Context c) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = { DATE };
		Cursor cursor = db.query(DATA_TABLE, columns, null, null, null, null, DATE + " asc", "1");
		if (cursor.moveToNext()) {
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));
			return date;
		}
		cursor.close();
		return null;
	}

	public static long addContact(Context c, Contact contact) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(CONTACT_NAME, contact.getName());
		cv.put(EMAIL_ADDRESS, contact.getEmailAddress());
		long result = db.insertOrThrow(CONTACT_TABLE, null, cv);
		db.close();
		return result;
	}

	public static int deleteContact(Context c, String emailAddress) {
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete(CONTACT_TABLE, EMAIL_ADDRESS + "='" + emailAddress + "'", null);
		db.close();
		return result;
	}

	public static LinkedList<Contact> getAllContacts(Context c) {
		LinkedList<Contact> results = new LinkedList<Contact>();
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = { CONTACT_NAME, EMAIL_ADDRESS };
		Cursor cursor = db.query(CONTACT_TABLE, columns, null, null, null, null, CONTACT_NAME);
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
			String email = cursor.getString(cursor.getColumnIndex(EMAIL_ADDRESS));
			results.add(new Contact(name, email));
		}
		cursor.close();
		db.close();
		return results;
	}

	public static LinkedList<String> getAllEmails(Context c) {
		LinkedList<String> results = new LinkedList<String>();
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = { EMAIL_ADDRESS };
		Cursor cursor = db.query(CONTACT_TABLE, columns, null, null, null, null, CONTACT_NAME);
		while (cursor.moveToNext()) {
			String email = cursor.getString(cursor.getColumnIndex(EMAIL_ADDRESS));
			results.add(email);
		}
		cursor.close();
		db.close();
		return results;
	}
}
