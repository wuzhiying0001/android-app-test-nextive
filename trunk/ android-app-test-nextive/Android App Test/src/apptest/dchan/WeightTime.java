package apptest.dchan;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class WeightTime {
	public final static String KILOGRAM="KILOGRAM";
	public final static String POUND="POUND";
	private int rowID;
	private GregorianCalendar date;
	//private Date date;
	private float weight;
	private static DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
	public WeightTime(GregorianCalendar _date, float _weight, String unit)
	{
		if(unit.equals(POUND))
		{
			_weight=lbsToKgs(_weight);
		}
		date=_date;
		weight=_weight;
	}
	public WeightTime(GregorianCalendar _date, float _weight, String unit, int _rowID)
	{
		if(unit.equals(POUND))
		{
			_weight=lbsToKgs(_weight);
		}
		date=_date;
		weight=_weight;
		rowID=_rowID;
	}
	public int getRowID()
	{
		return rowID;
	}
	public void setRowID(int _rowID)
	{
		rowID=_rowID;
	}
	public GregorianCalendar getDate() {
		return date;
	}
	public void setDate(GregorianCalendar date) {
		this.date = date;
	}
	public float getWeightKG() {
		return weight;
	}
	public float getWeightLB() {
		return kgsToLbs(weight);
	}
	public String getWeightKGString() {
		return maxDigitsFormatter.format(weight);
	}
	public String getWeightLBString() {
		return maxDigitsFormatter.format(kgsToLbs(weight));
	}
	public void setWeight(float _weight, String unit) {
		if(unit.equals(POUND))
		{
			_weight=lbsToKgs(_weight);
		}
		weight = _weight;
	}
	public static float lbsToKgs(float lbs)
	{
		return (float) (lbs*.45359237);
	}
	public static float kgsToLbs(float kgs)
	{
		return (float) (kgs*2.20452262);
	}
}
