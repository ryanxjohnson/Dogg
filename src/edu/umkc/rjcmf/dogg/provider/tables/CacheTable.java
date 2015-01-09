package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import edu.umkc.rjcmf.dogg.dao.CachedValue;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;

public class CacheTable extends ContentTable {

	private static final int CACHES = 80;

	private static final int CACHE_ID = 81;

	public static final String URI = "cache";

	public static final Uri BASE_URI = Uri.withAppendedPath(
			PottyProvider.BASE_URI, URI);


	public static final String[] PROJECTION = new String[] { CachedValue._ID,
			CachedValue.ITEM, CachedValue.KEY, CachedValue.VALUE,
			CachedValue.VALID, CachedValue.GROUP, CachedValue.ORDER };

	@Override
	protected Class<? extends Dao> getDaoType() {
		return CachedValue.class;
	}

	@Override
	public String getTableName() {
		return "cache";
	}


	@Override
	public String[] init(boolean isUpgrade) {
		return null;
	}

	@Override
	public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
		switch (type) {
		case CACHES:
			return db.insert(getTableName(), null, initialValues);
		}
		return -1;
	}

	@Override
	public boolean query(int type, Uri uri, SQLiteQueryBuilder queryBuilder,
			Context context, String[] projection) {
		switch (type) {
		case CACHE_ID:
			queryBuilder.appendWhere(CachedValue.KEY + " = "
					+ uri.getPathSegments().get(1));
		case CACHES:
			queryBuilder.setTables(getTableName());
			queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
			return true;
		}
		return false;
	}

	@Override
	public void registerUris() {
		PottyProvider.registerUri(this, URI, CACHES);
		PottyProvider.registerUri(this, URI + "/*", CACHE_ID);
	}

	@Override
	public int update(int match, SQLiteDatabase db, Uri uri,
			ContentValues values, String selection, String[] selectionArgs) {
		switch (match) {
		case CACHE_ID:
			return db.update(getTableName(), values, selection, selectionArgs);
		case CACHES:
			return db.update(getTableName(), values, selection, selectionArgs);
		}
		return -1;
	}

	@Override
	public String[] getProjection() {
		return PROJECTION;
	}

	@Override
	public String getType(int type) {
		// TODO Auto-generated method stub
		return null;
	}

}
