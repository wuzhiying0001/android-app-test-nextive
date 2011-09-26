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
	private final static String DBNAME = "weightAppDB";

	private final static String DATA_TABLE = "DATA";
	private final static String CONTACT_TABLE = "CONTACTS";

	private final static String CONTACT_NAME = "CONTACT_NAME";
	private final static String EMAIL_ADDRESS = "EMAIL_ADDRESS";
	private final static String DATE = "DATE_VALUE";
	private final static String WEIGHT = "WEIGHT";
	private final static String UNIT="UNIT";
	public final static String UID = "ROWID";

	public static int VERSION = 1;

	public DBHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version);
	}

	/**
	 * Called when the database has been opened. The implementation should check
	 * isReadOnly() before updating the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DATA_TABLE + "(" + UID
					+ " INTEGER PRIMARY KEY, " + DATE + " DATE, " + WEIGHT + " REAL, "+UNIT+" TEXT)");
			db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTACT_TABLE + "(" + CONTACT_NAME
					+ " TEXT, " + EMAIL_ADDRESS + " TEXT)");
		} catch (SQLException e) {
			Log.e("SqliteAndroid", "DBOpenHelper", e);
		}
	}

	/**
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * Inserts a row into the data table.  
	 * @param weightTime item to insert into the database.
	 * @return the rowid of the newly inserted row.
	 */
	public static long insertRow(Context c, WeightTime weightTime) {
		SQLiteDatabase db = getWritableDb(c);
		ContentValues cv = new ContentValues();
		cv.put(DATE, weightTime.getDate().getTimeInMillis());
		cv.put(WEIGHT, weightTime.getRawWeight());
		cv.put(UNIT, weightTime.getUnit().toString());
		long result = db.insertOrThrow(DATA_TABLE, null, cv);
		db.close();
		return result;
	}

	/**
	 * Gets all rows in the data table based on the start and end date.
	 * @param startDate no entries before this date are returned.  If null then there will be no limit for date of the first entry.
	 * @param endDate no entries after this date are returned  If null then there will be no limit for the date of the last entry.
	 * @return A linkedlist of WeightTime objects with all the rows.
	 */
	public static LinkedList<WeightTime> getWeightTime(Context c, GregorianCalendar startDate,
		GregorianCalendar endDate) {
		LinkedList<WeightTime> results = new LinkedList<WeightTime>();
		SQLiteDatabase db=getReadableDb(c);
		String[] columns = { UID, DATE, WEIGHT, UNIT};
		String selection = "";

		//Adds in the where clause based on if a start and end date were supplied.
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
			String unit=cursor.getString(cursor.getColumnIndex(UNIT));
			WeightTime wt = new WeightTime(date, weight, WeightTime.Unit.valueOf(unit), rowNum);
			results.addLast(wt);
		}
		cursor.close();
		db.close();
		return results;
	}

	/**
	 * Deletes a row from the data table.
	 * @param rowID The rowid of the row to be deleted.
	 * @return The number of rows deleted.
	 */
	public static int deleteRow(Context c, int rowID) {
		SQLiteDatabase db = getWritableDb(c);
		int result = db.delete(DATA_TABLE, UID + "=" + rowID, null);
		db.close();
		return result;
	}

	/**
	 * Updates the specified row with new values.
	 * @param rowID the row to be updated
	 * @param weightTime the new values to put into that row
	 * @return the number of rows affected
	 */
	public static int updateRow(Context c, int rowID, WeightTime weightTime) {
		SQLiteDatabase db = getWritableDb(c);
		ContentValues cv = new ContentValues();
		cv.put(DATE, weightTime.getDate().getTimeInMillis());
		cv.put(WEIGHT, weightTime.getRawWeight());
		cv.put(UNIT, weightTime.getUnit().toString());
		int result = db.update(DATA_TABLE, cv, UID + "=" + rowID, null);
		db.close();
		return result;
	}

	/**
	 * Gets the weight and time information for a specified row
	 * @param rowID the id of the row to get
	 * @return the weight and time information
	 */
	public static WeightTime getRow(Context c, int rowID) {
		WeightTime returnItem = null;
		SQLiteDatabase db=getReadableDb(c);
		String[] columns = { UID, DATE, WEIGHT, UNIT};
		String selection = UID + "=" + rowID;
		Cursor cursor = db.query(DATA_TABLE, columns, selection, null, null, null, DATE);
		while (cursor.moveToNext()) {
			GregorianCalendar date = new GregorianCalendar();
			date.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));
			float weight = cursor.getFloat(cursor.getColumnIndex(WEIGHT));
			String unit=cursor.getString(cursor.getColumnIndex(UNIT));
			returnItem = new WeightTime(date, weight, WeightTime.Unit.valueOf(unit), rowID);
		}
		cursor.close();
		return returnItem;
	}

	/**
	 * Gets the earliest entry in the data table sorted by date
	 * @return the date of the earliest entry
	 */
	public static GregorianCalendar getFirstEntryDate(Context c) {
		SQLiteDatabase db=getReadableDb(c);
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

	/**
	 * Adds a new contact into the default recipient contact table.
	 * @param contact the contact's name and email put into a contact object
	 * @return the row id of the newly inserted row
	 */
	public static long addContact(Context c, Contact contact) {
		SQLiteDatabase db = getWritableDb(c);
		ContentValues cv = new ContentValues();
		cv.put(CONTACT_NAME, contact.getName());
		cv.put(EMAIL_ADDRESS, contact.getEmailAddress());
		long result = db.insertOrThrow(CONTACT_TABLE, null, cv);
		db.close();
		return result;
	}

	/**
	 * Deletes a row from the default contact table.  The email address is used to search for the row
	 * @param emailAddress the email address of the row to be deleted
	 * @return
	 */
	public static int deleteContact(Context c, String emailAddress) {
		SQLiteDatabase db = getWritableDb(c);
		int result = db.delete(CONTACT_TABLE, EMAIL_ADDRESS + "='" + emailAddress + "'", null);
		db.close();
		return result;
	}

	/**
	 * Gets the name and email address for all of the default recipients.  Sorted by the contacts name.
	 * @return a linked list of Contact objects.  Each object contains the name and the email.
	 */
	public static LinkedList<Contact> getAllContacts(Context c) {
		LinkedList<Contact> results = new LinkedList<Contact>();
		SQLiteDatabase db=getReadableDb(c);
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

	/**
	 * Gets only the email addresses of all the default recipients.
	 * @return a linked list with a string for each email address
	 */
	public static LinkedList<String> getAllEmails(Context c) {
		LinkedList<String> results = new LinkedList<String>();
		SQLiteDatabase db=getReadableDb(c);
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
	
	private static SQLiteDatabase getReadableDb(Context c)
	{
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		return helper.getReadableDatabase();
	}
	
	private static SQLiteDatabase getWritableDb(Context c)
	{
		DBHelper helper = new DBHelper(c, DBNAME, VERSION);
		return helper.getWritableDatabase();
	}
}
