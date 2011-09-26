package apptest.dchan;

import java.text.DecimalFormat;
import java.util.GregorianCalendar;
/*
 * Class to store the weight and dates as a pair.
 */
public class WeightTime {
	public final static String KILOGRAM = "KILOGRAM";
	public final static String POUND = "POUND";
	private int mRowId;
	private GregorianCalendar mDate;
	private float mWeight;
	private static DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");

	public WeightTime(GregorianCalendar _date, float _weight, String unit) {
		if (unit.equals(POUND)) {
			_weight = lbsToKgs(_weight);
		}
		mDate = _date;
		mWeight = _weight;
	}

	public WeightTime(GregorianCalendar _date, float _weight, String unit, int _rowID) {
		if (unit.equals(POUND)) {
			_weight = lbsToKgs(_weight);
		}
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
		return mWeight;
	}

	public float getWeightLB() {
		return kgsToLbs(mWeight);
	}

	public String getWeightKGString() {
		return maxDigitsFormatter.format(mWeight);
	}

	public String getWeightLBString() {
		return maxDigitsFormatter.format(kgsToLbs(mWeight));
	}

	public void setWeight(float _weight, String unit) {
		if (unit.equals(POUND)) {
			_weight = lbsToKgs(_weight);
		}
		mWeight = _weight;
	}

	public static float lbsToKgs(float lbs) {
		return (float) (lbs * .45359237);
	}

	public static float kgsToLbs(float kgs) {
		return (float) (kgs * 2.20452262);
	}
}
