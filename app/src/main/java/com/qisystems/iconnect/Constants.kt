package com.qisystems.iconnect

class Constants{
    companion object {
        //geofencing constants
        const val GEOFENCE_RADIUS = 100.0f // in meters
        const val GEOFENCE_LATITUDE = 21.1388261
        const val GEOFENCE_LONGITUDE = 79.0777526

        // fetch Location utils
        const val UPDATE_INTERVAL = (50 * 1000).toLong()  /* 50 secs */
        const val FASTEST_INTERVAL: Long = 10 * 1000 /* 10 sec */

        //image storage path
        const val APP_PATH_SD_CARD = "/iConnect/"
        const val APP_THUMBNAIL_PATH_SD_CARD = "profileimage"
        const val DEMO_PLOT_IMAGE = "demoplotsImages"
        const val TADA_IMAGES = "tadaimages"

        //Distributors name
        const val DEALER_1 = "A J Ramoji"
        const val DEALER_2 = "R K Banerjee"
        const val DEALER_3 = "Shivam Singh"
        const val DEALER_4 = "Praphul Patel"
        const val DEALER_5 = "Pramod Vyas"
        const val DEALER_6 = "Jay Asati"
        const val DEALER_7 = "Ramesh Dhamde"
        const val DEALER_8 = "Rahat Khan"

        //Products Name
        const val PRODUCT_1 = "Yashoda Seeds"
        const val PRODUCT_2 = "R P Seeds"
        const val PRODUCT_3 = "H M T"
        const val PRODUCT_4 = "Shree Ram"
        const val PRODUCT_5 = "Bashmati"
        const val PRODUCT_6 = "Meera Rice"
        const val PRODUCT_7 = "Krishna Seeds"

        //Agency List
        const val AGENCY_1 = "Tirupati Agro"
        const val AGENCY_2 = "Padma Agro"
        const val AGENCY_3 = "Amar Agro"
        const val AGENCY_4 = "A G L Agro Ltd"
        const val AGENCY_5 = "Vidarbha Agro"
        const val AGENCY_6 = "Shyam Agro"
        const val AGENCY_7 = "Balaji Agro"
        const val AGENCY_8 = "Ganraj Agro"

        // Networking upload paths
        const val LOGIN_POST_URL = "http://192.168.29.160/salesERP1/android_api/v1/userLogin.php"
        const val SIGNUP_POST_URL = "http://192.168.29.160/salesERP1/android_api/v1/registerUser.php"
        const val UPLOAD_PROFILE_IMAGE = "http://192.168.29.160/salesERP1/android_api/v1/uploadImg.php"
        const val UPLOAD_TADA = "http://192.168.29.160/salesERP1/android_api/v1/tadaInsert.php"
        const val UPLOAD_DEALER_VISIT = "http://192.168.29.160/salesERP1/android_api/v1/dealerVisitInsert.php"
        const val UPLOAD_DEMOPLOTS = "http://192.168.29.160/salesERP1/android_api/v1/demoplotInsert.php"
        const val UPLOAD_TODO_LIST = "http://192.168.29.160/salesERP1/android_api/v1/todoListInsert.php"
        const val UPLOAD_ORDER_BOOK = "http://192.168.29.160/salesERP1/android_api/v1/orderBookInsert.php"
        const val UPLOAD_DISTRIBUTOR = "http://192.168.29.160/salesERP1/android_api/v1/distributorInsert.php"
        const val UPLOAD_SENDERS_CHAT = "http://192.168.29.160/salesERP1/android_api/v1/faqInsert.php"
        const val UPLOAD_LOCATIONS = "http://192.168.29.160/salesERP1/android_api/v1/locationInsert.php"

        //Networking download paths
        const val DOWNLOAD_TADA_LIST = "http://192.168.29.160/salesERP1/android_api/v1/tadaFetch.php"
        const val DOWNLOAD_DEMO_PLOTS = "http://192.168.29.160/salesERP1/android_api/v1/demoplotFetch.php"
        const val DOWNLOAD_DISTRIBUTOR = "http://192.168.29.160/salesERP1/android_api/v1/distributorFetch.php"
        const val DOWNLOAD_ORDERBOOK = "http://192.168.29.160/salesERP1/android_api/v1/orderBookFetch.php"
        const val DOWNLOAD_SCHEME = "http://simplifiedcoding.net/demos/marvel/"
    }
}