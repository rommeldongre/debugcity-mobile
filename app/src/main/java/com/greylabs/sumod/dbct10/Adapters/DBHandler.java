package com.greylabs.sumod.dbct10.Adapters;

/**
 * Created by Sumod on 6/3/2015.
 */

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.greylabs.sumod.dbct10.Model.Category;
import com.greylabs.sumod.dbct10.Model.Incident;
import com.greylabs.sumod.dbct10.Model.User;
import com.greylabs.sumod.dbct10.R;

public class DBHandler extends SQLiteOpenHelper {
    private static final String TAG = "DBHandler";

    //DATABASE VERSION and  NAME :
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DBugCT.db";
    private static final int zero = 0;
    //Incident Table:
    public static final String TABLE_INCIDENTS = "INCIDENTS";

    public static final String KEY_ID = "ID";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_CATEGORY = "CATEGORY";
    public static final String KEY_IMAGE = "IMAGE";
    public static final String KEY_PINCODE = "PINCODE";

    //Category Table:
    public static final String TABLE_CATEGORY = "CATEGORY";

    public static final String KEY_NAME = "NAME";
    public static final String KEY_DESCRIPTION = "DESCRIPTION";

    //User Table:
    public static final String TABLE_USERS = "USERS";

    public static final String KEY_EMAIL_ID = "ID";
    public static final String KEY_FULL_NAME = "FULL_NAME";
    public static final String KEY_AUTH = "AUTH";
    public static final String KEY_MOBILE = "MOBILE";
    public static final String KEY_LOCATION = "LOCATION";
    public static final String KEY_CREDITS = "CREDITS";



    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_INCIDENTS = "CREATE TABLE " + TABLE_INCIDENTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + KEY_LATITUDE + " VARCHAR(255) , "
                + KEY_LONGITUDE + " VARCHAR(255), "
                + KEY_CATEGORY + " VARCHAR(255), "
                + KEY_IMAGE + " BLOB, "
                + KEY_PINCODE + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_INCIDENTS);

        String CREATE_TABLE_CATEGORY = " CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_NAME + " VARCHAR(255), "
                + KEY_DESCRIPTION + " VARCHAR(255) " + ")";
        db.execSQL(CREATE_TABLE_CATEGORY);

        String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_EMAIL_ID + " VARCHAR(255) PRIMARY KEY , "
                + KEY_FULL_NAME + " VARCHAR(255) , "
                + KEY_AUTH + " VARCHAR(255), "
                + KEY_MOBILE + " VARCHAR(255), "
                + KEY_LOCATION + " VARCHAR(255), "
                + KEY_CREDITS + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_INCIDENTS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USERS);

        onCreate(db);

    }

    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //Delete all tables:
    public void resetDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCIDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);

    }

    //CRUD operations:

    //Create a new row:

    public void addUser(User user){
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL_ID, user.getEmail_ID());
            values.put(KEY_FULL_NAME, user.getFull_name());
            values.put(KEY_AUTH, user.getPassword());
            values.put(KEY_MOBILE, user.getMobile());
            values.put(KEY_LOCATION, user.getLocation());
            values.put(KEY_CREDITS, user.getCredits());
            db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        }catch (SQLException e){
            Log.e(TAG + ": addUser", e.getMessage());
        }
    }

    public void updateUser(User user){
        try {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_EMAIL_ID, user.getEmail_ID());
            values.put(KEY_FULL_NAME, user.getFull_name());
            values.put(KEY_AUTH, user.getPassword());
            values.put(KEY_MOBILE, user.getMobile());
            values.put(KEY_LOCATION, user.getLocation());
            values.put(KEY_CREDITS, user.getCredits());
            db.update(TABLE_USERS, values, KEY_EMAIL_ID + "=?", new String[]{user.getEmail_ID()});
            db.close();
        }catch (SQLException e){
            Log.e(TAG + ": addUser", e.getMessage());
        }
    }

    public boolean ifUSerExists(String email_ID){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_FULL_NAME + " FROM " + TABLE_USERS + " WHERE " + KEY_EMAIL_ID + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{email_ID});
        boolean ifUserExists = false;
        return cursor.moveToFirst();
    }

    public User getUser(String user_ID){
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + KEY_EMAIL_ID + ", " + KEY_FULL_NAME + ", "
                + KEY_AUTH + ", " + KEY_MOBILE + ", " + KEY_LOCATION + ", " + KEY_CREDITS + " FROM "
                + TABLE_USERS + " WHERE " + KEY_EMAIL_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{user_ID});
        cursor.moveToFirst();

        User user = new User();
        user.setEmail_ID(cursor.getString(cursor.getColumnIndex(KEY_EMAIL_ID)));
        user.setFull_name(cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(KEY_AUTH)));
        user.setLocation(cursor.getString(cursor.getColumnIndex(KEY_LOCATION)));
        user.setMobile(cursor.getString(cursor.getColumnIndex(KEY_MOBILE)));
        user.setCredits(cursor.getInt(cursor.getColumnIndex(KEY_CREDITS)));
        cursor.close();
        return user;
    }

    public void deleteUser(String user_ID){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USERS, KEY_EMAIL_ID + "=?", new String[]{user_ID});
        db.close();
    }


    public void addIncident(Incident incident, Context context) {
        //1
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LATITUDE, incident.getLatitude());
            values.put(KEY_LONGITUDE, incident.getLongitude());
            values.put(KEY_CATEGORY, incident.getCategory());
            values.put(KEY_IMAGE, getBitmapAsByteArray(incident.getImage()));
            values.put(KEY_PINCODE, incident.getPin_code());
            long k = db.insertOrThrow(TABLE_INCIDENTS, null, values);
            //ShowAlert("db.insertOrThrow Returns:", String.valueOf(k), context);
            db.close();
        }
        catch(SQLiteException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
    }

    public void addCategory(Category category, Context context) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, category.getName());
            values.put(KEY_DESCRIPTION, category.getDescription());

            long k = db.insertOrThrow(TABLE_CATEGORY, null, values);
            db.close();
        }
        catch(SQLiteException e){
            ShowAlert("Exception Caught", e.getMessage(), context);
        }
    }

    //Reading a row:

    public Incident getIncident(int _id, Context context) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT ID, LATITUDE, LONGITUDE, CATEGORY, IMAGE, PINCODE FROM INCIDENTS WHERE ID =?";

            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(_id)});
            //ShowAlert("Cursor count:", String.valueOf(cursor.getCount()), context);
            cursor.moveToFirst();
        Incident incident = new Incident(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)),
                getByteArrayAsBitmap(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE))),
                cursor.getString(cursor.getColumnIndex(KEY_PINCODE)));
        cursor.close();
        db.close();
        return incident;
    }

    public Category getCategory(String category_name, Context context) {

            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT NAME, DESCRIPTION FROM CATEGORY WHERE NAME =?";
            String[] arg = new String[]{category_name};
            //ShowAlert("Exception Caught", category_name, context);
            Cursor cursor = db.rawQuery(query, arg);
            cursor.moveToFirst();



        Category category = new Category(cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
        cursor.close();
        db.close();
        //Category category = new Category("general", "gen");
        return category;
    }

    //get all incidents in a hashmap to display in a listview.
    public ArrayList<HashMap<String, String>> getIncidentList() {
        //Open connection to read only
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  " +
                KEY_ID + "," +
                KEY_LATITUDE + "," +
                KEY_LONGITUDE + "," +
                KEY_CATEGORY +
                " FROM " + TABLE_INCIDENTS;

        ArrayList<HashMap<String, String>> incidentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> incident = new HashMap<String, String>();
                incident.put("id", cursor.getString(cursor.getColumnIndex(KEY_ID)));//new
                incident.put("latitude", cursor.getString(cursor.getColumnIndex(KEY_LATITUDE)));
                incident.put("longitude", cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE)));
                incident.put("category", cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                incidentList.add(incident);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return incidentList;

    }

    public List<String> getCategoryList(Context context){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_NAME + " FROM " + TABLE_CATEGORY, null);
        List<String> categoryList = new ArrayList<>();

        if(cursor.moveToFirst()){
            do{
                categoryList.add(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            }
            while(cursor.moveToNext());
        }
        cursor.close();
        return categoryList;
    }

/*
    //get all categories in a hashmap to display on a listview.
    public ArrayList<HashMap<String, String>> getCategoryList(Context context) {
        //Open connection to read only
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  " +
                KEY_NAME + ", " +
                KEY_DESCRIPTION +
                " FROM " + TABLE_CATEGORY;

        ArrayList<HashMap<String, String>> categoryList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> category = new HashMap<String, String>();
                category.put("name", cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                category.put("description", cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                categoryList.add(category);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;

    }
    */


    //Updating a row:
    public void editIncident(Incident incident) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, incident.getLatitude());
        values.put(KEY_LONGITUDE, incident.getLongitude());
        values.put(KEY_CATEGORY, incident.getCategory());
        values.put(KEY_IMAGE, getBitmapAsByteArray(incident.getImage()));
        values.put(KEY_PINCODE, incident.getPin_code());
        db.update(TABLE_INCIDENTS, values, KEY_ID + "=?", new String[]{String.valueOf(incident.get_id())});
        db.close();

    }

    public void editCategory(Category category, String category_name) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_DESCRIPTION, category.getDescription());

        db.update(TABLE_CATEGORY, values, KEY_NAME + "=?", new String[]{category_name});
        db.close();
    }

    //getAllRows:

    public Cursor getAllIncidents() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT ID as _id, PINCODE, CATEGORY FROM INCIDENTS", null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }



    public Cursor getAllCategories() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT NAME AS _id, DESCRIPTION FROM CATEGORY";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }

    public Cursor getAllIncidentsBy(String columnNames, String whereClause, String whereArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + columnNames + " FROM INCIDENTS WHERE " + whereClause + " =?";
        Cursor cursor = db.rawQuery(query, new String[]{whereArgs});

        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getCursorByRawQuery(String query, String whereArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{whereArgs});

        if(cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public Cursor getCursorByRawQuery(String query){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }


    //Deleting a row.
    public void deleteIncident(int _id) {
        SQLiteDatabase db = this.getWritableDatabase();

        //  db.execSQL("DELETE FROM" + TABLE_INCIDENTS + "WHERE" + KEY_ID + "=" + new String[]{String.valueOf(_id)});

        db.delete(TABLE_INCIDENTS, KEY_ID + "=?", new String[]{String.valueOf(_id)});
        db.close();

    }

    public void deleteCategory(String NAME) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_CATEGORY, KEY_NAME + "=?", new String[]{NAME});
        db.close();
    }

    //converting bitmap to byte[]:
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getByteArrayAsBitmap(byte[] imgByte){
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    /**
     * DEBUGGING RELATED FUNCTIONS HERE:............
     * ...............................................................
     * ...............................................................
     */

    public void ShowAlert(String title, String message, Context context) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.setIcon(R.drawable.abc_dialog_material_background_dark);
        alertDialog.show();
    }

    /* public void addIncident(Incident incident, Context context) {
        ShowAlert("Pop up", "addIncident function begins", context);


        try {

            SQLiteDatabase db = getWritableDatabase();//problem
        }
        catch (SQLiteException e){
            ShowAlert("SQL Error", e.getMessage() , context);
        }


        //1
        SQLiteDatabase db = getWritableDatabase();
        ShowAlert("Pop up", "1", context);


        ContentValues values = new ContentValues();
        ShowAlert("Pop up", "2", context);

        //values.put(KEY_ID, incident.get_id());

        //3
        values.put(KEY_LATITUDE, incident.getLatitude());
        ShowAlert("Pop up", "3", context);

        //4
        values.put(KEY_LONGITUDE, incident.getLongitude());
        ShowAlert("Pop up", "4", context);

        //5
        values.put(KEY_CATEGORY, incident.getCategory());
        ShowAlert("Pop up", "5", context);

        //6
        db.insert(TABLE_INCIDENTS, null, values);
        //ShowAlert("Pop up", "6", "OK", context);

        //7
        db.close();
        ShowAlert("Pop up", "7", context);

    }*/

    public void editIncident(Incident incident, Context context) {
        //1
        SQLiteDatabase db = this.getWritableDatabase();
        ShowAlert("Pop up", "1", context);

        //2
        ContentValues values = new ContentValues();
        ShowAlert("Pop up", "2", context);

        //3
        values.put(KEY_LATITUDE, incident.getLatitude());
        ShowAlert("Pop up", "3", context);

        //4
        values.put(KEY_LONGITUDE, incident.getLongitude());
        ShowAlert("Pop up", "4", context);

        //5
        values.put(KEY_CATEGORY, incident.getCategory());
        ShowAlert("Pop up", "5", context);

        //6
        db.update(TABLE_INCIDENTS, values, KEY_ID + "=?", new String[]{String.valueOf(incident.get_id())});
        ShowAlert("Pop up", "6", context);

        //7
        db.close();
        ShowAlert("Pop up", "7", context);

    }

    /*
    public Cursor getAllIncidents(Context context) {
        String where = "null";

        Cursor cursor = db.query(true, TABLE_INCIDENTS, null, null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor;
    }
    */

    public ArrayList<Incident> getAllIncidentsByList(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Incident> incidentArrayList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_INCIDENTS, new String[]{KEY_ID, KEY_LATITUDE, KEY_LONGITUDE, KEY_IMAGE, KEY_CATEGORY}, null, null, null, null, null);
        cursor.moveToFirst();

        for (int i=0; i<cursor.getCount(); i++){
            Incident incident = new Incident();
            incident.set_id(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
            incident.setLatitude(cursor.getLong(cursor.getColumnIndex(KEY_LATITUDE)));
            incident.setLongitude(cursor.getLong(cursor.getColumnIndex(KEY_LONGITUDE)));
            incident.setImage(getByteArrayAsBitmap(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE))));
            incident.setCategory(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
            incidentArrayList.add(incident);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return incidentArrayList;
    }

}
