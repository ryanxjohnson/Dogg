package edu.umkc.rjcmf.dogg.dao;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao.DataObject;
import edu.umkc.rjcmf.dogg.provider.tables.PottyFieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;

@DataObject(path = PottyTable.URI)
public class Potty extends Dao {

	public static final String DATE = "timestamp"; // ms since epoch

	// public static final String TIME = "potty_time"; // this might blow up. it did

	public static final String PET_ID = "pet_id";

	public static final String PEE = "is_pee";

	public static final String POO = "is_poo";

	public static final String COMMENT = "comment";

	public static final String RESTART = "restart"; // for a future version when
													// statistics are included

	@Range.Positive
	@Validate(R.string.error_no_pet_specified)
	@Column(type = Column.INTEGER, name = PET_ID)
	protected long mPetId;

	@Past
	@Validate(R.string.error_date_in_past)
	@Column(type = Column.TIMESTAMP, name = DATE)
	protected Date mDate;

	@Validate
	@Column(type = Column.BOOLEAN, name = PEE, value = 0)
	protected boolean mIsPee;

	@Validate
	@Column(type = Column.BOOLEAN, name = POO, value = 0)
	protected boolean mIsPoo;

	@Validate
	@Column(type = Column.BOOLEAN, name = RESTART)
	protected boolean mIsRestart;

	private final ArrayList<PottyField> mFields = new ArrayList<PottyField>();

	private Potty mNext = null;

	private Potty mPrevious = null;

	public Potty(ContentValues contentValues) {
		super(contentValues);
	}

	public Potty(Cursor cursor) {
		super(cursor);
	}

	@Override
	protected void preValidate() {
		if (mDate == null) {
			mDate = new Date(System.currentTimeMillis());
		}
	}

	public Potty loadPrevious(Context context) {
		Potty previous = null;
		if (!mIsRestart) {
			Uri uri = PottyTable.BASE_URI;
			String[] projection = PottyTable.PROJECTION;
			Cursor c = context.getContentResolver().query(
					uri,
					projection,
					Potty.PET_ID + " = ? AND " + DATE + " < ?",
					new String[] { String.valueOf(getPetId()),
							String.valueOf(getTimestamp()) },
					Potty.DATE + " desc");
			if (c.getCount() >= 1) {
				c.moveToFirst();
				previous = new Potty(c);
			}
			c.close();
		}
		return previous;
	}

	public Potty loadNext(Context context) {
		Potty next = null;
		if (!mIsRestart) {
			Uri uri = PottyTable.BASE_URI;
			String[] projection = PottyTable.PROJECTION;
			Cursor c = context.getContentResolver().query(
					uri,
					projection,
					Potty.PET_ID + " = ? AND " + DATE + " > ?",
					new String[] { String.valueOf(getPetId()),
							String.valueOf(getTimestamp()) },
					Potty.DATE + " asc");
			if (c.getCount() >= 1) {
				c.moveToFirst();
				next = new Potty(c);
			}
			c.close();
		}
		return next;
	}

	public ArrayList<PottyField> getFields() {
		return mFields;
	}

	/**
	 * Trying out some threading/async
	 * 
	 * @param context
	 * @return
	 */
	public ArrayList<PottyField> getFields(Context context) {
		if (mFields.size() == 0) {
			Uri uri = ContentUris.withAppendedId(
					PottyFieldsTable.POTTIES_FIELDS_URI, getId());
			Cursor c = context.getContentResolver().query(uri,
					PottyFieldsTable.PROJECTION, null, null, null);
			if (c.getCount() > 0) {
				c.moveToFirst();
				PottyField field = new PottyField(c);
				mFields.add(field);
			}
			c.close();
		}
		return mFields;
	}

	public Potty getNext() {
		return mNext;
	}

	public Potty getPrevious() {
		return mPrevious;
	}

	public long getTimestamp() {
		if (mDate == null) {
			mDate = new Date();
		}
		return mDate.getTime();
	}

	public long getPetId() {
		return mPetId;
	}

	public boolean hasNext() {
		return mNext != null;
	}

	public boolean hasPrevious() {
		return mPrevious != null;
	}

	public boolean isRestart() {
		return mIsRestart;
	}

	// Future: Either pee or poo must be set. Cannot be a blank potty event
	public boolean isPee() {
		return mIsPee;
	}

	public boolean isPoo() {
		return mIsPoo;
	}

	public void setFields(ArrayList<PottyField> fields) {
		mFields.clear();
		mFields.addAll(fields);
	}

	public void setNext(Potty next) {
		mNext = next;
	}

	public void setPee(boolean pee) {
		mIsPee = pee;
	}

	public void setPoo(boolean poo) {
		mIsPoo = poo;
	}

	public void setPrevious(Potty previous) {
		mPrevious = previous;
	}

	public void setRestart(boolean restart) {
		mIsRestart = restart;
	}

	public void setTimestamp(long timestamp) {
		if (mDate == null) {
			mDate = new Date(timestamp);
		} else {
			mDate.setTime(timestamp);
		}
	}

	public void setPetId(long id) {
		mPetId = id;
	}

}
