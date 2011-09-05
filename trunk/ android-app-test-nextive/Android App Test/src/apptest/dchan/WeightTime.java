package apptest.dchan;

import java.text.DecimalFormat;
import java.util.Date;

public class WeightTime {
	public final static String KILOGRAM="KILOGRAM";
	public final static String POUND="POUND";
	private int rowID;
	private Date date;
	private double weight;
	private static DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");
	public WeightTime(Date _date, double _weight, String unit)
	{
		if(unit.equals(POUND))
		{
			_weight=lbsToKgs(_weight);
		}
		date=_date;
		weight=_weight;
	}
	public int getRowID()
	{
		return rowID;
	}
	public void setRowID(int _rowID)
	{
		rowID=_rowID;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public double getWeightKG() {
		return weight;
	}
	public double getWeightLB() {
		return kgsToLbs(weight);
	}
	public String getWeightKGString() {
		return maxDigitsFormatter.format(weight);
	}
	public String getWeightLBString() {
		return maxDigitsFormatter.format(kgsToLbs(weight));
	}
	public void setWeight(double _weight, String unit) {
		if(unit.equals(POUND))
		{
			_weight=lbsToKgs(_weight);
		}
		weight = _weight;
	}
	private double lbsToKgs(double lbs)
	{
		return lbs/2.2;
	}
	private double kgsToLbs(double kgs)
	{
		return kgs*2.2;
	}
}
