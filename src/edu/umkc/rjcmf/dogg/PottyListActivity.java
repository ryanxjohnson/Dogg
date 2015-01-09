package edu.umkc.rjcmf.dogg;

import edu.umkc.rjcmf.dogg.adapters.PottyAdapter;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.views.CursorSpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class PottyListActivity extends Activity {

	private static final String TAG = "PottyListActivity";

	private CursorSpinner mPets;

	private Pet mPet;

	private PottyAdapter mAdapter;

	private ListView mList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.potty_list);

		initUI();
	}

	@Override
	protected void onResume() {
		mAdapter.requery();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected void initUI() {
		mPets = (CursorSpinner) findViewById(R.id.pet);
		mPets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> list, View row,
					int position, long id) {
				updateAdapterView();
			}

			private void updateAdapterView() {
				mPet = getPet();

				mAdapter.setPet(mPet);

				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		mPet = getPet();

		mAdapter = new PottyAdapter(this, getPet());
		mList = (ListView) findViewById(android.R.id.list);
		mList.setAdapter(mAdapter);
		registerForContextMenu(mList);
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> row, View view,
					int position, long id) {
				openPotty(id);
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// edit needs work. trouble resuming PottyActivity
		menu.add(Menu.NONE, R.string.edit, Menu.NONE, R.string.edit);
		menu.add(Menu.NONE, R.string.delete, Menu.NONE, R.string.delete);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.string.edit:
			editPotty(info.id);
			return true;
		case R.string.delete:
			showDeleteDialog(info.id);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void openPotty(long id) {
		Intent intent = new Intent(this, PottyInfoActivity.class);
		intent.putExtra(BaseFormActivity.EXTRA_ITEM_ID, id);
		startActivity(intent);
	}

	private void editPotty(long id) {
		Intent intent = new Intent(this, PottyActivity.class);
		intent.putExtra(BaseFormActivity.EXTRA_ITEM_ID, id);
		// startActivity(intent); // the world isn't ready for this
	}

	private void showDeleteDialog(final long id) {
		showDeleteDialog(new Runnable() {
			@Override
			public void run() {
				Uri uri = ContentUris.withAppendedId(PottyTable.BASE_URI, id);
				getContentResolver().delete(uri, null, null);
			}
		});
	}

	protected void showDeleteDialog(final Runnable deleteAction) {
		// dialog doesn't persist through rotations. set serializable might fix
		// it
		Dialog deleteDialog = new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_title_delete)
				.setMessage(R.string.dialog_message_delete)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteAction.run();
								dialog.dismiss();
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create();
		deleteDialog.show();
	}

	protected final Pet getPet() {
		Pet pet = Pet.loadById(this, mPets.getSelectedItemId());
		if (pet == null) {
			Log.e(TAG, "Unable to load pet #" + mPets.getSelectedItemId());
			throw new IllegalStateException("Unable to load pet #"
					+ mPets.getSelectedItemId());
		}
		return pet;
	}

}
