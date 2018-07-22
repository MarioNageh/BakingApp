package android.mohamedalaa.com.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.services.IntentServiceWidgetHelper;
import android.mohamedalaa.com.bakingapp.services.WidgetServiceIngredients;
import android.mohamedalaa.com.bakingapp.view.MainActivity;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsMasterActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 *
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    /**
     * VIP Notes
     * 1- I could 've made min cells 2X2, but in my opinion that's better for user
     * 2- as by below approach user can have 2 widget of the app.
     * 3- one for ingredient list and can launch immediately that recipe, to view it's ingredients.
     * 4- and the other to immediately launch app, without the need of searching for it
     *      in System App drawer (Quick Access).
     */
    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId, List<Recipe> recipeList) {
        // Get current width to decide either
        //      App launcher widget OR ingredients list
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        RemoteViews remoteViews;
        if (width < 110) {
            // Means less than 2 cells, got from official documentation
            // link -> https://developer.android.com/guide/practices/ui_guidelines/widget_design
            remoteViews = getLauncherRemoteView(context);
        } else {
            remoteViews = getIngredientsListRemoteView(context, recipeList);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        IntentServiceWidgetHelper.startActionUpdateIngredientsWidget(context);
    }

    public static void performUpdate(Context context, AppWidgetManager appWidgetManager,
                                     int[] appWidgetIds, List<Recipe> recipeList){
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeList);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        IntentServiceWidgetHelper.startActionUpdateIngredientsWidget(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // ---- Private Methods

    private static RemoteViews getLauncherRemoteView(Context context){
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.launcher_app_widget);
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(
                context, 0, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imageView, mainActivityPendingIntent);

        return remoteViews;
    }

    private static RemoteViews getIngredientsListRemoteView(Context context,
                                                            List<Recipe> recipeList){
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.ingredients_app_widget);

        if (recipeList != null && recipeList.size() > 0){
            remoteViews.setViewVisibility(R.id.listView, View.VISIBLE);

            // Set list view adapter
            Intent intent = new Intent(context, WidgetServiceIngredients.class);
            remoteViews.setRemoteAdapter(R.id.listView, intent);

            // Set list view pending intent template
            Intent appIntent = new Intent(context, RecipeStepsMasterActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(
                    context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.listView, appPendingIntent);
        }else {
            // occurs if application when created there was no internet connection, so no database
            // so no data at all so we set this as a fallback view ( poor internet connection )
            remoteViews.setViewVisibility(R.id.listView, View.GONE);

            Intent intent = IntentServiceWidgetHelper
                    .getIntentOfStartActionUpdateIngredientsWidget(context);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                pendingIntent = PendingIntent.getService(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }else {
                pendingIntent = PendingIntent.getBroadcast(
                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            remoteViews.setOnClickPendingIntent(R.id.refreshButton, pendingIntent);
        }

        return remoteViews;
    }

}

