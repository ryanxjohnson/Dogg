package edu.umkc.rjcmf.dogg.dao;

import android.content.ContentValues;
import android.database.Cursor;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.dao.Dao.DataObject;
import edu.umkc.rjcmf.dogg.provider.tables.FieldsTable;



@DataObject(path = FieldsTable.URI_PATH)
public class Field extends Dao {
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type"; // only text for now

    @Validate(R.string.error_invalid_field_title)
    @Column(type = Column.STRING, name = TITLE)
    protected String mTitle;

    @Validate(R.string.error_invalid_field_description)
    @Column(type = Column.STRING, name = DESCRIPTION)
    @CanBeEmpty
    protected String mDescription;

    @Validate
    @Nullable
    @Column(type = Column.STRING, name = TYPE)
    protected String mType; // TODO(3.2) - Add the ability to set field types

    public Field(ContentValues values) {
        super(values);
    }

    public Field(Cursor cursor) {
        super(cursor);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getType() {
        return mType;
    }
}
