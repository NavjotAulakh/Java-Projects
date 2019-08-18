package ca.uoit.projectxiphos;

/* Project Xiphos
 * 2/12/2018
 * Navjot Aulakh ID#100488741
 * Nicolas Zarfino ID100599899
 */
/**
 This class takes care of the creation of the database table and population of that table
 with sample product data
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UserDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERVION =1;
    private static final String DATABASE_NAME = "UserInfo.db";
    private static final String TABLE_USERS = "UserTable";
    private static final String TABLE_PASSWORDS = "PasswordTable";
    private static final String KEY_PID = "userId";
    private static final String KEY_NAME = "userName";
    private static final String KEY_PASS = "userPass";
    private static final String KEY_FIRST = "firstName";
    private static final String KEY_LAST = "lastName";
    private static final String KEY_EMAIL = "userEmail";
    private static final String KEY_CHECK = "checkLocal";
    private static final String KEY_PASSID = "passId";
    private static final String KEY_PASSUSER = "passUser";
    private static final String KEY_PASSTITLE = "passTitle";
    private static final String KEY_PASSWORD = "password";

    private static final String DBN = "FileService.db";
    private static final String TABLE_FILES = "FileTable";
    private static final String KEY_ID = "id";
    private static final String FILEN = "name";
    private static final String KEY_SIZE = "size";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_DATA = "data";

    public UserDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERVION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_PID + " Integer PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME +  " String NOT NULL,"
                + KEY_PASS +  " String NOT NULL,"
                + KEY_FIRST +  " String NOT NULL,"
                + KEY_LAST +  " String NOT NULL,"
                + KEY_EMAIL + " String NOT NULL,"
                + KEY_CHECK + " Integer NOT NULL" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_PASSWORDS_TABLE = "CREATE TABLE " + TABLE_PASSWORDS + "("
                + KEY_PASSID + " Integer PRIMARY KEY AUTOINCREMENT,"
                + KEY_PASSUSER +  " String NOT NULL,"
                + KEY_PASSTITLE +  " String NOT NULL,"
                + KEY_PASSWORD +  " String NOT NULL" +")";
        db.execSQL(CREATE_PASSWORDS_TABLE);

        String CREATE_TABLE = "CREATE TABLE " + TABLE_FILES + "("
                + KEY_ID + " Integer PRIMARY KEY,"
                + FILEN +  " Text,"
                + KEY_SIZE +  " Integer,"
                + KEY_OWNER +  " Text,"
                + KEY_DATA +  " varbinary(1024)" +")";
        // + ";" ;
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table " + TABLE_USERS + ";" );
        db.execSQL("Drop table " + TABLE_PASSWORDS + ";" );
        db.execSQL("Drop table " + TABLE_FILES + ";" );
        this.onCreate(db);
    }
    /**
     The method addProduct will function as  for inserting a new product into the database
     */
    public void addUser(User user){

        ContentValues values= new ContentValues();
        //adding product details
        values.put(KEY_NAME, user.getName());
        values.put(KEY_PASS, user.getPass());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_FIRST, user.getName());
        values.put(KEY_LAST, user.getPass());
        values.put(KEY_CHECK, user.getEmail());

        SQLiteDatabase db = getWritableDatabase();

        //inserting Row
        db.insert(TABLE_USERS,null,values);
        db.close();
    }

    /**
     The method addPassword will function as  for inserting a new password into the database
     */
    public void addPassword(Password password){

        ContentValues values= new ContentValues();
        //adding product details
        values.put(KEY_PASSUSER, password.getUser());
        values.put(KEY_PASSTITLE, password.getTitle());
        values.put(KEY_PASSWORD, password.getPass());

        SQLiteDatabase db = getWritableDatabase();

        //inserting Row
        db.insert(TABLE_PASSWORDS,null,values);
        db.close();
    }

    /**
     The method addProduct will function as  for inserting a new product into the database
     */
    public boolean checkUser(User user){
        boolean check;
        String username = user.getName();
        User checkuser = getUser(username);
        if (checkuser == null) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    /**
     The method addProduct will function as  for inserting a new product into the database
     */
    public boolean checkPassword(Password pass, String username){
        boolean check;
        String query = "Select * FROM " + TABLE_PASSWORDS + " WHERE " + KEY_PASSUSER + " = " + "'"
                + username + "' AND " + KEY_PASSTITLE + " = " + "'" + pass.getTitle() +"';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Password checkpass;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            checkpass = new Password(cursor.getString(1), cursor.getString(2), cursor.getString(3));
            cursor.close();
        } else {
            checkpass = null;
        }
        db.close();
        if (checkpass == null) {
            check = false;
        } else {
            check = true;
        }
        return check;
    }

    public List<String> getPassData() {
        // TODO Auto-generated method stub
        String[] coloumn = new String[]{KEY_PASSID, KEY_PASSTITLE,KEY_PASSWORD};
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_PASSWORDS, coloumn, null, null, null, null, null);
        List<String> results = new ArrayList<String>();
        String result = " ";
        int iRow =c.getColumnIndex(KEY_PASSID);
        int iTitle =c.getColumnIndex(KEY_PASSTITLE);
        int iPasswords =c.getColumnIndex(KEY_PASSWORD);
        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            result = result + c.getString(iRow)+" "+ c.getString(iTitle)+" = "+ c.getString(iPasswords)+"\n";
            results.add(result);
        }

        return results;
    }

    public List<String> getMatch(String pass) {
        // TODO Auto-generated method stub
        String[] coloumn = new String[]{KEY_PASSID, KEY_PASSTITLE,KEY_PASSWORD};
        String whereC = coloumn[1] + " = " + pass;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_PASSWORDS, coloumn, whereC, null, null, null, null);
        List<String> results = new ArrayList<String>();
        String result = " ";
        int iRow =c.getColumnIndex(KEY_PASSID);
        int iTitle =c.getColumnIndex(KEY_PASSTITLE);
        int iPasswords =c.getColumnIndex(KEY_PASSWORD);
        for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
            result = result + c.getString(iRow)+" "+ c.getString(iTitle)+" = "+ c.getString(iPasswords)+"\n";
            results.add(result);
        }

        return results;
    }

    /**
     The method findAllProducts will function for querying the database, finding all products
     */
    public String findAllUsers(){
        String result = "";
        String query = "Select * from " + TABLE_USERS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            String result_2 = cursor.getString(2);
            String result_3 = cursor.getString(3);
            result += String.valueOf(result_0) + " " + result_1 + " " + result_2 + " " +
                    String.valueOf(result_3) + System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
    /**
     Gets the count of number of products there is in the database
     */
    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("Select * from " + TABLE_USERS,null);
        Log.i("Number of Records"," :: "+c.getCount());
        return c.getCount();
    }
    /**
     getProduct will fuction to select a specific product form the database and return it
     */
    public User getUser(String name){
        String query = "Select * FROM " + TABLE_USERS + " WHERE " + KEY_NAME + " = " + "'" + name + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user;
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            user = new User(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5), cursor.getInt(6));
            cursor.close();
        } else {
            user = null;
        }
        db.close();
        return user;
    }
    /**
     The method deleteProduct will function for deleting a product from the database
     *
    public void deleteProduct(User user) {
        SQLiteDatabase db = getWritableDatabase();
        int pid = MainActivity.currentProduct;
        System.out.print(BrowseProductsActivity.currentProduct);
        String deleterows = "delete from " + TABLE_PRODUCTS + " where " + KEY_PID + "='" + pid + "';" ;
        db.execSQL(deleterows);
        db.execSQL("UPDATE " + TABLE_PRODUCTS + " set " + KEY_PID + " = (" + KEY_PID + "-1) where " + KEY_PID + " > " + String.valueOf(pid));
        Cursor cursor = db.query(TABLE_PRODUCTS, new String[]{"MAX(" +KEY_PID + ")"}, null, null, null, null, null);
        cursor.moveToNext(); // to move the cursor to first record
        System.out.println(DatabaseUtils.dumpCursorToString(cursor));
        int max = getMaxColumnData();

        String fix = "UPDATE SQLITE_SEQUENCE SET seq =" + String.valueOf(max) + " WHERE name = 'ProductTable';";
        db.execSQL(fix);
        if (BrowseProductsActivity.productCount == 0) {
            String reset = "TRUNCATE 'ProductTable';";
            db.execSQL(reset);
        }
        db.close();
    }*/

    public int getMaxColumnData() {

        SQLiteDatabase db = getWritableDatabase();
        final SQLiteStatement stmt = db
                .compileStatement("SELECT MAX(productId) FROM ProductTable");
        return (int) stmt.simpleQueryForLong();
    }



    public void addFile(CFile file){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values= new ContentValues();
        //adding time
        values.put(KEY_NAME,file.name);
        values.put(KEY_SIZE,file.size);
        values.put(KEY_OWNER,file.owner);
        values.put(KEY_DATA,file.data);

        //inserting Row
        db.insert(TABLE_FILES,null,values);
        db.close();
    }

    public void deleteAllFiles( ) {
        SQLiteDatabase db = getWritableDatabase();
        String deleterows = "delete from " + TABLE_FILES + ";" ;
        db.execSQL(deleterows);
        db.close();
    }

    public ArrayList<CFile> getListOfFiles( ){
        SQLiteDatabase db = getWritableDatabase();
        String readRows = "select * from " + TABLE_FILES + ";" ;
        ArrayList<CFile> files = new ArrayList<CFile>();
        Cursor dbc = db.query(TABLE_FILES, null, null, null, null, null, null);
        int count = dbc.getCount();
        int iterator = 0;
        while (iterator < count)
        {

            CFile temp = new CFile();
            temp.name = dbc.getString(dbc.getColumnIndex(FILEN));
            temp.owner = dbc.getString(dbc.getColumnIndex(KEY_OWNER));
            temp.size = dbc.getInt(dbc.getColumnIndex(KEY_SIZE));
            temp.data = dbc.getBlob(dbc.getColumnIndex(KEY_DATA));
            files.add(temp);
            iterator++;
            dbc.moveToNext();
        }

        db.close();
        return files;
    }
}
