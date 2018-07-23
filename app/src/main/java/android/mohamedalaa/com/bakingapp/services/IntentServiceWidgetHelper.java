package android.mohamedalaa.com.bakingapp.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.mohamedalaa.com.bakingapp.BaseApplication;
import android.mohamedalaa.com.bakingapp.DataRepository;
import android.mohamedalaa.com.bakingapp.IngredientsWidgetProvider;
import android.mohamedalaa.com.bakingapp.model.Recipe;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiClient;
import android.mohamedalaa.com.bakingapp.model.retrofit.RetrofitApiInterface;
import android.mohamedalaa.com.bakingapp.receivers.BroadcastReceiverUpdateWidget;
import android.mohamedalaa.com.bakingapp.utils.RecipeUtils;
import android.mohamedalaa.com.bakingapp.utils.SharedPrefUtils;
import android.mohamedalaa.com.bakingapp.utils.StringUtils;
import android.os.Build;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Trigger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by Mohamed on 7/21/2018.
 *
 * ==> Ingredients Widget Flow
 * 1- we changed updatePeriodMillis to zero, as we want the widget to be updated
 * only dynamically which is when
 *      A- Database insert completes it's work
 *      B- from it's own custom button, discussed below
 *
 * 2- flow when updated is as follows
 *      A- checks database for data, if no data found
 *      B- then it will check from internet, if no internet found it will put it's own custom
 *      button to make user retry when they think they have internet and this flow will be re-done.
 *
 */
public class IntentServiceWidgetHelper extends IntentService {

    private static final String UPDATE_INGREDIENTS_WIDGET = "UPDATE_INGREDIENTS_WIDGET";

    public IntentServiceWidgetHelper() {
        super("IntentServiceWidgetHelper");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null){
            return;
        }

        String action = intent.getAction();
        if (StringUtils.isNullOrEmpty(action)){
            return;
        }

        if (action.equals(UPDATE_INGREDIENTS_WIDGET)){
            handleActionUpdateIngredientsWidget();
        }
    }

    // ---- Private Static Methods ( to handle actions )

    private void handleActionUpdateIngredientsWidget(){
        // Get recipe list from
        // 1- database
        DataRepository dataRepository = ((BaseApplication) getApplication()).getRepository();
        List<Recipe> recipeList = dataRepository.getAllRecipeList();
        // 2- internet, if not in database
        if (recipeList == null || recipeList.size() == 0){
            Retrofit retrofitApiClient = RetrofitApiClient.getClient();
            RetrofitApiInterface retrofitApiInterface = retrofitApiClient
                    .create(RetrofitApiInterface.class);

            Call<List<Recipe>> call = retrofitApiInterface.getAllRecipes();
            try {
                Response<List<Recipe>> response = call.execute();

                if (response.isSuccessful()){
                    recipeList = response.body();
                    recipeList = RecipeUtils.fillImagesInsideRecipeList(recipeList);
                }
            } catch (IOException e) {
                // Maybe network failure.
            }
        }

        // Make widget update.
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, IngredientsWidgetProvider.class));
        if (recipeList == null || recipeList.size() == 0){
            IngredientsWidgetProvider.performUpdateOnAllIds(
                    this, appWidgetManager, appWidgetIds, null);
        }else {
            List<Recipe> associatedWithIdsRecipeList = new ArrayList<>();
            for (int appWidgetId : appWidgetIds){
                Timber.v("appWidgetId = " + appWidgetId);
                if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
                    continue;
                }
                int indexChosen = SharedPrefUtils.getWidgetChosenRecipeIndex(
                        appWidgetId, getApplicationContext());
                associatedWithIdsRecipeList.add(recipeList.get(indexChosen));
            }

            IngredientsWidgetProvider.performUpdateOnAllIds(
                    this, appWidgetManager, appWidgetIds, associatedWithIdsRecipeList);
        }
    }

    // ---- Public Static Methods ( to start actions here )

    public static Intent getIntentOfStartActionUpdateIngredientsWidget(Context context){
        Intent intent;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            intent = new Intent(context, IntentServiceWidgetHelper.class);
            intent.setAction(UPDATE_INGREDIENTS_WIDGET);
        }else {
            intent = new Intent(context, BroadcastReceiverUpdateWidget.class);
            intent.setAction("baking.app.update.widget");
        }

        return intent;
    }

    public static void startActionUpdateIngredientsWidget(Context context){
        Intent intent = new Intent(context, IntentServiceWidgetHelper.class);
        intent.setAction(UPDATE_INGREDIENTS_WIDGET);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            context.startService(intent);
        }else {
            // Made below alternative for android Oreo and above due to the background limitation
            // link -> https://developer.android.com/about/versions/oreo/background
            // because if not done it will make error on devices running Oreo and above
            Driver driver = new GooglePlayDriver(context.getApplicationContext());
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
            Job job = dispatcher.newJobBuilder()
                    .setService(JobServiceUpdateWidget.class)
                    .setTag(JobServiceUpdateWidget.SERVICE_GENERAL_TAG)
                    .setRecurring(false)
                    .setTrigger(Trigger.executionWindow(0, 0))
                    .build();
            dispatcher.mustSchedule(job);
        }
    }

}
