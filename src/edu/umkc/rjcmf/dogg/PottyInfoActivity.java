package edu.umkc.rjcmf.dogg;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.dao.Potty;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.views.FormattedDateView;

@SuppressWarnings("unused")
public class PottyInfoActivity extends Activity implements OnClickListener {

	private static final String TAG = "PottyInfoActivity";

	private final SparseArray<Holder> mLayouts = new SparseArray<Holder>();

	//private PottyInfoTask mInfoTask;

	private Potty mPotty;

	private Pet mPet;

	
	private LayoutInflater mInflater;

	// private LinearLayout mStatContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.potty_info);

		mInflater = LayoutInflater.from(this);

		Button previous = (Button) findViewById(R.id.previous);
		Button next = (Button) findViewById(R.id.next);

		// next.setOnClickListener(this);
		// previous.setOnClickListener(this);
		// findViewById(R.id.edit).setOnClickListener(this);

		long id = getIntent().getLongExtra(BaseFormActivity.EXTRA_ITEM_ID, -1);
		Uri uri = ContentUris.withAppendedId(PottyTable.BASE_URI, id);
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);

		 //mPotty = new Potty(cursor);
		// cursor.close();

		// mPet = Pet.loadById(this, mPotty.getPetId());
		// mPotty.setPrevious(mPotty.loadPrevious(this));
		// mPotty.setNext(mPotty.loadNext(this));

		// previous.setEnabled(mPotty.hasPrevious());
		// next.setEnabled(mPotty.hasNext());

		FormattedDateView header = (FormattedDateView) findViewById(R.id.header);
		header.setText("4/19/2014");

		setTitle(getString(R.string.title_potty, header.getText()));

		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	private static final class Holder {
		public final ViewGroup view;

		public Holder(ViewGroup view) {
			this.view = view;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// mInfoTask = new PottyInfoTask(mPotty);
		// mInfoTask.attach(this);
		// mInfoTask.execute();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.edit:
			// intent = new Intent(this, PottyActivity.class);
			// intent.putExtra(BaseFormActivity.EXTRA_ITEM_ID,
			// getIntent().getLongExtra(BaseFormActivity.EXTRA_ITEM_ID, -1));
			// startActivity(intent);
			break;

		case R.id.previous:
			// intent = new Intent(this, PottyInfoActivity.class);
			// intent.putExtra(BaseFormActivity.EXTRA_ITEM_ID,
			// mPotty.getPrevious().getId());
			// startActivity(intent);
			// finish();
			// Overrider.get(this).overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			break;

		case R.id.next:
			// intent = new Intent(this, PottyInfoActivity.class);
			// intent.putExtra(BaseFormActivity.EXTRA_ITEM_ID,
			// mPotty.getNext().getId());
			// startActivity(intent);
			// finish();
			// Overrider.get(this).overridePendingTransition(R.anim.slide_in_left,
			// R.anim.slide_out_right);
			break;
		}
	} // end of onClick

//	public void addInformation(DataHolder update) {
//		Holder holder = mLayouts.get(update.key);
//		if (holder == null)
//			return;
//
//		ViewGroup view = holder.view;
//		TextView stat = (TextView) view.findViewById(android.R.id.text2);
//		stat.setText("Place Holder"); 
//	}

	private static class Overrider {
		public static Overrider get(Activity activity) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
				return new RealOverrider(activity);
			}
			return new Overrider(activity);
		}

		protected final Activity mActivity;

		public Overrider(Activity activity) {
			mActivity = activity;
		}

		public void overridePendingTransition(int in, int out) {
		}

		private static class RealOverrider extends Overrider {
			public RealOverrider(Activity activity) {
				super(activity);
			}

			@Override
			public void overridePendingTransition(int in, int out) {
				mActivity.overridePendingTransition(in, out);
			}
		}
	}

}
