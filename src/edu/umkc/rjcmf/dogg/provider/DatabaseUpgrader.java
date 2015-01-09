package edu.umkc.rjcmf.dogg.provider;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.provider.BaseColumns;
import android.util.Log;
import edu.umkc.rjcmf.dogg.dao.Pet;
import edu.umkc.rjcmf.dogg.dao.Potty;
import edu.umkc.rjcmf.dogg.dao.PottyField;
import edu.umkc.rjcmf.dogg.provider.tables.CacheTable;
import edu.umkc.rjcmf.dogg.provider.tables.ContentTable;
import edu.umkc.rjcmf.dogg.provider.tables.FieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PetTypeTable;
import edu.umkc.rjcmf.dogg.provider.tables.PetsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyFieldsTable;
import edu.umkc.rjcmf.dogg.provider.tables.PottyTable;
import edu.umkc.rjcmf.dogg.util.Debugger;

public class DatabaseUpgrader {

    private static final String TAG = "DatabaseUpgrader";

    private static final int V1_DATABASE = 3; // Version 1.X

    private static final int V2_DATABASE = 4; // Version 2.X

    private static final int V3_DATABASE = 5; // Version 3.X

    private static final StringBuilder BUILDER = new StringBuilder();

    private static SQLiteDatabase sDatabase;

    // Note: these columns are hard-coded for now. I should back-port the old
    // column name constants but I likely won't.
    public static void upgradeDatabase(final SQLiteDatabase database) {
        sDatabase = database;
        try {
            switch (database.getVersion()) {
                case V1_DATABASE:
                    // add the partial flag
                    exec("ALTER TABLE potties ADD COLUMN is_partial INTEGER;");

                    // add the restart flag
                    exec("ALTER TABLE potties ADD COLUMN restart INTEGER;");

                    // add the distance units
                    exec("ALTER TABLE pets ADD COLUMN distance INTEGER DEFAULT -1;");

                    // add the volume units
                    exec("ALTER TABLE pets ADD COLUMN volume INTEGER DEFAULT -1;");

                    // create the service interval table
                    BUILDER.append("CREATE TABLE maintenance_intervals (");
                    BUILDER.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
                    BUILDER.append("creation_date INTEGER,");
                    
                    BUILDER.append("description TEXT,");

                    BUILDER.append("pet_id INTEGER,");
                    BUILDER.append("is_repeating INTEGER");
                    BUILDER.append(");");
                    flush();

                    // create the version table
                    BUILDER.append("CREATE TABLE version (");
                    BUILDER.append("version INTEGER");
                    BUILDER.append(");");
                    flush();
                case V2_DATABASE:
                    // add the economy field
                    exec("ALTER TABLE potties ADD COLUMN date DOUBLE;");
                case V3_DATABASE:
                    // This is the upgrade to 3.0 -- brace for impact!

                    if (backupExistingTables() && createNewTables() && migrateOldData()
                            && cleanUpOldTables()) {
                        Log.d(TAG, "Completed migration!");
                    } else {
                        Log.e(TAG, "Unable to complete migration!");
                    }
                    break;
                default:
                    // unknown version; recurse and start from the beginning
                    database.setVersion(V1_DATABASE);
                    upgradeDatabase(database);
                    return;
            }
            database.setVersion(PottyProvider.DATABASE_VERSION);
        } catch (SQLiteException e) {
            Log.e(TAG, "Couldn't upgrade database!", e);
        }
    }

    private static final void flush() {
        exec(BUILDER.toString());
        BUILDER.setLength(0);
    }

    private static final void exec(final String query) {
        log(query);
        sDatabase.execSQL(query);
    }

    private static final void log(final String msg) {
        Debugger.d(TAG, msg);
    }

    private static boolean backupExistingTables() {
        String[] tables = new String[] {
                "potties", "pets"
        };

        try {
            for (String table : tables) {
                BUILDER.append("ALTER TABLE ").append(table).append(" RENAME TO OLD_")
                        .append(table);
                flush();
            }
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "Unable to backup existing tables!", e);
        }
        return false;
    }

    private static boolean createNewTables() {
        ContentTable[] tables =
                new ContentTable[] {
                        new PottyTable(), new PottyFieldsTable(), new FieldsTable(),
                        new PetsTable(), new PetTypeTable(), new CacheTable()
                };

        try {
            for (ContentTable table : tables) {
                exec(table.create());
                String[] upgradeSql = table.init(true);
                if (upgradeSql != null) {
                    for (String sql : upgradeSql) {
                        exec(sql);
                    }
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to create new table!", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Unable to create new table!", e);
        }
        return false;
    }

    private static boolean migrateOldData() {
        try {
            // migrate pet data
            BUILDER.append("INSERT INTO ").append(PetsTable.TABLE_NAME).append(" (");
            
            BUILDER.append(Pet.TITLE).append(", ");
            BUILDER.append(Pet.BREED).append(", ");           
            BUILDER.append(Pet.PET_TYPE);
            BUILDER.append(Pet.DEFAULT_TIME).append(", ");
            BUILDER.append("CASE WHEN title IS NULL OR title=\"\" THEN breed) ELSE title END AS d_title, ");
            BUILDER.append("year, def, '1' FROM OLD_pets;");
            flush();

            // TODO(3.1) - migrate service intervals.

            // migrate potty data
            BUILDER.append("INSERT INTO ").append(PottyTable.TABLE_NAME).append(" (");
            BUILDER.append(Potty.PET_ID).append(", ");
            BUILDER.append(Potty.DATE).append(", ");
            BUILDER.append(Potty.PEE).append(", ");
            BUILDER.append(Potty.POO).append(", ");
            BUILDER.append(Potty.RESTART).append(", ");
            BUILDER.append(Potty.COMMENT).append(", ");
            flush();

            // migrate the potty comments
            BUILDER.append("INSERT INTO ").append(PottyFieldsTable.TABLE_NAME).append(" (");
            BUILDER.append(PottyField.POTTY_ID).append(", ");
            BUILDER.append(PottyField.TEMPLATE_ID).append(", ");
            BUILDER.append(PottyField.VALUE);
            BUILDER.append(") SELECT _id, '1', comment FROM OLD_potties;");
            flush();

            // Update the pet IDs
            BUILDER.append("UPDATE potties SET pet_id = (SELECT pets._id FROM pets, OLD_pets WHERE pets.title = OLD_pets.title AND OLD_pets._id = pet_id)");
            flush();

            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "Unable to migrate data!", e);
            return false;
        }
    }

    private static boolean cleanUpOldTables() {
        try {
            // exec("DROP TABLE OLD_pets");
            // exec("DROP TABLE OLD_potties");
            return true;
        } catch (SQLiteException e) {
            Log.e(TAG, "Unable to clean up old tables!", e);
            return false;
        }
    }
}
