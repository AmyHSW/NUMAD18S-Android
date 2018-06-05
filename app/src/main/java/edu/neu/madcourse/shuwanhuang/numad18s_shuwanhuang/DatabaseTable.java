package edu.neu.madcourse.shuwanhuang.numad18s_shuwanhuang;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseTable {

    private static final String TAG = "DictionaryDatabase";

    //The columns we'll include in the dictionary table
    public static final String COL_WORD = "WORD";

    private static final String DATABASE_NAME = "DICTIONARY.db";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    public DatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public boolean containsWord(String word) {
        return getWordMatches(word.toLowerCase(), null) != null;
    }

    public Cursor getWordMatches(String query, String[] columns) {
        String selection = COL_WORD + "=?";
        String[] selectionArgs = new String[] {query};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private static final String DB_PATH = "/data/data/" + BuildConfig.APPLICATION_ID
                + "/databases/";
        private static final String DB_NAME = "DICTIONARY.db";
        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        COL_WORD + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
            createOrCopyDatabase();
        }

        private void createOrCopyDatabase() {
            if (!dbInPhone()) {
                if (dbInAssets()) {
                    copyDatabase();
                } else {
                    createDictionaryDatabase();
                }
            }
        }

        private boolean dbInPhone() {
            File dbFile = mHelperContext.getDatabasePath(DB_NAME);
            return dbFile.exists();
        }

        private boolean dbInAssets() {
            try {
                return Arrays.asList(mHelperContext.getAssets().list("")).contains(DATABASE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private void createDictionaryDatabase() {
            mDatabase = this.getReadableDatabase();
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadDictionary();
        }

        private void copyDatabase() {
            InputStream mInputStream = null;
            OutputStream mOutputStream = null;
            try {
                mInputStream = mHelperContext.getAssets().open(DB_NAME);
                Log.i(TAG, "start coping db from assets..");
                String outFileName = DB_PATH + DB_NAME;
                File outFile = new File(outFileName);
                File dbFolder = new File(DB_PATH);
                dbFolder.mkdirs();
                mOutputStream = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = mInputStream.read(buffer)) > 0) {
                    mOutputStream.write(buffer, 0, length);
                }
                Log.i(TAG, "finish coping db");
                mOutputStream.flush();
                mOutputStream.close();
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (mInputStream != null) {
                        mInputStream.close();
                    }
                    if (mOutputStream != null) {
                        mOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        private void loadWords() throws IOException {
            Log.i(TAG, "start loading words into dictionary...");
            final Resources resources = mHelperContext.getResources();
            InputStream inputStream = resources.openRawResource(R.raw.wordlist);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> wordlist = new ArrayList<>();

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    wordlist.add(line);
                }
                addWords(wordlist);
            } finally {
                reader.close();
            }
        }

        private void addWords(List<String> wordlist) {
            ContentValues contentValues = new ContentValues();
            for (String word : wordlist) {
                contentValues.put(COL_WORD, word);
                mDatabase.insert(FTS_VIRTUAL_TABLE, null, contentValues);
            }
            Log.i(TAG, "finish loading, total: " + wordlist.size());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            mDatabase = db;
//            mDatabase.execSQL(FTS_TABLE_CREATE);
//            loadDictionary();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            createOrCopyDatabase();
        }
    }
}
