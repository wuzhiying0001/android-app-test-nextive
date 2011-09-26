package apptest.dchan;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ModifyRecordActivity extends LogActivity {
	private int mRecordId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button cancel = (Button) findViewById(R.id.cancel);
		cancel.setVisibility(Button.VISIBLE);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mRecordId = getIntent().getIntExtra(DBHelper.UID, -1);
	}

	@Override
	protected void fillInPrevious() {
		WeightTime wt = DBHelper.getRow(this, mRecordId);
		DecimalFormat maxDigitsFormatter = new DecimalFormat("#.#");

		if (Preferences.getUnit(this).equals(WeightTime.Unit.POUND)) {
			mWeightText.setText(maxDigitsFormatter.format((wt.getWeightLB())));
			mWeightSeekbar.setProgress((int) (wt.getWeightLB() * 10));
		} else {
			mWeightText.setText(maxDigitsFormatter.format((wt.getWeightKG())));
			mWeightSeekbar.setProgress((int) (wt.getWeightKG() * 10));
		}
		mDate = wt.getDate();
		updateDisplay();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mSave)) {
			try {
				float weight = Float.parseFloat(mWeightText.getText().toString());

				WeightTime wt = new WeightTime(mDate, weight, Preferences.getUnit(this));
				DBHelper.updateRow(this, mRecordId, wt);

				Preferences.setLastWeight(this, wt.getWeightKG());
				finish();
			} catch (NumberFormatException e) {
				createError(R.string.weight_number);
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
}
