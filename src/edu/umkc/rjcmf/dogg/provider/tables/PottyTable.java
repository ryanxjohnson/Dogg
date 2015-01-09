package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Potty;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;

public class PottyTable extends ContentTable {

	private static final int POTTIES = 10;

	private static final int POTTY_ID = 11;

	private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rjcmf.potty";

	private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rjcmf.potty";

	public static final String TABLE_NAME = "potties";

	public static final String URI = "potties/";

	public static final Uri BASE_URI = Uri.withAppendedPath(
			PottyProvider.BASE_URI, URI);

	public static final String[] PROJECTION = new String[] { Potty._ID,
			Potty.PET_ID, Potty.DATE, Potty.PEE, Potty.POO};

	// this is where my string arrays go
	public static final String[] CSV_PROJECTION = new String[] { Potty._ID,
			Potty.PET_ID, Potty.DATE, Potty.PEE, Potty.POO };

	public static final int[] PLAINTEXT = new int[] { R.string.column_id,
			R.string.column_pet, R.string.column_date, R.string.column_pee,
			R.string.column_poo };

	@Override
	protected Class<? extends Dao> getDaoType() {
		return Potty.class;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public void registerUris() {
		PottyProvider.registerUri(this, URI, POTTIES);
		PottyProvider.registerUri(this, URI + "#", POTTY_ID);
	}

	@Override
	public String getType(final int type) {
		switch (type) {
		case POTTIES:
			return CONTENT_TYPE;
		case POTTY_ID:
			return CONTENT_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
		switch (type) {
		case POTTIES:
			return db.insert(getTableName(), null, initialValues);
		}
		return -1L;
	}

	@Override
	public boolean query(final int type, Uri uri,
			SQLiteQueryBuilder queryBuilder, Context context,
			String[] projection) {
		switch (type) {
		case POTTY_ID:
			queryBuilder.appendWhere(TABLE_NAME + "." + Dao._ID + " = "
					+ uri.getPathSegments().get(1));
		case POTTIES:
			queryBuilder.setTables(getTableName());
			queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
			return true;
		}
		return false;
	}

	@Override
	public int update(int match, SQLiteDatabase db, Uri uri,
			ContentValues values, String selection, String[] selectionArgs) {
		switch (match) {
		case POTTIES:
			return db.update(getTableName(), values, selection, selectionArgs);
		case POTTY_ID:
			String pottyId = uri.getPathSegments().get(1);
			String clause = Dao._ID
					+ " = "
					+ pottyId
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ")" : "");
			return db.update(getTableName(), values, clause, selectionArgs);
		}
		return -1;
	}

	@Override
	public String[] init(boolean isUpgrade) {
		return null;
	}

	@Override
	public String[] getProjection() {
		return PROJECTION;
	}

	@Override
	public String getDefaultSortOrder() {
		return Potty.DATE + " desc";
	}
}
