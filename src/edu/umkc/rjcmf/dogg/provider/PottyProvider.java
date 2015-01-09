package edu.umkc.rjcmf.dogg.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import edu.umkc.rjcmf.dogg.provider.tables.CacheTable;
import edu.umkc.rjcmf.dogg.provider.tables.ContentTable;
import edu.umkc.rjcmf.dogg.provider.tables.FieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PetTypeTable;
import edu.umkc.rjcmf.dogg.provider.tables.PetsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyFieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.util.Debugger;

public class PottyProvider extends ContentProvider {

	public static final String AUTHORITY = "edu.umkc.rjcmf.dogg";

	public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

	public static final int DATABASE_VERSION = 6;

	public static final ArrayList<ContentTable> TABLES = new ArrayList<ContentTable>();

	private static final SparseIntArray LOOKUP = new SparseIntArray();

	public static final String DATABASE_NAME = "dogg.db";

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final String TAG = "PottyProvider";

	private DatabaseHelper mDatabaseHelper;

	// static class here, add new tables to the database

	static {
		TABLES.add(new FieldsTable());
		TABLES.add(new PottyFieldsTable());
		TABLES.add(new PottyTable());
		TABLES.add(new PetsTable());
		TABLES.add(new PetTypeTable());
		TABLES.add(new CacheTable());

		/*
		 * Service Intervals
		 */

		for (ContentTable table : TABLES) {
			table.registerUris();
		}
	}

	public static void registerUri(ContentTable table, String path, int code) {
		URI_MATCHER.addURI(AUTHORITY, path, code);
		int position = TABLES.indexOf(table);
		if (position < 0) {
			TABLES.add(table);
			position = TABLES.size() - 1;
		}
		LOOKUP.put(code, position);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, "Creating database");
			for (ContentTable table : TABLES) {
				try {
					String sql = table.create();
					if (sql != null) {
						db.execSQL(sql);
					}
				} catch (IllegalArgumentException e) {
					Log.e("DatabaseHelper", "Could not create table", e);
				} catch (IllegalAccessException e) {
					Log.e("DatabaseHelper", "Could not create table", e);
				}
			}

			initTables(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, final int oldVersion,
				final int newVersion) {
			DatabaseUpgrader.upgradeDatabase(db); // DO I NEED TO EVER UPGRADE?
		}
	}

	public static void initTables(SQLiteDatabase db) {
		for (ContentTable table : TABLES) {
			String[] init = table.init(false);
			if (init != null) {
				for (String sql : init) {
					db.execSQL(sql);
				}
			}
		}
	}

	@Override
	public boolean onCreate() {
		mDatabaseHelper = new DatabaseHelper(getContext());
		URI_MATCHER.addURI(AUTHORITY, "reset/", 0);
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Debugger.checkQueryOnUiThread(getContext());
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

		int count = -1;
		final int type = URI_MATCHER.match(uri);
		int position = LOOKUP.get(type, -1);
		if (position >= 0) {
			count = TABLES.get(position).delete(db, uri, selection,
					selectionArgs);
		}

		if (count < 0) {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		notifyListeners(uri);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		final int type = URI_MATCHER.match(uri);
		if (type == 0) {
			// TODO(3.1) - Figure this out.
			// mDatabaseHelper.close();
			// mDatabaseHelper.getReadableDatabase();
			// Log.d(TAG, "Database closed!");
			return null;
		}
		String result = null;
		for (ContentTable table : TABLES) {
			result = table.getType(type);
			if (result != null) {
				return result;
			}
		}
		throw new IllegalArgumentException("Unknown URI: " + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Debugger.checkQueryOnUiThread(getContext());
		final int match = URI_MATCHER.match(uri);
		long newId = -1L;
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
		int position = LOOKUP.get(match, -1);
		if (position >= 0) {
			newId = TABLES.get(position).insert(match, db, initialValues);
			if (newId >= 0) {
				uri = ContentUris.withAppendedId(uri, newId);
				notifyListeners(uri);
			}
			return uri;
		}
		throw new IllegalArgumentException("Unknown URI: " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Debugger.checkQueryOnUiThread(getContext());
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		final int match = URI_MATCHER.match(uri);
		boolean changed = false;
		ContentTable queryTable = null;
		int position = LOOKUP.get(match, -1);
		if (position >= 0) {
			ContentTable table = TABLES.get(position);
			changed = table.query(match, uri, qb, getContext(), projection);
			if (changed) {
				queryTable = table;
			}
		}

		if (!changed) {
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		if (projection == null) {
			projection = queryTable.getProjection();
		}

		String orderBy = TextUtils.isEmpty(sortOrder) ? queryTable
				.getDefaultSortOrder() : sortOrder;

		SQLiteDatabase db;
		try {
			db = mDatabaseHelper.getReadableDatabase();
		} catch (SQLiteException e) {
			db = mDatabaseHelper.getWritableDatabase();
		}

		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Debugger.checkQueryOnUiThread(getContext());
		final int match = URI_MATCHER.match(uri);
		int position = LOOKUP.get(match, -1);
		if (position >= 0) {
			SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
			int count = TABLES.get(position).update(match, db, uri, values,
					selection, selectionArgs);
			if (count >= 0) {
				notifyListeners(uri);
			}
			return count;
		}
		throw new IllegalArgumentException("Unknown URI: " + uri);
	}

	private void notifyListeners(final Uri uri) {
		Context context = getContext();
		context.getContentResolver().notifyChange(uri, null);

		// AutomaticBackupService.run(context);
	}
}
