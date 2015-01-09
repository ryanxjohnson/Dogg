package edu.umkc.rjcmf.dogg;

import android.app.ActionBar;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import edu.umkc.rjcmf.dogg.dao.CachedValue;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.exceptions.InvalidFieldException;
import edu.umkc.rjcmf.dogg.provider.tables.CacheTable;
import edu.umkc.rjcmf.dogg.provider.tables.PetsTable;
import edu.umkc.rjcmf.dogg.views.CursorSpinner;

public class PetActivity extends BaseFormActivity {

	private EditText mTitle;

	private EditText mDescription;

	private EditText mBreed;

	private CheckBox mSetDefault;

	private CursorSpinner mPetTypes;

	private Pet mPet = new Pet(new ContentValues());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.pet);

		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected Dao getDao() {
		return mPet;
	}

	@Override
	protected String[] getProjectionArray() {
		return PetsTable.PROJECTION;
	}

	@Override
	protected Uri getUri(long id) {
		return ContentUris.withAppendedId(PetsTable.BASE_URI, id);
	}

	@Override
	protected void initUI() {
		mTitle = (EditText) findViewById(R.id.title);
		mBreed = (EditText) findViewById(R.id.breed);
		mDescription = (EditText) findViewById(R.id.description);
		mSetDefault = (CheckBox) findViewById(R.id.make_default);
		mPetTypes = (CursorSpinner) findViewById(R.id.type);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void populateUI() {
		mTitle.setText(mPet.getTitle());
		mDescription.setText(mPet.getDescription());
		mBreed.setText(mPet.getBreed());
		mPetTypes.setSelectedId(getPetType());

		Uri uri = PetsTable.BASE_URI;
		String[] projection = new String[] { Dao._ID };
		Cursor c = managedQuery(uri, projection, null, null, Pet.DEFAULT_TIME
				+ " desc");
		if (c.getCount() > 0) {
			c.moveToFirst();
			mSetDefault.setChecked(c.getLong(0) == mPet.getId());
		}

		if (mPet.isExistingObject()) {
			setTitle(mPet.getTitle());

		}
	}

	@Override
	protected void setFields() throws InvalidFieldException {

		String title = mTitle.getText().toString().trim();

		if (TextUtils.isEmpty(title)) {
			throw new InvalidFieldException(mTitle,
					R.string.error_invalid_pet_title);
		}
		mPet.setTitle(title);

		String breed = mBreed.getText().toString().trim();
		if (TextUtils.isEmpty(breed)) {
			throw new InvalidFieldException(mBreed,
					R.string.error_invalid_pet_breed);
		}
		mPet.setBreed(breed);

		String description = mDescription.getText().toString().trim();
		mPet.setDescription(description);

		mPet.setPetType(mPetTypes.getSelectedItemId());

		if (mSetDefault.isChecked()) {
			mPet.setDefaultTime(System.currentTimeMillis());
		}
	}

	@Override
	protected void saved() {
// Debig this later
//		ContentValues values = new ContentValues();
//		String where = Dao._ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(mPet.getId()) };

//		Uri uri = PottyTable.BASE_URI;

		/* This was causing crash on Pet Save */
		// getContentResolver().update(uri, values, null, selectionArgs);

		// Also blow away the statistics cache
		getContentResolver().delete(CacheTable.BASE_URI,
				CachedValue.ITEM + " = ?", selectionArgs);

		super.saved();
	}

	private long getPetType() {
		return mPet.getPetType();
	}

	@Override
	protected int getCreateString() {
		return R.string.add_pet;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean canDelete() {
		Cursor count = managedQuery(PetsTable.BASE_URI, null, null, null, null);
		return count != null && count.getCount() > 1;
	}

}
