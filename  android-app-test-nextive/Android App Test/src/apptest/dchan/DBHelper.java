package apptest.dchan;

import java.util.Date;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper
{
	public final static String DBNAME="weightAppDB";
	public final static String DATA_TABLE="DATA";
	
	public final static String DATE="DATE_VALUE";
	public final static String WEIGHT="WEIGHT";
	public final static String UID="ROWID";
		
	public static int VERSION=1;
	public DBHelper(Context context, String dbName, int version)
	{
		super(context, dbName, null, version);
	}

	/*
	 * Called when the database has been opened. The implementation should check
	 * isReadOnly() before updating the database.
	 */
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		try
		{
			db.execSQL("CREATE TABLE IF NOT EXISTS "+DATA_TABLE+"("+UID+" INTEGER PRIMARY KEY, "+DATE+" DATE, "+WEIGHT+" REAL)");
		}
		catch (SQLException e)
		{
			Log.e("SqliteAndroid", "DBOpenHelper", e);
		}
	}

	/*
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
	}
	public static long insertTimeWeight(Context c, WeightTime weightTime)
	{
		DBHelper helper=new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(DATE, weightTime.getDate().getTime());
		cv.put(WEIGHT, weightTime.getWeightKG());
		long result=db.insertOrThrow(DATA_TABLE, null, cv);
		db.close();
		return result;
	}
	
	public static LinkedList<WeightTime> getWeightTime(Context c, Date startDate, Date endDate)
	{
		LinkedList<WeightTime> results=new LinkedList<WeightTime>();
		DBHelper helper=new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db=helper.getReadableDatabase();
		String[] columns={UID, DATE, WEIGHT};
		String selection="";
		if(startDate!=null)
		{
			selection=selection+DATE+">"+startDate.getTime()+" ";
		}
		if(startDate!=null && endDate!=null)
		{
			selection=selection+" and ";
		}
		if(endDate!=null)
		{
			selection=selection+DATE+">"+startDate.getTime()+" ";
		}
		Cursor cursor=db.query(DATA_TABLE, columns, selection, null, null, null, DATE);
		cursor.getCount();
		while(cursor.moveToNext())
		{
			Date date=new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DATE)));
			double weight=cursor.getDouble(cursor.getColumnIndex(WEIGHT));
			WeightTime wt=new WeightTime(date, weight, WeightTime.KILOGRAM);
			wt.setRowID(cursor.getInt(cursor.getColumnIndex(UID)));
			results.addLast(wt);
		}
		cursor.close();
		db.close();
		return results;
	}
	
	public static int deleteRow(Context c, int rowID)
	{
		DBHelper helper=new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db=helper.getWritableDatabase();
		int result=db.delete(DATA_TABLE, UID+"="+rowID, null);
		db.close();
		return result;
	}
	
	public static int updateRow(Context c, int rowID, WeightTime weightTime)
	{
		DBHelper helper=new DBHelper(c, DBNAME, VERSION);
		SQLiteDatabase db=helper.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(DATE, weightTime.getDate().getTime());
		cv.put(WEIGHT, weightTime.getWeightLB());
		int result=db.update(DATA_TABLE, cv, UID+"="+rowID, null);
		db.close();
		return result;
	}
}
