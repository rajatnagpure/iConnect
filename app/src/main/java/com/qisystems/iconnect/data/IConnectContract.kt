package com.qisystems.iconnect.data

import android.net.Uri
import android.provider.BaseColumns

/**
 * API Contract for the Pets app.
 */
object IConnectContract {

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     *
     *
     */

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    val CONTENT_AUTHORITY = "com.qisystems.iconnect"

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    val PATH_LOCATIONS = "locations"
    val DEMO_PLOT_PATH = "demoplots"
    val TADA_PATH = "tadas"
    val DEALER_VISIT_PATH ="dealervisits"
    val TODO_LIST_PATH = "todolists"
    val DISTRIBUTOR_PATH = "distributorlist"
    val ORDERBOOK_PATH = "orderbooklist"
    val FEEDBACK_CHAT_PATH = "feedbackchat"
    class LocationEntry : BaseColumns{
        companion object{

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_LOCATIONS)

            /** Name of database table for pets  */
            val TABLE_NAME = "locations"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID


            /**
             * LATITUDE of the CLIENT.
             * Type: REAL
             */
            val LATITUDE = "longitude"

            /**
             * LonGITUDE of the CLIENT.
             * Type: REAL
             */
            val LONGITUDE = "latitude"

        }

    }

    class DemoPlotEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, DEMO_PLOT_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "demoplots"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID


            /**
             * CURRENT_DATE of the DEMO PLOT.
             * Type: STRING
             */
            val CURRENT_DATE = "current_date"

            /**
             * FARMER NAME of the CLIENT.
             * Type: STRING
             */
            val FARMER_NAME = "farmer_name"

            /**
             * CONTACT of the CLIENT.
             * Type: STRING
             */
            val CONTACT = "contact"

            /**
             * VILLAGE of the CLIENT.
             * Type: STRING
             */
            val VILLAGE = "village"

            /**
             * DISTRICT of the CLIENT.
             * Type: STRING
             */
            val DISTRICT = "district"

            /**
             * STATE of the CLIENT.
             * Type: STRING
             */
            val STATE = "state"

            /**
             * CROPE of the CLIENT.
             * Type: STRING
             */
            val CROPE = "crop"

            /**
             * PRODUCT of the CLIENT.
             * Type: STRING
             */
            val PRODUCT = "product"

            /**
             * DEMO_AREA of the CLIENT.
             * Type: STRING
             */
            val DEMO_AREA = "demo_area"

            /**
             * NEXT_VISIT of the CLIENT.
             * Type: STRING
             */
            val NEXT_VISIT = "next_visit"

            /**
             * IMAGE of the CLIENT.
             * Type: BLOB
             */
            val IMAGE = "image"

        }

    }
    class TADAEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TADA_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "tadas"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID

            /**
             * CURRENT_DATE of the CLIENT.
             * Type: STRING
             */
            val CURRENT_DATE = "current_date"

            /**
             * FROM_PLACE of the CLIENT.
             * Type: STRING
             */
            val FROM_PLACE = "from_place"

            /**
             * TO_PLACE of the CLIENT.
             * Type: STRING
             */
            val TO_PLACE = "to_place"

            /**
             * DISTANCE of the CLIENT.
             * Type: INTEGER
             */
            val DISTANCE = "distance"

            /**
             * MODE of the CLIENT.
             * Type: STRING
             */
            val MODE = "mode"

            /**
             * EXPENSE of the CLIENT.
             * Type: INTEGER
             */
            val EXPENSE = "expense"

            /**
             * OTHER_EXPENSES of the CLIENT.
             * Type: INTEGER
             */
            val OTHER_EXPENSES = "other_expenses"

            /**
            * GPS_CALCULATED_DISTANCE of the CLIENT.
            * Type: REAL
            */
            val GPS_CALCULATED_DISTANCE = "gps_calculated_distance"

            /**
             * START_IMAGE_READING of the CLIENT.
             * Type: BLOB
             */
            val START_IMAGE_READING = "start_reading_image"

            /**
             * END_IMAGE_READING of the CLIENT.
             * Type: BLOB
             */
            val END_IMAGE_READING = "end_reading_image"

        }
    }
    class DealerVisitEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, DEALER_VISIT_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "dealervisits"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID

            /**
             * DEALER of the CLIENT.
             * Type: STRING
             */
            val DEALER = "dealer"

            /**
             * DATE_TIME of the CLIENT.
             * Type: STRING
             */
            val DATE_TIME = "date_time"

            /**
             * LATITUDE of the CLIENT.
             * Type: REAL
             */
            val LATITUDE = "longitude"

            /**
             * LonGITUDE of the CLIENT.
             * Type: REAL
             */
            val LONGITUDE = "latitude"

        }
    }
    class TodoListEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TODO_LIST_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "todolists"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID

            /**
             * DATE of the TODO.
             * Type: STRING
             */
            val DATE = "date"

            /**
             * TODO_LIST of the DATE.
             * Type: STRING
             */
            val TODO_LIST = "todo_list"

        }
    }
    class DistributorEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, DISTRIBUTOR_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "distributorlist"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID


            /**
             * CURRENT_DATE of the DEMO PLOT.
             * Type: STRING
             */
            val DISTRIBUTOR_NAME = "distributor_name"

            /**
             * FARMER NAME of the CLIENT.
             * Type: STRING
             */
            val COMPANY_NAME = "company_name"

            /**
             * CONTACT of the CLIENT.
             * Type: STRING
             */
            val PHONE_NO = "phone_no"

            /**
             * VILLAGE of the CLIENT.
             * Type: STRING
             */
            val ADDRESS = "address"

            /**
             * DISTRICT of the CLIENT.
             * Type: STRING
             */
            val CITY = "city"

            /**
             * STATE of the CLIENT.
             * Type: STRING
             */
            val DISTRICT = "district"

            /**
             * CROPE of the CLIENT.
             * Type: STRING
             */
            val STATE = "state"


        }

    }

    class OrderbookEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, ORDERBOOK_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "orderbooklist"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID


            /**
             * CURRENT_DATE of the DEMO PLOT.
             * Type: STRING
             */
            val AGENCY_NAME = "agency_name"

            /**
             * FARMER NAME of the CLIENT.
             * Type: STRING
             */
            val PRODUCT_NAME = "product_name"

            /**
             * CONTACT of the CLIENT.
             * Type: STRING
             */
            val MRP = "mrp"

            /**
             * VILLAGE of the CLIENT.
             * Type: STRING
             */
            val QUANTITY = "quantity"

            /**
             * DISTRICT of the CLIENT.
             * Type: STRING
             */
            val RATE = "rate"

            /**
             * STATE of the CLIENT.
             * Type: STRING
             */
            val SUBTOTAL = "subtotal"

            /**
             * CROPE of the CLIENT.
             * Type: STRING
             */
            val PAYMENT = "payment"

        }

    }
    class FeedbackEntry : BaseColumns {
        companion object {

            /** The content URI to access the pet data in the provider  */
            val CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, FEEDBACK_CHAT_PATH)

            /** Name of database table for pets  */
            val TABLE_NAME = "feedbackchat"

            /**
             * Unique ID number for the pet (only for use in the database table).
             *
             * Type: INTEGER
             */
            val _ID = BaseColumns._ID

            /**
             * MESSAGE of the LIST.
             * Type: STRING
             */
            val MESSAGE = "message"

            /**
             * authority of the message.
             * Type: INTEGER
             */
            val AUTHORITY = "authority"

        }
    }
}

