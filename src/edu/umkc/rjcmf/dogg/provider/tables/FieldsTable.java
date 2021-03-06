package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Field;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;

public class FieldsTable extends ContentTable {
	// make sure it's globally unique
    private static final int FIELDS = 30;

    private static final int FIELD_ID = 31;

    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rjcmf.fields";

    private static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.rjcmf.field_id";

    /**
     * All saved field templates
     */
    public static final String URI_PATH = "fields/";

    public static final Uri URI = Uri.withAppendedPath(PottyProvider.BASE_URI, URI_PATH);

    public static final String[] PROJECTION = new String[] {
            Field._ID, Field.TITLE, Field.DESCRIPTION, Field.TYPE
    };

    @Override
    protected Class<? extends Dao> getDaoType() {
        return Field.class;
    }

    @Override
    public String getDefaultSortOrder() {
        return Field.TITLE + " asc";
    }

    @Override
    public String getTableName() {
        return "fields";
    }

    @Override
    public String getType(int type) {
        switch (type) {
            case FIELDS:
                return CONTENT_TYPE;
            case FIELD_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public String[] init(boolean isUpgrade) {
        //  Hardcoded strings are bad!
        return new String[] {
            new InsertBuilder().add(Field.TITLE, "Comment")
                    .add(Field.DESCRIPTION, "Comment about the potty.").build()
        };
    }

    @Override
    public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
        switch (type) {
            case FIELDS:
                return db.insert(getTableName(), null, initialValues);
        }
        return -1L;
    }

    @Override
    public boolean query(int type, Uri uri, SQLiteQueryBuilder queryBuilder, Context context,
            String[] projection) {
        switch (type) {
            case FIELDS:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                return true;
            case FIELD_ID:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                queryBuilder.appendWhere(BaseColumns._ID + " = " + uri.getPathSegments().get(1));
                return true;
        }
        return false;
    }

    @Override
    public void registerUris() {
        PottyProvider.registerUri(this, URI_PATH, FIELDS);
        PottyProvider.registerUri(this, URI_PATH + "#", FIELD_ID);
    }

    @Override
    public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        if (selection == null) {
            selection = "";
        }
        if (selectionArgs == null) {
            selectionArgs = new String[0];
        }
        switch (match) {
            case FIELD_ID:
                String query =
                        Dao._ID + " = ?"
                                + (TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ")");
                String[] args = new String[selectionArgs.length + 1];
                args[0] = values.getAsString(Dao._ID);
                for (int i = 0; i < selectionArgs.length; i++) {
                    args[i + 1] = selectionArgs[i];
                }
                return db.update(getTableName(), values, query, args);
            case FIELDS:
                return db.update(getTableName(), values, selection, selectionArgs);
        }
        return -1;
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }
}
