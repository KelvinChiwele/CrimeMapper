package com.techart.crimemapper.constants;

/**
 * Created by brad on 2017/02/05.
 * Stores firebase node keys and other constants to prevent spelling mistakes in different part of
 * the apps
 */

public class Constants {
    //Profile
    public static final String USER_URL = "userUrl";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "surname";
    public static final String NRC = "nrc";
    public static final String PHONE = "phone";
    public static final String RESIDENCE = "residence";
    public static final String LOCATION = "location";
    public static final String STATION = "station";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String OCCURANCES_KEY = "occurences";
    public static final String STATION_KEY = "stations";
    public static final String COMPLAINANT_KEY = "complainant";
    public static final String TIME_CREATED = "createdAt";

    public static final String SUBJECT = "subject";
    public static final String SUSPECT_KEY = "suspects";

    public static final String ICON = "icon";
    public static final String PLACE = "place";
    public static final String PARTICULARS_OF_OFFENCE = "particularOfOffence";
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String MODE_OF_SUBMISSION = "modeOfSubmission";

    // Name of Notification Channel for verbose notifications of background work
    public static final CharSequence VERBOSE_NOTIFICATION_CHANNEL_NAME =
            "Verbose WorkManager Notifications";
    public static final String PROVINCE = "province";
    public static String VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
            "Shows notifications whenever work starts";
    public static final CharSequence NOTIFICATION_TITLE = "Progress Report";
    public static final String CHANNEL_ID = "VERBOSE_NOTIFICATION";
    public static final int NOTIFICATION_ID = 1;
}
