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
    final static String APPLIANCES_TABLE = "Applicances";
    //Column
    final static String KEY_ID = "id";
    final static String KEY_NAME = "name";
    final static String KEY_EMAIL = "email";
    final static String KEY_PHONE = "phone";
    // final static String KEY_ADDRESS = "address";
    final static String KEY_LOCALITY = "locality";
    final static String KEY_PINCODE = "pincode";
    final static String KEY_CATEGORY = "category";
    final static String KEY_TYPE = "type";
    final static String KEY_PASS = "password";

    //Appliances column
    final static String KEY_APPLIANCE = "appliance";
    final static String KEY_QTY = "quantity";
    final static String KEY_RATE = "rating";
    final static String KEY_SHIFT = "shiftable";

    final static String CREATE_TABLE_DETAILS = "CREATE TABLE " + DETAILS_TABLE + "(" + KEY_ID + " INTERGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
            + KEY_EMAIL + " TEXT, " + KEY_PASS + " TEXT, " + KEY_PHONE + " TEXT, " + KEY_LOCALITY + " TEXT, "
            + KEY_PINCODE + " TEXT, " + KEY_CATEGORY + " TEXT, " + KEY_TYPE + " TEXT " + ")";

    final static String CREATE_TABLE_APPLIANCES = "CREATE TABLE " + APPLIANCES_TABLE + "(" + KEY_ID + " INTERGER PRIMARY KEY, " + KEY_APPLIANCE + " TEXT, "
            + KEY_QTY + " INTEGER, " + KEY_RATE + " TEXT, " + KEY_SHIFT + " INTEGER " +")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DETAILS);
        db.execSQL(CREATE_TABLE_APPLIANCES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DETAILS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + APPLIANCES_TABLE);
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public long createDetails(Details details) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, details.getId());
        values.put(KEY_NAME, details.getName());
        values.put(KEY_EMAIL, details.getEmail());
        values.put(KEY_PASS, details.getPass());
        values.put(KEY_PHONE, details.getPhone());
        values.put(KEY_LOCALITY, details.getLocality());
        values.put(KEY_PINCODE, details.getPin());
        values.put(KEY_CATEGORY, details.getCategory());
        values.put(KEY_TYPE, details.getType());
        long details_id = db.insert(DETAILS_TABLE, null, values);

        db.close();
        return details_id;
    }

    public Details getDetails(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DETAILS_TABLE, new String[]{KEY_ID,
                        KEY_NAME, KEY_EMAIL, KEY_PHONE, KEY_LOCALITY, KEY_CATEGORY, KEY_TYPE}, KEY_EMAIL + "=?",
                new String[]{id}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Details details = new Details(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), null, cursor.getString(3), cursor.getString(4), null, cursor.getString(5), cursor.getString(6));
        // return contact
        return details;


    }

    public void DeleteDetails(Details det) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(DETAILS_TABLE, KEY_ID + " = ?", new String[]{String.valueOf(det.getId())});
    }

    public void updateAppobj(Appliances a) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_QTY, a.getQty());
        values.put(KEY_RATE, a.getRating()[0]);
        values.put(KEY_SHIFT,a.getShiftable());
        // updating row
        db.update(APPLIANCES_TABLE, values, KEY_APPLIANCE + " = ?",
                new String[]{String.valueOf(a.getName())});
    }

    public void removeAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QTY, 0);
        values.put(KEY_RATE,"");
        db.update(APPLIANCES_TABLE, values,null,null);
    }
    public boolean CreateTableAppliances(List<Appliances> appliances)
    {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        try {
            for (int i = 0; i < appliances.size(); i++) {
                Appliances a = appliances.get(i);
                ContentValues values = new ContentValues();
                values.put(KEY_ID, i+100);
                values.put(KEY_APPLIANCE, a.getName());
                values.put(KEY_QTY, a.getQty());

                String rating = a.getRating()[0];
                values.put(KEY_RATE, rating);
                long appliance_id = sqLiteDatabase.insert(APPLIANCES_TABLE, null, values);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Appliances> getAppliances()
    {
        ArrayList<Appliances> appliances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String SELECT_APPLIANCES = "SELECT * FROM "+APPLIANCES_TABLE+" WHERE "+KEY_QTY +" >0";
        Cursor c = db.rawQuery(SELECT_APPLIANCES,null);
        if(c.moveToFirst())
        {
            do {
                Appliances app = new Appliances();
                app.setName(c.getString(c.getColumnIndex(KEY_APPLIANCE)));
                app.setQty(c.getInt(c.getColumnIndex(KEY_QTY)));
                String[] rating = new String[1];
                rating[0] = c.getString(c.getColumnIndex(KEY_RATE));
                app.setRating(rating);
                app.setShiftable(c.getInt(c.getColumnIndex(KEY_SHIFT)));
                appliances.add(app);
            }while (c.moveToNext());
        }
        return appliances;
    }
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
