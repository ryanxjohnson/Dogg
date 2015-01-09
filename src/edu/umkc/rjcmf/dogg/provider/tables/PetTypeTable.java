package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.PetType;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;




public class PetTypeTable extends ContentTable {

    // make sure it's globally unique
    private static final int TYPES = 50;

    private static final int TYPE_ID = 51;

    public static final String URI = "pets/types/";
	
    public static final Uri BASE_URI = Uri.withAppendedPath(PottyProvider.BASE_URI, URI);
	
    private static final String CONTENT_TYPE =
            "vnd.android.cursor.dir/vnd.rjcmf.pet_types";

    private static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/vnd.rjcmf.pet_type";
	
    public static final String[] PROJECTION = new String[] {
        PetType._ID, PetType.TITLE, PetType.DESCRIPTION
};
	
    @Override
    protected Class<? extends Dao> getDaoType() {
        return PetType.class;
    }

    @Override
    public String getTableName() {
        return "pet_types";
    }	
	
	
    @Override
    public String getType(int type) {
        switch (type) {
            case TYPES:
                return CONTENT_TYPE;
            case TYPE_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }
	
	
    @Override
    public String[] init(boolean isUpgrade) {
        // remove hardcoded strings
        return new String[] {
            new InsertBuilder().add(PetType.TITLE, "Dog")
                    .add(PetType.DESCRIPTION, "Canine").build()
        };
    }
	
    @Override
    public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
        switch (type) {
            case TYPES:
                return db.insert(getTableName(), null, initialValues);
        }
        return -1L;
    }
	
    @Override
    public boolean query(int type, Uri uri, SQLiteQueryBuilder queryBuilder, Context context,
            String[] projection) {
        switch (type) {
            case TYPES:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                return true;
            case TYPE_ID:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                queryBuilder.appendWhere(Dao._ID + " = " + uri.getPathSegments().get(2));
                return true;
        }
        return false;
    }
	
    @Override
    public void registerUris() {
        PottyProvider.registerUri(this, URI, TYPES);
        PottyProvider.registerUri(this, URI + "#", TYPE_ID);
    }
	
    @Override
    public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        switch (match) {
            case TYPE_ID:
                return db.update(getTableName(), values, Dao._ID + " = ?", new String[] {
                    values.getAsString(Dao._ID)
                });
            case TYPES:
                return db.update(getTableName(), values, selection, selectionArgs);
        }
        return -1;
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }
	
} //EOC
