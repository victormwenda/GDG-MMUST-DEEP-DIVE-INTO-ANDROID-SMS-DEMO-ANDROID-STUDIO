package com.marvik.apps.gdgmmustsmsdemo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by victor_mwenda on 11/16/2014. 4:50pm - 5:50pm
 * Phone: 0718034449
 * Email: vmwenda.vm@gmail.com
 * 	other: victor@merusongs.com
 * Website: http://www.merusongs.com
 */
public class MessageProvider extends ContentProvider {
    public static final Uri GDG_MMUST_SAVED_SMS_URI;
    public static final String AUTH;
    public static final int MESSAGES_INSERT;
    public static final UriMatcher uriMatcher;

    private Cursor cursor;
    MessageDatabase messageDatabase;
    SQLiteDatabase sqLiteDatabase;

    private static final String MESSAGES_DATABASE_NAME ;//Database name
    private static final int MESSAGE_DATABASE_VERSION;//Database version

    private static final String MESSAGES_TABLE_NAME;//Table Name

    public static final String ID ;
    public static final String ADDRESS;
    public static final String BODY;
    public static final String SNIPPET;
    public static final String STATUS;

    static{

       MESSAGES_DATABASE_NAME ="Messages"; //Database name
       MESSAGE_DATABASE_VERSION=1;//Database version

        MESSAGES_TABLE_NAME="SavedMessages";

        ID ="_id";
        ADDRESS="recipient";
        BODY="body";
        SNIPPET="snippet";
        STATUS="status";

        AUTH= "com.marvik.apps.gdgmmustsmsdemo.MessageProvider";
        GDG_MMUST_SAVED_SMS_URI = Uri.parse("content://"+AUTH+"/"+MESSAGES_TABLE_NAME);
        MESSAGES_INSERT=1;

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH,MESSAGES_TABLE_NAME,MESSAGES_INSERT);
    }
    @Override
    public boolean onCreate() {
        openDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        getReadableDatabase();
        if(uriMatcher.match(uri)==MESSAGES_INSERT){
           cursor = sqLiteDatabase.query(MESSAGES_TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        if(uriMatcher.match(uri)==MESSAGES_INSERT){
            uri = GDG_MMUST_SAVED_SMS_URI;
        }
        return uri.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        getReadableDatabase();
        if(uriMatcher.match(uri)==MESSAGES_INSERT){
            sqLiteDatabase.insert(MESSAGES_TABLE_NAME,null,values);
        }

        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        getWritableDatabase();;
        int numRows = 0;
        if(uriMatcher.match(uri)==MESSAGES_INSERT){
            numRows = sqLiteDatabase.delete(MESSAGES_TABLE_NAME,selection,selectionArgs);
        }
        return numRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        getWritableDatabase();
        int numRows = 0;
        if(uriMatcher.match(uri)==MESSAGES_INSERT){
            numRows = sqLiteDatabase.update(MESSAGES_TABLE_NAME,values,selection,selectionArgs);

        }
        return numRows;
    }

    /**
     *  METHODS
     * */

    private void openDatabase(){
        messageDatabase = new MessageDatabase(getContext());
    }
    private void getWritableDatabase(){
        if(messageDatabase==null){
            openDatabase();
        }

        sqLiteDatabase = messageDatabase.getWritableDatabase();
    }
    private void getReadableDatabase(){
        if(messageDatabase==null){
            openDatabase();
        }

        sqLiteDatabase = messageDatabase.getReadableDatabase();
    }

     private class MessageDatabase extends SQLiteOpenHelper{

        public MessageDatabase(Context context){

            super(context, MessageProvider.MESSAGES_DATABASE_NAME, null, MessageProvider.MESSAGE_DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS "+MessageProvider.MESSAGES_TABLE_NAME +"("
                            +MessageProvider.ID +" integer primary key autoincrement,"
                            +MessageProvider.ADDRESS+" text not null, "
                            +MessageProvider.BODY+" text not null, "
                            +MessageProvider.SNIPPET+" text not null, "
                            +MessageProvider.STATUS+" integer not null);" );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+MessageProvider.MESSAGES_TABLE_NAME);
            onCreate(db);
        }
    }
}

