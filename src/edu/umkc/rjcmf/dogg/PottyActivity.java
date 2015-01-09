package edu.umkc.rjcmf.dogg;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import edu.umkc.rjcmf.dogg.dao.CachedValue;
import edu.umkc.rjcmf.dogg.dao.Dao;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.dao.Potty;
import edu.umkc.rjcmf.dogg.dao.PottyField;
import edu.umkc.rjcmf.dogg.exceptions.InvalidFieldException;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;
import edu.umkc.rjcmf.dogg.provider.tables.CacheTable;
import edu.umkc.rjcmf.dogg.provider.tables.FieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.views.CursorSpinner;
import edu.umkc.rjcmf.dogg.views.DateButton;
import edu.umkc.rjcmf.dogg.views.DividerView;
import edu.umkc.rjcmf.dogg.views.FieldView;

public class PottyActivity extends BaseFormActivity {

	private DateButton mDate;

	private CursorSpinner mPets;

	private CheckBox mPee;

	private CheckBox mPoo;

	private LinearLayout mFieldsContainer;

	private final ArrayList<FieldView> mFields = new ArrayList<FieldView>();

	private Potty mPotty = new Potty(new ContentValues());

	private Bundle mIcicle;

	private Pet mPet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.potty);
		// save the icicle so that we can restore the meta fields later on.
		mIcicle = savedInstanceState;
	}

	private final Pet getPet() {
		if (mPet == null) {
			mPet = Pet.loadById(this, mPets.getSelectedItemId());
		}
		if (mPet == null) {
			throw new IllegalStateException("Unable to load pet #"
					+ mPets.getSelectedItemId());
		}
		return mPet;
	}

	@SuppressWarnings("deprecation")
	@Override
	// this needs work
	protected void onResume() {
		super.onResume();

		Cursor fields = managedQuery(Uri.withAppendedPath(
				PottyProvider.BASE_URI, FieldsTable.URI_PATH),
				FieldsTable.PROJECTION, null, null, null);
		LayoutInflater inflater = LayoutInflater.from(this);
		mFieldsContainer.removeAllViews();

		HashMap<Long, PottyField> fieldMap = new HashMap<Long, PottyField>();
		if (mPotty.isExistingObject()) {
			ArrayList<PottyField> objectFields = mPotty.getFields(this);
			for (PottyField field : objectFields) {
				fieldMap.put(field.getTemplateId(), field);
			}
		}

		if (fields.getCount() > 0) {
			DividerView divider = (DividerView) inflater.inflate(
					R.layout.divider, mFieldsContainer, false);
			divider.setText(R.string.divider_potty_fields);
			mFieldsContainer.addView(divider);
		}

		while (fields.moveToNext()) {
			//String hint = fields.getString(fields.getColumnIndex(Field.TITLE));
			long id = fields.getLong(fields.getColumnIndex(Dao._ID));
			View container = inflater.inflate(R.layout.potty_field, null);
			FieldView field = (FieldView) container.findViewById(R.id.field);
			field.setFieldId(id);
			field.setId((int) id);
			//field.setHint(hint);
			mFieldsContainer.addView(container);
			mFields.add(field);

			if (mIcicle != null || fieldMap.size() > 0) {
				String value = null;
				if (mIcicle != null) {
					value = mIcicle.getString(field.getKey());
				}
				if (value != null && value.length() > 0) {
					field.setText(value);
				} else {
					if (mPotty.isExistingObject()) {
						// set the value from the database, if present
						PottyField objectField = fieldMap.get(id);
						if (objectField != null) {
							field.setText(objectField.getValue());
						}
					}
				}
			}
		}
		if (fields.getCount() == 0) { // this isn't useful right now. For
										// PetCareIntervals Later
			mFieldsContainer.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(Menu.NONE, R.string.edit_fields, Menu.NONE,
//				R.string.edit_fields).setIntent(
//				new Intent(this, FieldListActivity.class));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		for (FieldView fieldView : mFields) {
			outState.putString(fieldView.getKey(), fieldView.getText()
					.toString());
		}
	}

	@Override
	protected boolean postSaveValidation() {
		try {
			for (FieldView fieldView : mFields) { // because the user will be
													// able to add custom fields
				PottyField field = new PottyField(new ContentValues());
				field.setPottyId(mPotty.getId());
				field.setTemplateId(fieldView.getFieldId());
				field.setValue(fieldView.getText().toString());
				field.save(this);
			}
			return true;
		} catch (InvalidFieldException exception) {
			handleInvalidField(exception);
		}

		return false;
	}

	@Override
	protected void saved() {
		// invalidate the cache
		ContentValues values = new ContentValues();
		values.put(CachedValue.VALID, "0");
		getContentResolver().update(CacheTable.BASE_URI, values,
				CachedValue.ITEM + " = ?",
				new String[] { String.valueOf(mPets.getSelectedItemId()) });

		// switch to history tab when potty is saved
		Activity parent = getParent();
		if (parent == null) {
			finish();
		} else if (parent instanceof Dogg) {
			((Dogg) parent).switchToHistoryTab();
		}

		// clear the checkboxes, text
		mPotty = new Potty(new ContentValues());
		onCreate(null);
	}

	@Override
	protected Dao getDao() {
		return mPotty;
	}

	@Override
	protected String[] getProjectionArray() {
		return PottyTable.PROJECTION;
	}

	@Override
	protected Uri getUri(long id) {
		return ContentUris.withAppendedId(PottyTable.BASE_URI, id);
	}

	@Override
	protected void initUI() {

		mDate = (DateButton) findViewById(R.id.date);
		mPee = (CheckBox) findViewById(R.id.pee);
		mPoo = (CheckBox) findViewById(R.id.poo);
		mFieldsContainer = (LinearLayout) findViewById(R.id.container);
		mPets = (CursorSpinner) findViewById(R.id.pet);

		mPets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mPet = null;
				mPet = getPet();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

	}

	@Override
	protected void populateUI() {

		// initialize the pottty fields
		mDate.setDate(mPotty.getTimestamp());
		mPee.setChecked(mPotty.isPee());
		mPoo.setChecked(mPotty.isPoo());

		if (mPotty.isExistingObject()) {
			setTitle(getString(R.string.title_potty, mDate.getText()));
			mPets.setSelectedId(mPotty.getPetId());
		}

	}

	@Override
	protected void deleted() {
		getContentResolver().delete(CacheTable.BASE_URI,
				CachedValue.KEY + " = ?", new String[] {});
		super.deleted();
	}

	@Override
	protected int getCreateString() {
		return R.string.add_potty;
	}

	/*
	 * Pre-condition: The checkboxes and fields are empty
	 * 
	 * Post-condition: Data is validated and mPotty params saved
	 */
	@Override
	protected void setFields() throws InvalidFieldException {
		mPotty.setPee(mPee.isChecked());
		mPotty.setPoo(mPoo.isChecked());

		// Invalid Pre-Condition: Pee or Poo must be checked. Cannot be empty
		// potty
		assert (mPee.isChecked() || mPoo.isChecked());

		mPotty.setPetId(mPets.getSelectedItemId());
		mPotty.setTimestamp(mDate.getTimestamp());
	}
}
