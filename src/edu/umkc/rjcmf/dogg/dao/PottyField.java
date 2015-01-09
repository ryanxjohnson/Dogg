package edu.umkc.rjcmf.dogg.dao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import edu.umkc.rjcmf.dogg.R;
import edu.umkc.rjcmf.dogg.exceptions.InvalidFieldException;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;
import edu.umkc.rjcmf.dogg.provider.tables.FieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyFieldsTable;

public class PottyField extends Dao {
	
    public static final String POTTY_ID = "potty_id";
    public static final String TEMPLATE_ID = "template_id";
    public static final String VALUE = "value"; //this is the comment?
	
    @Validate(R.string.error_invalid_template_id)
    @Range.Positive
    @Column(type = Column.LONG, name = TEMPLATE_ID)
    protected long mTemplateId;
    
    @Validate(R.string.error_invalid_potty_id)
    @Range.Positive
    @Column(type = Column.LONG, name = POTTY_ID)
    protected long mPottyId;
    
    @Validate
    @Column(type = Column.STRING, name = VALUE)
    protected String mValue;
    
    public PottyField(ContentValues values) {
        super(values);
    }

    public PottyField(Cursor cursor) {
        super(cursor);
    }
    
    @Override
    public Uri getUri() {
        Uri base = PottyProvider.BASE_URI;
        if (isExistingObject()) {
            base = Uri.withAppendedPath(base, PottyFieldsTable.POTTIES_FIELD_PATH);
            base = ContentUris.withAppendedId(base, getId());
        } else {
            base = Uri.withAppendedPath(base, PottyFieldsTable.POTTIES_FIELDS_PATH);
        }
        return base;
    }
    

    @Override
    public boolean save(Context context) throws InvalidFieldException {
        ContentValues values = new ContentValues();
        validate(values);
        String selection = PottyField.POTTY_ID + " = ? AND " + PottyField.TEMPLATE_ID + " = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(mPottyId),
                String.valueOf(mTemplateId)
        };
        Cursor c = context.getContentResolver().query(PottyFieldsTable.POTTIES_FIELDS_URI,
                PottyFieldsTable.PROJECTION, selection, selectionArgs,
                null);
        long id = 0;
        if (c.getCount() > 0) {
            c.moveToFirst();
            id = c.getLong(c.getColumnIndex(Dao._ID));
        }
        c.close();
        if (id != 0 || isExistingObject()) {
            // update
            context.getContentResolver().update(getUri(), values, _ID + " = ?", new String[] {
                    String.valueOf(id)
            });
            return true;
        } else {
            return super.save(context);
        }
    }
    
    public Potty getPotty(Context context) {
        return null;
    }

    public String getValue() {
        return mValue;
    }
    
    public Field getFieldTemplate(Context context) {
        Uri uri = ContentUris.withAppendedId(FieldsTable.URI, getTemplateId());
        Cursor c = context.getContentResolver()
                .query(uri, FieldsTable.PROJECTION, null, null, null);
        Field f = new Field(c);
        c.close();
        return f;
    } 
    
    
    public long getTemplateId() {
        return mTemplateId;
    }

    public void setPottyId(long id) {
        mPottyId = id;
    }
    
    public void setTemplateId(long id) {
        mTemplateId = id;
    }
    
    public void setValue(String value) {
        mValue = value;
    }
    
}
