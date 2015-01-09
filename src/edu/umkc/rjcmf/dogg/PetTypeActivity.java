package edu.umkc.rjcmf.dogg;

import android.app.ActionBar;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.PetType;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;
import edu.umkc.rjcmf.dogg.provider.tables.PetTypeTable;

public class PetTypeActivity extends BaseFormActivity {
	
    private EditText mTitle;
    private EditText mDescription;
    private PetType mPetType = new PetType(new ContentValues());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.pet_type);
        
		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    protected Dao getDao() {
        return mPetType;
    }

    @Override
    protected void initUI() {
        mTitle = (EditText) findViewById(R.id.title);
        mDescription = (EditText) findViewById(R.id.description);
    }
    
    @Override
    protected void populateUI() {
        mTitle.setText(mPetType.getTitle());
        mDescription.setText(mPetType.getDescription());
    }
    
    @Override
    protected void setFields() {
    	mPetType.setTitle(mTitle.getText().toString());
    	mPetType.setDescription(mDescription.getText().toString());
    }

    @Override
    protected String[] getProjectionArray() {
        return PetTypeTable.PROJECTION;
    }

    @Override
    protected Uri getUri(long id) {
        return ContentUris.withAppendedId(
                Uri.withAppendedPath(PottyProvider.BASE_URI, PetTypeTable.URI), id);
    }

    @Override
    protected int getCreateString() {
        return R.string.add_pet_type;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean canDelete() {
        Cursor count = managedQuery(PetTypeTable.BASE_URI, null, null, null, null);
        return count != null && count.getCount() > 1;
    }


} 
