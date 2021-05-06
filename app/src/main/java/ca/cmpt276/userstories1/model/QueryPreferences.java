package ca.cmpt276.userstories1.model;

import android.content.Context;
import android.preference.PreferenceManager;

/*********************************************************************
// *********************************************************************
// Helps get saved preferences
// Code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
// CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip
// *********************************************************************
*/

public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_SEARCH_QUERY, query)
                .apply();
    }
}
