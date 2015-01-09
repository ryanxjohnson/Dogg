package edu.umkc.rjcmf.dogg.adapters;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.dao.Potty;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.views.FormattedDateView;

public class PottyAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private static final String TAG = "PottyAdapter";

	private static final String[] PROJECTION = PottyTable.PROJECTION;

	private final Context mContext;

	private Cursor mCursor;

	private Pet mPet;

	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			requery();
		}
	};

	public PottyAdapter(Context context, Pet pet) {
		mContext = context;
		mContext.getContentResolver().registerContentObserver(
				PottyTable.BASE_URI, true, mObserver);
		setPet(pet);
	}

	public void setPet(Pet pet) {
		mPet = pet;
		if (mCursor != null && !mCursor.isClosed()) {
			mCursor.close();
		}
		mCursor = mContext.getContentResolver().query(PottyTable.BASE_URI,
				PROJECTION, Potty.PET_ID + " = ?",
				new String[] { String.valueOf(mPet.getId()) },
				Potty.DATE + " DESC");

	}

	@SuppressWarnings("deprecation")
	public void requery() {
		mCursor.requery();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mCursor.getCount();
	}

	@Override
	public Object getItem(int position) {
		if (mCursor != null && mCursor.moveToPosition(position)) {
			return new Potty(mCursor);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		if (mCursor != null && mCursor.moveToPosition(position)) {
			return mCursor.getLong(mCursor.getColumnIndex(Dao._ID));
		}
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		mCursor.moveToPosition(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.potty_list_item, parent, false);
		}

		Holder holder = (Holder) convertView.getTag();
		if (holder == null) {
			holder = new Holder(convertView);
		}
		String value;
		// date view
		holder.dateView.setText(mCursor.getString(mCursor
				.getColumnIndex(Potty.DATE)));
		
		// time view
		holder.timeView.setText(mCursor.getString(mCursor
				.getColumnIndex(Potty.DATE)));

		// show if Peed or Pooped
		if (mCursor.getInt(mCursor.getColumnIndex(Potty.PEE)) == 1) {
			value = mContext.getString(R.string.peed);
			holder.peeField.setText(value);
		} else if (mCursor.getInt(mCursor.getColumnIndex(Potty.PEE)) == 0) {
			value = "-  ";
			holder.peeField.setText(value);
		}

		if (mCursor.getInt(mCursor.getColumnIndex(Potty.POO)) == 1) {
			value = mContext.getString(R.string.pooped);
			holder.pooField.setText(value);
		} else if (mCursor.getInt(mCursor.getColumnIndex(Potty.POO)) == 0) {
			value = "-  ";
			holder.pooField.setText(value);
		}

		return convertView;
	}

	private static class Holder {
		public final FormattedDateView dateView;
		public final TextView peeField;
		public final TextView pooField;
		public final FormattedDateView timeView;

		

		public Holder(View convertView) {
			dateView = (FormattedDateView) convertView
					.findViewById(android.R.id.text1);
			
			
			
			timeView = (FormattedDateView) convertView
					.findViewById(android.R.id.text2);
			
			peeField = (TextView) convertView.findViewById(R.id.pee_field);
			pooField = (TextView) convertView.findViewById(R.id.poo_field);
			convertView.setTag(this);
			


		}
	}

	public Context getContext() {
		return mContext;
	}

}
