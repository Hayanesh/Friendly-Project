package com.hayanesh.dsm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hayanesh on 07-Dec-16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    final static int DATABASE_VERSION = 1;

    final static String DATABASE_NAME = "CustomerList";

    //Table
    final static String DETAILS_TABLE = "Details";

    //Column
    final static String KEY_ID  = "id";
    final static String KEY_NAME = "name";
    final static String KEY_EMAIL = "email";
    final static String KEY_PHONE = "phone";
   // final static String KEY_ADDRESS = "address";
    final static String KEY_LOCALITY = "locality";
    final static String KEY_PINCODE = "pincode";
    final static String KEY_CATEGORY = "category";
    final static String KEY_TYPE = "type";
    final static String KEY_PASS = "password";




    final static String CREATE_TABLE_DETAILS = "CREATE TABLE " + DETAILS_TABLE + "(" + KEY_ID + " INTERGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
            + KEY_EMAIL +" TEXT, "+ KEY_PASS +" TEXT, "+ KEY_PHONE +" TEXT, "+ KEY_LOCALITY +" TEXT, "
            + KEY_PINCODE +" TEXT, "+ KEY_CATEGORY +" TEXT, "+ KEY_TYPE +" TEXT "+")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DETAILS_TABLE);
    }

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public long createDetails(Details details)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,details.getId());
        values.put(KEY_NAME,details.getName());
        values.put(KEY_EMAIL,details.getEmail());
        values.put(KEY_PASS,details.getPass());
        values.put(KEY_PHONE,details.getPhone());
        values.put(KEY_LOCALITY,details.getLocality());
        values.put(KEY_PINCODE,details.getPin());
        values.put(KEY_CATEGORY,details.getCategory());
        values.put(KEY_TYPE,details.getType());
        long details_id = db.insert(DETAILS_TABLE,null,values);

        db.close();
        return details_id;
    }

    public Details getDetails(String id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DETAILS_TABLE, new String[] { KEY_ID,
                        KEY_NAME,KEY_EMAIL,KEY_PHONE,KEY_LOCALITY,KEY_CATEGORY,KEY_TYPE }, KEY_EMAIL + "=?",
                new String[] { id }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Details details = new Details(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),cursor.getString(2),null,cursor.getString(3),cursor.getString(4),null,cursor.getString(5),cursor.getString(6));
        // return contact
        return details;


    }
    public void DeleteDetails(Details det)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DETAILS_TABLE,KEY_ID + " = ?",new String[]{String.valueOf(det.getId())});
    }
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
