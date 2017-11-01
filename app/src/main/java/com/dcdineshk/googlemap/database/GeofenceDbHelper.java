package com.dcdineshk.googlemap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dcdineshk.googlemap.activity.App;

/**
 * Created by DC\dineshk on 26/10/17.
 */

public class GeofenceDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="geofences.db";

    public static final String SQL_CREATES_ENTRIES=
            "CREATE TABLE " + GeofenceContract.GeofenceEntry.TABLE_NAME + " (" +
                    GeofenceContract.GeofenceEntry._ID + " INTEGER PRIMARY KEY," +
                    GeofenceContract.GeofenceEntry.COLUMN_NAME_KEY + " TEXT," +
                    GeofenceContract.GeofenceEntry.COLUMN_NAME_LAT + " TEXT," +
                    GeofenceContract.GeofenceEntry.COLUMN_NAME_LNG + " TEXT," +
                    GeofenceContract.GeofenceEntry.COLUMN_NAME_EXPIRES + " TEXT)";

    private static final String SQL_DELETE_ENTRIES=
            "DROP TABLE IF EXISTS " + GeofenceContract.GeofenceEntry.TABLE_NAME;

    public GeofenceDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATES_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }

    public static GeofenceDbHelper get(){
        return new GeofenceDbHelper(App.getInstance());
    }
}
