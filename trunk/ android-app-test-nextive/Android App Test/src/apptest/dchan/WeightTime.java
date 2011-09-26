package apptest.dchan;

import java.text.DecimalFormat;
import java.util.GregorianCalendar;
/*
 * Class to store the weight and dates as a pair.
 */
public class WeightTime {
	public enum Unit{
		KILOGRAM,
		POUND
	}
	private int mRowId;
	private GregorianCalendar mDate;
	private float mWeight;
	private static DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
	private Unit mCurrentUnit;
	public WeightTime(GregorianCalendar _date, float _weight, Unit unit) {
		mCurrentUnit=unit;
		mDate = _date;
		mWeight = _weight;
	}

	public WeightTime(GregorianCalendar _date, float _weight, Unit unit, int _rowID) {
		mCurrentUnit=unit;
		mDate = _date;
		mWeight = _weight;
		mRowId = _rowID;
	}

	public int getRowID() {
		return mRowId;
	}

	public void setRowID(int _rowID) {
		mRowId = _rowID;
	}

	public GregorianCalendar getDate() {
		return mDate;
	}

	public void setDate(GregorianCalendar date) {
		this.mDate = date;
	}

	public float getWeightKG() {
		if(mCurrentUnit==Unit.KILOGRAM){
			return mWeight;
		}
		return lbsToKgs(mWeight);
	}

	public float getWeightLB() {
		if(mCurrentUnit==Unit.POUND){
			return mWeight;
		}
		return kgsToLbs(mWeight);
	}

	public float getRawWeight()
	{
		return mWeight;
	}
	
	public Unit getUnit()
	{
		return mCurrentUnit;
	}
	
	public String getWeightKGString() {
		if(mCurrentUnit==Unit.KILOGRAM){
			return maxDigitsFormatter.format(mWeight);
		}
		return maxDigitsFormatter.format(lbsToKgs(mWeight));
	}

	public String getWeightLBString() {
		if(mCurrentUnit==Unit.POUND){
			return maxDigitsFormatter.format(mWeight);
		}
		return maxDigitsFormatter.format(kgsToLbs(mWeight));
	}

	public void setWeight(float _weight, Unit unit) {
		mCurrentUnit=unit;
		mWeight = _weight;
	}

	public static float lbsToKgs(float lbs) {
		return (float) (lbs * .45359237);
	}

	public static float kgsToLbs(float kgs) {
		return (float) (kgs * 2.20452262);
	}
}
