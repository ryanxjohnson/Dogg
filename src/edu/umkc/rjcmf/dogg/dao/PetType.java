package edu.umkc.rjcmf.dogg.dao;

import android.content.ContentValues;
import android.database.Cursor;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao.DataObject;
import edu.umkc.rjcmf.dogg.provider.tables.PetTypeTable;


@DataObject(path = PetTypeTable.URI)
public class PetType extends Dao {

    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    
    @Validate(R.string.error_invalid_pet_type_title)
    @Column(type = Column.STRING, name = TITLE)
    protected String mTitle;
    
    @Validate(R.string.error_invalid_pet_type_description)
    @CanBeEmpty
    @Column(type = Column.STRING, name = DESCRIPTION)
    protected String mDescription;
	
	
    public PetType(ContentValues values) {
        super(values);
    }

    public PetType(Cursor cursor) {
        super(cursor);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }
    
    
    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }
    
}
