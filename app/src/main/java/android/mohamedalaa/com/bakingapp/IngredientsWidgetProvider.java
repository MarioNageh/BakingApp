package android.mohamedalaa.com.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.services.IntentServiceWidgetHelper;
import android.mohamedalaa.com.bakingapp.services.WidgetServiceIngredients;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsMasterActivity;
import android.mohamedalaa.com.bakingapp.view.RecipeStepsMasterFragment;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import java.io.Serializable;
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
                                        int appWidgetId, Recipe recipe) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget_ingredients_list);
        if (recipe != null){
            remoteViews.setViewVisibility(R.id.recipeNameAndIngredientsLinearLayout,
                    View.VISIBLE);
            remoteViews.setTextViewText(R.id.headerTextView,
                    recipe.getName());

            Intent adapterIntent = new Intent(context, WidgetServiceIngredients.class);
            adapterIntent.setAction(String.valueOf(appWidgetId));
            remoteViews.setRemoteAdapter(R.id.listView, adapterIntent);

            Intent masterActivityIntent = new Intent(context, RecipeStepsMasterActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, masterActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            masterActivityIntent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_INGREDIENTS_LIST,
                    (Serializable) recipe.getIngredients());
            masterActivityIntent.putExtra(RecipeStepsMasterFragment.INTENT_KEY_STEPS_LIST,
                    (Serializable) recipe.getSteps());
            PendingIntent pendingIntentWithExtras = PendingIntent.getActivity(
                    context, 0, masterActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent changeRecipeInWidgetIntent = new Intent(
                    context, IngredientsWidgetConfigureActivity.class);
            changeRecipeInWidgetIntent.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent changeRecipeInWidgetPendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    changeRecipeInWidgetIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.headerTextView, pendingIntentWithExtras);
            remoteViews.setPendingIntentTemplate(R.id.listView, pendingIntent);
            remoteViews.setOnClickPendingIntent(R.id.settingsIconImageView,
                    changeRecipeInWidgetPendingIntent);
        }else {
            remoteViews.setViewVisibility(R.id.recipeNameAndIngredientsLinearLayout,
                    View.GONE);

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
            remoteViews.setOnClickPendingIntent(R.id.refreshButton,
                    pendingIntent);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        if (appWidgetIds.length == 0
                || (appWidgetIds.length == 1 && appWidgetIds[0] == AppWidgetManager.INVALID_APPWIDGET_ID)){
            return;
        }

        IntentServiceWidgetHelper.startActionUpdateIngredientsWidget(context);
    }

    public static void performUpdateOnAllIds(Context context, AppWidgetManager appWidgetManager,
                                             int[] appWidgetIds, List<Recipe> recipeList){
        if (recipeList == null){
            return;
        }

        if (appWidgetIds.length != recipeList.size()){
            return;
        }

        // There may be multiple widgets active, so update all of them
        for (int i=0; i<recipeList.size(); i++) {
            int appWidgetId = appWidgetIds[i];
            Recipe recipe = recipeList.get(i);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listView);
            updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
        }
    }

    public static void performUpdateOnOneId(Context context, AppWidgetManager appWidgetManager,
                                            int appWidgetId, Recipe recipe){
        updateAppWidget(context, appWidgetManager, appWidgetId, recipe);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

