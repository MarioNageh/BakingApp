package android.mohamedalaa.com.bakingapp.utils;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mohamed on 7/23/2018.
 *
 */
public class SharedPrefUtils {

    /**
     * Doing below approach to ensure we have the same widget with different lists,
     *      not all widget with the same recipe.
     *
     * Same sharedPref key generation methodology is done in corresponding get method.
     */
    synchronized public static void setWidgetChosenRecipeIndex(Context context,
                                                               int appWidgetId,
                                                               int index){
        SharedPreferences sharedPref = context.getSharedPreferences(
                SharedPrefConstants.SH_PREF_FILE_WIDGET_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        String keySpecificToAppWidgetId
                = SharedPrefConstants.SH_PREF_KEY_CHOSEN_RECIPE_INDEX + "_" + appWidgetId;

        editor.putInt(keySpecificToAppWidgetId,
                index);

        editor.apply();
    }

    synchronized public static int getWidgetChosenRecipeIndex(int appWidgetId, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                SharedPrefConstants.SH_PREF_FILE_WIDGET_NAME, Context.MODE_PRIVATE);

        String keySpecificToAppWidgetId
                = SharedPrefConstants.SH_PREF_KEY_CHOSEN_RECIPE_INDEX + "_" + appWidgetId;

        return sharedPref.getInt(keySpecificToAppWidgetId,
                SharedPrefConstants.SH_PREF_VALUE_CHOSEN_RECIPE_INDEX);
    }

    private class SharedPrefConstants {

        /**
         * Overview
         * It's a private file to determine which ingredient to be shown in the ingredients list
         * widget, which can be changed in 3 places
         *      A- On widget creation
         *      B- On shown Widget Button
         *      C- In Settings
         */
        static final String SH_PREF_FILE_WIDGET_NAME = "SH_PREF_FILE_WIDGET_NAME";

        /** File Keys */
        static final String SH_PREF_KEY_CHOSEN_RECIPE_INDEX = "SH_PREF_KEY_CHOSEN_RECIPE_INDEX";

        /** File Default Values */
        static final int SH_PREF_VALUE_CHOSEN_RECIPE_INDEX = AppWidgetManager.INVALID_APPWIDGET_ID;

    }

}
