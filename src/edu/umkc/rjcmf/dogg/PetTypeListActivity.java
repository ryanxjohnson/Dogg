package edu.umkc.rjcmf.dogg;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import edu.umkc.rjcmf.dogg.dao.PetType;
import edu.umkc.rjcmf.dogg.provider.PottyProvider;
import edu.umkc.rjcmf.dogg.provider.tables.PetTypeTable;

public class PetTypeListActivity extends BaseListActivity {

	private static final int MENU_CREATE = 1;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_CREATE, Menu.NONE, R.string.add_pet_type);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CREATE:
                startActivity(new Intent(this, PetTypeActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String[] getFrom() {
        return new String[] {
                PetType.TITLE,
                PetType.DESCRIPTION
        };
    }

    @Override
    protected Uri getUri() {
        return Uri.withAppendedPath(PottyProvider.BASE_URI, PetTypeTable.URI);
    }

    @Override
    public void onItemClick(long id) {
        loadItem(id, PetTypeActivity.class);
    }

    @Override
    protected boolean canDelete(int position) {
        return getAdapter().getCount() > 1;
    }
	
	
} // EOC
