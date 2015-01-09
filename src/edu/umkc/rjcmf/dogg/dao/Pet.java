package edu.umkc.rjcmf.dogg.dao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao.DataObject;
import edu.umkc.rjcmf.dogg.provider.tables.PetsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;

@DataObject(path = PetsTable.PETS_URI)
public class Pet extends Dao {
	
    public static final String TITLE = "title";

    public static final String DESCRIPTION = "description";

    public static final String BREED = "breed";
    
    public static final String PET_TYPE = "pet_type_id";
    
    public static final String DEFAULT_TIME = "default_time";
    
    @Validate(R.string.error_invalid_pet_title)
    @Column(type = Column.STRING, name = TITLE)
    protected String mTitle;
    
    @Validate
    @Nullable
    @Column(type = Column.STRING, name = DESCRIPTION)
    protected String mDescription;

    @Validate(R.string.error_invalid_pet_breed)
    @Column(type = Column.STRING, name = BREED)
    protected String mBreed;
    
    @Validate(R.string.error_invalid_pet_type)
    @Range.Positive
    @Column(type = Column.LONG, name = PET_TYPE)
    protected long mPetType;
    
    @Validate
    @Column(type = Column.LONG, name = DEFAULT_TIME)
    protected long mDefaultTime;

    public Pet(ContentValues values) {
    	super(values);
    }
    
    public Pet(Cursor cursor) {
        super(cursor);
    }
    
    /*
     * Pre-Condition: The Pet ID is not known. (Static State)
     * 
     * Post-Condition: Returns Pet ID or returns null
     */
    public static final Pet loadById(final Context context, final long id) {
        Uri uri = ContentUris.withAppendedId(PetsTable.BASE_URI, id);
        Cursor cursor =
                context.getContentResolver().query(uri, PetsTable.PROJECTION, null, null, null);
        Pet p = null;
        if (cursor.getCount() > 0) {
            p = new Pet(cursor);
        }
        cursor.close();
        if (p == null) {
            throw new IllegalArgumentException("Unable to load pet #" + id);
        }        
        // Invalid Precondition: Cannot return p if there is no pet!
        assert p!= null;        
        return p;
    }
    
    /*
     * Pre-Condition: There must be at least Potty
     * 
     */
    public Potty loadLatestPotty(Context context) {
        Uri uri = PottyTable.BASE_URI;
        String[] projection = PottyTable.PROJECTION;
        Cursor c =
                context.getContentResolver().query(uri, projection, Potty.PET_ID + " = ?",
                        new String[] {
                            String.valueOf(getId())
                        }, Potty.DATE + " desc");

        Potty newest = null;
        if (c.getCount() >= 1) {
            c.moveToFirst();
            newest = new Potty(c);
        }
        c.close();
        
        // Invalid Pre-Condition: C can be null
        assert c != null;
        return newest;
    }
    
    
    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setBreed(String breed) {
        mBreed = breed;
    }
    
    public void setPetType(long petType) {
        mPetType = petType;
    }

    public void setDefaultTime(long defaultTime) {
        mDefaultTime = defaultTime;
    }
    
    // Getters
    
    public String getTitle() {
        if (mTitle.trim().length() == 0) {
            mTitle = String.format("%s", getBreed());
        }
        return mTitle;
    }
      
    public String getDescription() {
        return mDescription;
    }
    
    public String getBreed() {
        return mBreed;
    }
    
    public long getPetType() {
        return mPetType;
    }

    public long getDefaultTime() {
        return mDefaultTime;
    }
        
}
