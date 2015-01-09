package edu.umkc.rjcmf.dogg.provider.tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;

public class PetsTable extends ContentTable {
	
	// make sure it's globally unique
    private static final int PETS = 40;

    private static final int PET_ID = 41;
    
    public static final String TABLE_NAME = "pets";

    public static final String PETS_URI = "pets/";
    
    public static final Uri BASE_URI = Uri.withAppendedPath(PottyProvider.BASE_URI, PETS_URI);
    
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rjcmf.pets";
    
    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rjcmf.pet";
    
    public static String[] PROJECTION = new String[] {
    	Pet._ID, Pet.TITLE, Pet.DESCRIPTION, Pet.BREED, Pet.PET_TYPE, Pet.DEFAULT_TIME
    };
    
    @Override
    protected Class<? extends Dao> getDaoType() {
        return Pet.class;
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
    
    
    @Override
    public String getType(int type) {
        switch (type) {
            case PETS:
                return CONTENT_TYPE;
            case PET_ID:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    } 
    
    @Override
    public String[] init(boolean isUpgrade) {
        if (isUpgrade) {
            return null;
        }
        return new String [] { // i know, hard-coded strings are bad
        		new InsertBuilder().add(Pet.TITLE, "Taz")
        		.add(Pet.DESCRIPTION, "Mr. Pisser")
        		.add(Pet.DEFAULT_TIME, System.currentTimeMillis())
        		.add(Pet.BREED, "Rott Terrier Mix")
        		.add(Pet.PET_TYPE, 1).build()
        };
    }
    
    @Override
    public long insert(int type, SQLiteDatabase db, ContentValues initialValues) {
        switch (type) {
            case PETS:
                return db.insert(getTableName(), null, initialValues);
        }
        return -1L;
    }
    
    @Override
    public boolean query(int type, Uri uri, SQLiteQueryBuilder queryBuilder, Context context,
            String[] projection) {
        switch (type) {
            case PETS:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                return true;
            case PET_ID:
                queryBuilder.setTables(getTableName());
                queryBuilder.setProjectionMap(buildProjectionMap(PROJECTION));
                queryBuilder.appendWhere(BaseColumns._ID + " = " + uri.getPathSegments().get(1));
                return true;
        }
        return false;
    }
    
    @Override
    public void registerUris() {
        PottyProvider.registerUri(this, PETS_URI, PETS);
        PottyProvider.registerUri(this, PETS_URI + "#", PET_ID);
    }
    
    @Override
    public int update(int match, SQLiteDatabase db, Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        switch (match) {
            case PET_ID:
                return db.update(getTableName(), values, Dao._ID + " = ?", new String[] {
                    uri.getPathSegments().get(1)
                });
            case PETS:
                return db.update(getTableName(), values, selection, selectionArgs);
        }
        return -1;
    }
    
    
    @Override
    public String getDefaultSortOrder() {
        return Pet.DEFAULT_TIME + " desc";
    }

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }
    
    
} // EOC
