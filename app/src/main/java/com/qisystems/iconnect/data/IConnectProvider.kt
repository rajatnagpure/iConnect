package com.qisystems.iconnect.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.content.UriMatcher
import android.content.ContentUris
import android.util.Log


/**
 * [ContentProvider] for Locationsfetchs app.
 */
class IConnectProvider : ContentProvider() {

    /**
     * Initialize the provider and the database helper object.
     */
    /*database helper object*/
    private var mDbHelper: IConnectDbHelper? = null

    /** URI matcher code for the content URI for the locations table  */
    private val LOCATIONS = 100

    /** URI matcher code for the content URI for a single locations in the locations table  */
    private val LOCATION_ID = 101

    /** URI matcher code for the content URI for the demoplots table  */
    private val DEMOPLOTS = 200

    /** URI matcher code for the content URI for a single demoplots in the demoplots table  */
    private val DEMOPLOT_ID = 201

    /** URI matcher code for the content URI for the tadas table  */
    private val TADAS = 300

    /** URI matcher code for the content URI for a single tadas in the tadas table  */
    private val TADA_ID = 301

    /** URI matcher code for the content URI for the dealer_visits table  */
    private val DEALER_VISITS = 400

    /** URI matcher code for the content URI for a single dealer_visits in the dealer_visits table  */
    private val DEALER_VISIT_ID = 401

    /** URI matcher code for the content URI for the dealer_visits table  */
    private val TODO_LIST = 600

    /** URI matcher code for the content URI for a single dealer_visits in the dealer_visits table  */
    private val TODO_LIST_ID = 601

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private val DISTRIBUTORS = 500

    /** URI matcher code for the content URI for a single demoplots in the demoplots table  */
    private val DISTRIBUTOR_ID = 501

    private val ORDERBOOKS = 700

    /** URI matcher code for the content URI for a single demoplots in the demoplots table  */
    private val ORDERBOOK_ID = 701

    /** URI matcher code for the content URI for the dealer_visits table  */
    private val FEEDBACKCHATS = 800

    /** URI matcher code for the content URI for a single dealer_visits in the dealer_visits table  */
    private val FEEDBACKCHAT_ID = 801


    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.PATH_LOCATIONS, LOCATIONS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.PATH_LOCATIONS + "/#", LOCATION_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DEMO_PLOT_PATH, DEMOPLOTS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DEMO_PLOT_PATH + "/#", DEMOPLOT_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.TADA_PATH, TADAS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.TADA_PATH + "/#", TADA_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DEALER_VISIT_PATH, DEALER_VISITS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DEALER_VISIT_PATH + "/#", DEALER_VISIT_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.TODO_LIST_PATH, TODO_LIST)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.TODO_LIST_PATH + "/#", TODO_LIST_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DISTRIBUTOR_PATH, DISTRIBUTORS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.DISTRIBUTOR_PATH + "/#", DISTRIBUTOR_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.ORDERBOOK_PATH, ORDERBOOKS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.ORDERBOOK_PATH + "/#", ORDERBOOK_ID)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.FEEDBACK_CHAT_PATH, FEEDBACKCHATS)

        addURI(IConnectContract.CONTENT_AUTHORITY, IConnectContract.FEEDBACK_CHAT_PATH + "/#", FEEDBACKCHAT_ID)

    }

    override fun onCreate(): Boolean {
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = IConnectDbHelper(context)
        return true
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get readable database
        val database = mDbHelper?.readableDatabase

        // This cursor will hold the result of the query
        var cursor: Cursor ?= null

        // Figure out if the URI matcher can match the URI to a specific code
        val match = sUriMatcher.match(uri)
        when (match) {
            LOCATIONS -> {
                cursor = database!!.query(IConnectContract.LocationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            LOCATION_ID -> {
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = IConnectContract.LocationEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database!!.query(IConnectContract.LocationEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            DEMOPLOTS -> {
                cursor = database!!.query(IConnectContract.DemoPlotEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            DEMOPLOT_ID -> {
                selection = IConnectContract.DemoPlotEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.DemoPlotEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            TADAS -> {
                cursor = database!!.query(IConnectContract.TADAEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            TADA_ID -> {
                selection = IConnectContract.TADAEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.TADAEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            DEALER_VISITS -> {
                cursor = database!!.query(IConnectContract.DealerVisitEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            DEALER_VISIT_ID -> {
                selection = IConnectContract.DealerVisitEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.DealerVisitEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            TODO_LIST -> {
                cursor = database!!.query(IConnectContract.TodoListEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            TODO_LIST_ID -> {
                selection = IConnectContract.TodoListEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.TodoListEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }

            DISTRIBUTORS -> {
                cursor = database!!.query(IConnectContract.DistributorEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            DISTRIBUTOR_ID -> {
                selection = IConnectContract.DistributorEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.DistributorEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }

            ORDERBOOKS -> {
                cursor = database!!.query(IConnectContract.OrderbookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            ORDERBOOK_ID -> {
                selection = IConnectContract.OrderbookEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.OrderbookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }

            FEEDBACKCHATS -> {
                cursor = database!!.query(IConnectContract.FeedbackEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            FEEDBACKCHAT_ID -> {
                selection = IConnectContract.FeedbackEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())

                cursor = database!!.query(IConnectContract.FeedbackEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder)
            }
            else -> throw IllegalArgumentException("Cannot query unknown URI $uri")
        }// For the PETS code, query the pets table directly with the given
        // projection, selection, selection arguments, and sort order. The cursor
        // could contain multiple rows of the pets table.
        // TODO: Perform database query on pets table
        return cursor
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        val match = sUriMatcher.match(uri)
        when (match) {
            LOCATIONS -> return insertLocation(uri, contentValues)
            DEMOPLOTS -> return insertDemoPlot(uri, contentValues)
            TADAS -> return insertTADA(uri, contentValues)
            DEALER_VISITS -> return insertDealerVisit(uri, contentValues)
            TODO_LIST -> return insertTodoList(uri, contentValues)
            DISTRIBUTORS -> return insertDistributor(uri, contentValues)
            ORDERBOOKS -> return insertOrderbook(uri, contentValues)
            FEEDBACKCHATS -> return insertFeedbackChat(uri, contentValues)
            else -> throw IllegalArgumentException("Insertion is not supported for $uri")
        }
    }
    /** Tag for the log messages  */
    val LOG_TAG = IConnectProvider::class.java!!.simpleName

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private fun insertTodoList(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.TodoListEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private fun insertLocation(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.LocationEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }
    private fun insertDemoPlot(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.DemoPlotEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }
    private fun insertTADA(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.TADAEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }
    private fun insertDealerVisit(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.DealerVisitEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    private fun insertDistributor(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.DistributorEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    private fun insertFeedbackChat(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.FeedbackEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    private fun insertOrderbook(uri: Uri, values: ContentValues?): Uri? {

        //get writable database
        val database = mDbHelper?.writableDatabase
        // Insert the new pet with the given values
        val id = database!!.insert(IConnectContract.OrderbookEntry.TABLE_NAME, null, values)
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id.equals(-1)) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri)
            return null
        }

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id)
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var selection = selection
        var selectionArgs = selectionArgs
        // Get writeable database
        val database = mDbHelper!!.writableDatabase

        val match = sUriMatcher.match(uri)
        when (match) {
            LOCATIONS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.LocationEntry.TABLE_NAME, selection, selectionArgs)
            LOCATION_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.LocationEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.LocationEntry.TABLE_NAME, selection, selectionArgs)
            }
            DEMOPLOTS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.DemoPlotEntry.TABLE_NAME, selection, selectionArgs)
            DEMOPLOT_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.DemoPlotEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.DemoPlotEntry.TABLE_NAME, selection, selectionArgs)
            }
            TADAS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.TADAEntry.TABLE_NAME, selection, selectionArgs)
            TADA_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.TADAEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.TADAEntry.TABLE_NAME, selection, selectionArgs)
            }
            DEALER_VISITS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.DealerVisitEntry.TABLE_NAME, selection, selectionArgs)
            DEALER_VISIT_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.DealerVisitEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.DealerVisitEntry.TABLE_NAME, selection, selectionArgs)
            }
            TODO_LIST ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.TodoListEntry.TABLE_NAME, selection, selectionArgs)
            TODO_LIST_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.TodoListEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.TodoListEntry.TABLE_NAME, selection, selectionArgs)
            }

            DISTRIBUTORS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.DistributorEntry.TABLE_NAME, selection, selectionArgs)
            DISTRIBUTOR_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.DistributorEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.DistributorEntry.TABLE_NAME, selection, selectionArgs)
            }

            ORDERBOOKS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.OrderbookEntry.TABLE_NAME, selection, selectionArgs)
            ORDERBOOK_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.OrderbookEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.OrderbookEntry.TABLE_NAME, selection, selectionArgs)
            }

            FEEDBACKCHATS ->
                // Delete all rows that match the selection and selection args
                return database.delete(IConnectContract.FeedbackEntry.TABLE_NAME, selection, selectionArgs)
            FEEDBACKCHAT_ID -> {
                // Delete a single row given by the ID in the URI
                selection = IConnectContract.FeedbackEntry._ID + "=?"
                selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                return database.delete(IConnectContract.FeedbackEntry.TABLE_NAME, selection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Deletion is not supported for $uri")
        }
    }
    /**
     * Returns the MIME type of data for the content URI.
     */
    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {

        /** Tag for the log messages  */
        val LOG_TAG = IConnectProvider::class.java.simpleName
    }
}