package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.PottyField;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;

public class PottyFieldsTable extends ContentTable {

	// make sure it's globally unique
    // TODO: rename these because it's confusing as fuck
    private static final int POTTY_FIELDS = 20;

    private static final int POTTY_FIELD = 21;

    private static final int POTTIES_FIELDS = 22;

    public static final String TABLE_NAME = "potty_fields";
    
    /**
     * Given a potty ID, return all of the fields that were saved on that
     * potty
     */
    public static final String POTTIES_FIELDS_PATH = "potties/fields";
    
    public static final Uri POTTIES_FIELDS_URI = Uri.withAppendedPath(PottyProvider.BASE_URI,
            POTTIES_FIELDS_PATH);
    
    /**
     * Given a field ID, return the field
     */
    public static final String POTTIES_FIELD_PATH = "potties/field";
    
    private static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.rjcmf.potty_fields";
    
    private static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.rjcmf.potty_field_id";

    private static final String CONTENT_ITEMS_TYPE =
            "vnd.android.cursor.dir/vnd.rjcmf.potties_fields";
    
    public static final String[] PROJECTION = new String[] {
    	PottyField._ID, PottyField.POTTY_ID, PottyField.TEMPLATE_ID, PottyField.VALUE
    };
    
    @Override
    protected Class<? extends Dao> getDaoType() {
        return PottyField.class;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getDefaultSortOrder() {
        return PottyField.TEMPLATE_ID + " desc";
    }
    
    @Override
    public String getType(int type) {
        switch (type) {
            case POTTY_FIELD:
                return CONTENT_ITEM_TYPE;
            case POTTY_FIELDS:
                return CONTENT_TYPE;
            case POTTIES_FIELDS:
                return CONTENT_ITEMS_TYPE;
        }
        return null;
    }

    @Override
    public String[] init(boolean isUpgrade) {
        return null;
    }
    
    
    
    @Override
    public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
        switch (type) {
            case POTTY_FIELDS:
            case POTTIES_FIELDS:
                return db.insert(getTableName(), null, initialValues);
        }
        return -1L;
    }
    
    @Override
    public boolean query(int type, Uri uri, SQLiteQueryBuilder queryBuilder, Context context,
            String[] projection) {
        switch (type) {
            case POTTIES_FIELDS:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                return true;
            case POTTY_FIELDS:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                queryBuilder.appendWhere(PottyField.POTTY_ID + " = "
                        + uri.getPathSegments().get(2));
                return true;
            case POTTY_FIELD:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                queryBuilder.appendWhere(Dao._ID + " = " + uri.getPathSegments().get(2));
                return true;
        }
        return false;
    }
    
    //clean this up....
    @Override
    public void registerUris() { 
        PottyProvider.registerUri(this, POTTIES_FIELDS_PATH, POTTIES_FIELDS);
        PottyProvider.registerUri(this, POTTIES_FIELDS_PATH + "/#", POTTY_FIELDS);
        PottyProvider.registerUri(this, POTTIES_FIELD_PATH + "/#", POTTY_FIELD);
    }
    
    @Override
    public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        switch (match) {
            case POTTY_FIELD:
                return db.update(getTableName(), values, Dao._ID + " = ?", new String[] {
                    values.getAsString(Dao._ID)
                });
            case POTTIES_FIELDS:
                if (values.containsKey(BaseColumns._ID)) {
                    values.remove(BaseColumns._ID);
                }
                return db.update(getTableName(), values, selection, selectionArgs);
        }
        return -1;
    }
    
    @Override
    public String[] getProjection() {
        return PROJECTION;
    }   
    
} 
