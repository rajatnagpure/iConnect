package com.qisystems.iconnect.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.qisystems.iconnect.data.IConnectContract.LocationEntry
import com.qisystems.iconnect.data.IConnectContract.DemoPlotEntry
import com.qisystems.iconnect.data.IConnectContract.TADAEntry
import com.qisystems.iconnect.data.IConnectContract.DealerVisitEntry

/**
 * Database helper for Locations app. Manages database creation and version management.
 */
class IConnectDbHelper
/**
 * Constructs a new instance of [IConnectDbHelper].
 *
 * @param context of the app
 */
    (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * This is called when the database is created for the first time.
     */
    override fun onCreate(db: SQLiteDatabase) {
        // Create a String that contains the SQL statement to create the loactions table
        val SQL_CREATE_LOCATIONS_TABLE = ("CREATE TABLE " + LocationEntry.TABLE_NAME + " ("
                + LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LocationEntry.LATITUDE + " REAL NOT NULL, "
                + LocationEntry.LONGITUDE + " REAL NOT NULL DEFAULT 0);")

        // Create a String that contains the SQL statement to create the demoplots table
        val SQL_CREATE_DEMOPLOTS_TABLE = ("CREATE TABLE " + DemoPlotEntry.TABLE_NAME + " ("
                + DemoPlotEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DemoPlotEntry.CURRENT_DATE + " TEXT NOT NULL, "
                + DemoPlotEntry.FARMER_NAME + " TEXT NOT NULL, "
                + DemoPlotEntry.CONTACT + " TEXT NOT NULL, "
                + DemoPlotEntry.VILLAGE + " TEXT NOT NULL, "
                + DemoPlotEntry.DISTRICT + " TEXT NOT NULL, "
                + DemoPlotEntry.STATE + " TEXT NOT NULL, "
                + DemoPlotEntry.CROPE + " TEXT NOT NULL, "
                + DemoPlotEntry.PRODUCT + " TEXT NOT NULL, "
                + DemoPlotEntry.DEMO_AREA + " TEXT NOT NULL, "
                + DemoPlotEntry.NEXT_VISIT + " TEXT, "
                + DemoPlotEntry.IMAGE + " BLOB NOT NULL);")

        // Create a String that contains the SQL statement to create the tadas table
        val SQL_CREATE_TADAS_TABLE = ("CREATE TABLE " + TADAEntry.TABLE_NAME + " ("
                + TADAEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TADAEntry.CURRENT_DATE + " TEXT NOT NULL, "
                + TADAEntry.FROM_PLACE + " TEXT NOT NULL, "
                + TADAEntry.TO_PLACE + " TEXT NOT NULL, "
                + TADAEntry.DISTANCE + " INTEGER, "
                + TADAEntry.MODE + " TEXT NOT NULL, "
                + TADAEntry.GPS_CALCULATED_DISTANCE + " REAL NOT NULL, "
                + TADAEntry.START_IMAGE_READING + " BLOB NOT NULL, "
                + TADAEntry.END_IMAGE_READING + " BLOB NOT NULL, "
                + TADAEntry.EXPENSE + " INTEGER NOT NULL, "
                + TADAEntry.OTHER_EXPENSES + " INTEGER NOT NULL);")

        // Create a String that contains the SQL statement to create the dealervisits table
        val SQL_CREATE_DEALERVISITS_TABLE = ("CREATE TABLE " + DealerVisitEntry.TABLE_NAME + " ("
                + DealerVisitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DealerVisitEntry.DEALER + " TEXT NOT NULL, "
                + DealerVisitEntry.DATE_TIME + " TEXT NOT NULL, "
                + DealerVisitEntry.LATITUDE + " REAL NOT NULL, "
                + DealerVisitEntry.LONGITUDE + " REAL NOT NULL DEFAULT 0);")

        // Create a String that contains the SQL statement to create the todolist table
        val SQL_CREATE_TODOLIST_TABLE = ("CREATE TABLE " + IConnectContract.TodoListEntry.TABLE_NAME + " ("
                + IConnectContract.TodoListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IConnectContract.TodoListEntry.DATE + " TEXT NOT NULL, "
                + IConnectContract.TodoListEntry.TODO_LIST + " TEXT NOT NULL);")


        val SQL_CREATE_DISTRIBUTOR_TABLE = ("CREATE TABLE " + IConnectContract.DistributorEntry.TABLE_NAME + " ("
                + IConnectContract.DistributorEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IConnectContract.DistributorEntry.DISTRIBUTOR_NAME + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.COMPANY_NAME + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.PHONE_NO + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.ADDRESS + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.CITY + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.DISTRICT + " TEXT NOT NULL, "
                + IConnectContract.DistributorEntry.STATE + " TEXT NOT NULL);")

        val SQL_CREATE_ORDERBOOK_TABLE = ("CREATE TABLE " + IConnectContract.OrderbookEntry.TABLE_NAME + " ("
                + IConnectContract.OrderbookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IConnectContract.OrderbookEntry.AGENCY_NAME + " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.MRP+ " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.QUANTITY + " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.RATE + " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.SUBTOTAL + " TEXT NOT NULL, "
                + IConnectContract.OrderbookEntry.PAYMENT + " BLOB NOT NULL);")

        // Create a String that contains the SQL statement to create the loactions table
        val SQL_CREATE_FEEDBACK_CHAT_TABLE = ("CREATE TABLE " + IConnectContract.FeedbackEntry.TABLE_NAME + " ("
                + IConnectContract.FeedbackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IConnectContract.FeedbackEntry.AUTHORITY + " INTEGER NOT NULL, "
                + IConnectContract.FeedbackEntry.MESSAGE + " TEXT NOT NULL);")

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_LOCATIONS_TABLE)
        db.execSQL(SQL_CREATE_DEMOPLOTS_TABLE)
        db.execSQL(SQL_CREATE_TADAS_TABLE)
        db.execSQL(SQL_CREATE_DEALERVISITS_TABLE)
        db.execSQL(SQL_CREATE_TODOLIST_TABLE)
        db.execSQL(SQL_CREATE_DISTRIBUTOR_TABLE)
        db.execSQL(SQL_CREATE_ORDERBOOK_TABLE)
        db.execSQL(SQL_CREATE_FEEDBACK_CHAT_TABLE)
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

    companion object {

        val LOG_TAG = IConnectDbHelper::class.java.simpleName

        /** Name of the database file  */
        private val DATABASE_NAME = "iConnect.db"

        /**
         * Database version. If you change the database schema, you must increment the database version.
         */
        private val DATABASE_VERSION = 1
    }
}